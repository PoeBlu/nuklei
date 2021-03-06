/**
 * Copyright 2007-2016, Kaazing Corporation. All rights reserved.
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
package org.kaazing.nuklei.http.internal;

import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteOrder.nativeOrder;

import org.kaazing.nuklei.http.internal.layouts.StreamsLayout;
import org.kaazing.nuklei.http.internal.types.stream.BeginFW;
import org.kaazing.nuklei.http.internal.types.stream.DataFW;
import org.kaazing.nuklei.http.internal.types.stream.EndFW;
import org.kaazing.nuklei.http.internal.types.stream.HttpBeginFW;
import org.kaazing.nuklei.http.internal.types.stream.HttpDataFW;
import org.kaazing.nuklei.http.internal.types.stream.HttpEndFW;

import uk.co.real_logic.agrona.DirectBuffer;
import uk.co.real_logic.agrona.concurrent.AtomicBuffer;
import uk.co.real_logic.agrona.concurrent.MessageHandler;
import uk.co.real_logic.agrona.concurrent.UnsafeBuffer;
import uk.co.real_logic.agrona.concurrent.ringbuffer.RingBuffer;

public final class HttpStreams
{
    private static final int MAX_SEND_LENGTH = 1024; // TODO: Configuration and Context

    private final BeginFW.Builder beginRW = new BeginFW.Builder();
    private final DataFW.Builder dataRW = new DataFW.Builder();
    private final EndFW.Builder endRW = new EndFW.Builder();

    private final HttpBeginFW.Builder httpBeginRW = new HttpBeginFW.Builder();
    private final HttpDataFW.Builder httpDataRW = new HttpDataFW.Builder();
    private final HttpEndFW.Builder httpEndRW = new HttpEndFW.Builder();

    private final StreamsLayout captureStreams;
    private final StreamsLayout routeStreams;
    private final RingBuffer captureBuffer;
    private final RingBuffer routeBuffer;
    private final AtomicBuffer atomicBuffer;

    HttpStreams(
        Context context,
        String capture,
        String route)
    {
        this.captureStreams = new StreamsLayout.Builder().streamsCapacity(context.streamsBufferCapacity())
                                                         .streamsFile(context.captureStreamsFile().apply(capture))
                                                         .createFile(false)
                                                         .build();
        this.captureBuffer = this.captureStreams.buffer();

        this.routeStreams = new StreamsLayout.Builder().streamsCapacity(context.streamsBufferCapacity())
                                                       .streamsFile(context.routeStreamsFile().apply(route))
                                                       .createFile(false)
                                                       .build();
        this.routeBuffer = this.routeStreams.buffer();

        this.atomicBuffer = new UnsafeBuffer(allocateDirect(MAX_SEND_LENGTH).order(nativeOrder()));
    }

    public void close()
    {
        captureStreams.close();
        routeStreams.close();
    }

    public boolean begin(
        long streamId,
        long referenceId)
    {
        BeginFW beginRO = beginRW.wrap(atomicBuffer, 0, atomicBuffer.capacity())
                                 .streamId(streamId)
                                 .referenceId(referenceId)
                                 .build();

        return captureBuffer.write(beginRO.typeId(), beginRO.buffer(), beginRO.offset(), beginRO.length());
    }

    public boolean data(
        long streamId,
        DirectBuffer buffer,
        int offset,
        int length)
    {
        DataFW dataRO = dataRW.wrap(atomicBuffer, 0, atomicBuffer.capacity())
                              .streamId(streamId)
                              .payload(buffer, offset, length)
                              .build();

        return captureBuffer.write(dataRO.typeId(), dataRO.buffer(), dataRO.offset(), dataRO.length());
    }

    public boolean end(
        long streamId)
    {
        EndFW endRO = endRW.wrap(atomicBuffer, 0, atomicBuffer.capacity())
                           .streamId(streamId)
                           .build();

        return captureBuffer.write(endRO.typeId(), endRO.buffer(), endRO.offset(), endRO.length());
    }

    public boolean httpBegin(
        long streamId,
        long referenceId)
    {
        HttpBeginFW httpBegin = httpBeginRW.wrap(atomicBuffer, 0, atomicBuffer.capacity())
                                           .streamId(streamId)
                                           .referenceId(referenceId)
                                           .build();

        return captureBuffer.write(httpBegin.typeId(), httpBegin.buffer(), httpBegin.offset(), httpBegin.length());
    }

    public boolean httpData(
        long streamId,
        DirectBuffer buffer,
        int offset,
        int length)
    {
        HttpDataFW data = httpDataRW.wrap(atomicBuffer, 0, atomicBuffer.capacity())
                                    .streamId(streamId)
                                    .payload(buffer, offset, length)
                                    .build();

        return captureBuffer.write(data.typeId(), data.buffer(), data.offset(), data.length());
    }

    public boolean httpEnd(
        long streamId)
    {
        HttpEndFW end = httpEndRW.wrap(atomicBuffer, 0, atomicBuffer.capacity())
                                 .streamId(streamId)
                                 .build();

        return captureBuffer.write(end.typeId(), end.buffer(), end.offset(), end.length());
    }

    public int read(MessageHandler handler)
    {
        return routeBuffer.read(handler);
    }
}
