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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY ERROR_TYPE_ID, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaazing.nuklei.tcp.internal.types.stream;

import uk.co.real_logic.agrona.MutableDirectBuffer;

public final class BeginRW extends BeginType<MutableDirectBuffer>
{
    public BeginRW wrap(MutableDirectBuffer buffer, int offset)
    {
        super.wrap(buffer, offset);
        return this;
    }

    public BeginRW streamId(long streamId)
    {
        buffer().putLong(offset() + FIELD_OFFSET_STREAM_ID, streamId);
        return this;
    }

    public BeginRW bindingRef(long bindingRef)
    {
        buffer().putLong(offset() + FIELD_OFFSET_BINDING_REF, bindingRef);
        return this;
    }
}
