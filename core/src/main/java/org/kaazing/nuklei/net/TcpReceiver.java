/*
 * Copyright 2014 Kaazing Corporation, All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaazing.nuklei.net;

import org.kaazing.nuklei.MessagingNukleus;
import org.kaazing.nuklei.NioSelectorNukleus;
import org.kaazing.nuklei.Nuklei;
import org.kaazing.nuklei.concurrent.MpscArrayBuffer;
import org.kaazing.nuklei.net.command.TcpCloseConnectionCmd;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class TcpReceiver
{
    private static final int MPSC_READ_LIMIT = 10;

    private final MessagingNukleus messagingNukleus;
    private final NioSelectorNukleus selectorNukleus;
    private final Map<Long, TcpConnection> connectionsByIdMap;
    private final MpscArrayBuffer<Object> tcpManagerCommandQueue;
    private final MpscArrayBuffer<Object> tcpSenderCommandQueue;

    public TcpReceiver(
        final MpscArrayBuffer<Object> commandQueue,
        final NioSelectorNukleus selectorNukleus,
        final MpscArrayBuffer<Object> tcpManagerCommandQueue,
        final MpscArrayBuffer<Object> tcpSenderCommandQueue)
    {
        final MessagingNukleus.Builder builder = new MessagingNukleus.Builder()
            .nioSelector(selectorNukleus)
            .mpscArrayBuffer(commandQueue, this::commandHandler, MPSC_READ_LIMIT);

        this.selectorNukleus = selectorNukleus;
        this.tcpManagerCommandQueue = tcpManagerCommandQueue;
        this.tcpSenderCommandQueue = tcpSenderCommandQueue;

        messagingNukleus = new MessagingNukleus(builder);
        connectionsByIdMap = new HashMap<>();
    }

    public void launch(final Nuklei nuklei)
    {
        nuklei.spinUp(messagingNukleus);
    }

    private void commandHandler(final Object obj)
    {
        if (obj instanceof TcpConnection)
        {
            final TcpConnection connection = (TcpConnection)obj;

            try
            {
                selectorNukleus.register(connection.channel(), SelectionKey.OP_READ, connection::onReadable);
                connectionsByIdMap.put(connection.id(), connection);

                connection.informOfNewConnection();
            }
            catch (final Exception ex)
            {
                ex.printStackTrace(); // TODO: temp
            }
        }
        else if (obj instanceof TcpCloseConnectionCmd)
        {
            final TcpCloseConnectionCmd cmd = (TcpCloseConnectionCmd) obj;
            final TcpConnection connection = connectionsByIdMap.remove(cmd.connectionId());

            if (null != connection)
            {
                selectorNukleus.cancel(connection.channel(), SelectionKey.OP_READ);
                connection.receiverClosed();
                informTcpManagerOfClose(connection);
            }
        }
    }

    private void informTcpManagerOfClose(final TcpConnection connection)
    {
        if (!tcpManagerCommandQueue.write(connection))
        {
            throw new IllegalStateException("could not write to command queue");
        }
    }
}
