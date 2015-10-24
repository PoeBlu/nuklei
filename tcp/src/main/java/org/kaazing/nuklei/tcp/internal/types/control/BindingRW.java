/*
 * Copyright 2015, Kaazing Corporation. All rights reserved.
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

package org.kaazing.nuklei.tcp.internal.types.control;

import org.kaazing.nuklei.tcp.internal.types.StringRW;

import uk.co.real_logic.agrona.MutableDirectBuffer;

public final class BindingRW extends BindingType<MutableDirectBuffer>
{
    private final StringRW source = new StringRW();
    private final StringRW destination = new StringRW();
    private final AddressRW address = new AddressRW();

    public BindingRW wrap(MutableDirectBuffer buffer, int offset)
    {
        super.wrap(buffer, offset);

        return this;
    }

    public void wrap(MutableDirectBuffer buffer, int offset, BindingRO binding)
    {
        super.wrap(buffer, offset);

        buffer.putBytes(offset, binding.buffer(), binding.offset(), binding.remaining());

        this.source.wrap(buffer, offset + FIELD_OFFSET_SOURCE);
        this.destination.wrap(buffer, source.limit() + FIELD_SIZE_SOURCE_BINDING_REF);
        this.address.wrap(buffer, destination.limit());
    }

    @Override
    public StringRW source()
    {
        return source;
    }

    @Override
    public StringRW destination()
    {
        return destination;
    }

    @Override
    public AddressRW address()
    {
        return address;
    }
}
