/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
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
package org.kaazing.nuklei;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DedicatedNuklei is a thread that holds a group of Nukleus'
 *
 * Used for manual assignment of Nukleus' to Nuklei
 */
public class DedicatedNuklei implements Nuklei, Runnable
{
    private final Thread thread;
    private final AtomicReference<Nukleus[]> nukleusArrayRef;
    private final Idler idler;

    private volatile boolean done;

    public DedicatedNuklei(final String name)
    {
        this(name, new SpinYieldParkBackoffIdler());
    }

    public DedicatedNuklei(final String name, final Idler idler)
    {
        thread = new Thread(this);
        nukleusArrayRef = new AtomicReference<>();
        this.idler = idler;

        final Nukleus[] initialArray = new Nukleus[0];

        thread.setName(name);
        nukleusArrayRef.set(initialArray);
        done = false;
        thread.start();
    }

    public void done(final boolean value)
    {
        done = value;
    }

    public void stop()
    {
        done = true;

        do
        {
            try
            {
                thread.join(100);

                if (!thread.isAlive())
                {
                    break;
                }
            }
            catch (final Exception ex)
            {
                System.err.println("Dedicated nuklei <" + thread.getName() + "> exception. Retrying...");
                thread.interrupt();
            }
        }
        while (true);
    }

    public void spinUp(final Nukleus nukleus)
    {
        Nukleus[] oldArray = nukleusArrayRef.get();
        Nukleus[] newArray = Arrays.copyOf(oldArray, oldArray.length + 1);

        newArray[oldArray.length] = nukleus;

        nukleusArrayRef.lazySet(newArray);
    }

    public void run()
    {
        while (!done)
        {
            final Nukleus[] nuklei = nukleusArrayRef.get();
            int weight = 0;

            for (final Nukleus nukleus: nuklei)
            {
                weight += nukleus.process();

                if (done)
                {
                    return;
                }
            }

            idler.idle(weight);
        }
    }

}
