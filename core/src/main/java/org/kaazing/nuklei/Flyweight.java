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
package org.kaazing.nuklei;

import java.nio.ByteOrder;
import java.util.function.Consumer;

import org.kaazing.nuklei.concurrent.AtomicBuffer;

/**
 * Encapsulation of basic field operations and flyweight usage pattern
 *
 * All flyweights are intended to be direct subclasses.
 */
public class Flyweight
{
    private final ByteOrder byteOrder;
    private int offset;
    private AtomicBuffer buffer;
    private Consumer<Flyweight> observer = (owner) -> {};  // NOP

    /**
     * Construct a flyweight with a given byte order assumed
     *
     * @param byteOrder of the entire flyweight
     */
    public Flyweight(final ByteOrder byteOrder)
    {
        this.byteOrder = byteOrder;  // byte order is a function of the Flyweight
        this.offset = 0;
    }

    protected ByteOrder byteOrder()
    {
        return byteOrder;
    }

    public int offset()
    {
        return offset;
    }

    public int limit()
    {
        return offset;
    }

    public AtomicBuffer buffer()
    {
        return buffer;
    }

    /**
     * Wrap a flyweight to use a specific buffer starting at a given offset.
     *
     * @param buffer to use
     * @param offset to start at
     * @return flyweight
     */
    public Flyweight wrap(final AtomicBuffer buffer, final int offset)
    {
        this.buffer = buffer;
        this.offset = offset;
        return this;
    }

    public Flyweight watch(Consumer<Flyweight> observer)
    {
        this.observer = observer;
        return this;
    }

    public final void notifyChanged()
    {
        observer.accept(this);
    }

    /**
     * Return the 32-bit field at a given location as an float.
     *
     * @param buffer to read from
     * @param offset to read from
     * @param byteOrder to decode with
     * @return float representation of the 32-bit field
     */
    public static float floatGet(final AtomicBuffer buffer, final int offset, final ByteOrder byteOrder)
    {
        return buffer.getFloat(offset, byteOrder);
    }

    /**
     * Encode the given float value as a 32-bit field at the given location.
     *
     * @param buffer to write from
     * @param offset to write at
     * @param byteOrder to encode with
     * @param value to encode represented as a 32-bit field
     */
    public static void floatPut(
        final AtomicBuffer buffer, final int offset, final float value, final ByteOrder byteOrder)
    {
        buffer.putFloat(offset, value, byteOrder);
    }

    /**
     * Return the 64-bit field at a given location as an double.
     *
     * @param buffer to read from
     * @param offset to read from
     * @param byteOrder to decode with
     * @return double representation of the 64-bit field
     */
    public static double doubleGet(final AtomicBuffer buffer, final int offset, final ByteOrder byteOrder)
    {
        return buffer.getDouble(offset, byteOrder);
    }

    /**
     * Encode the given double value as a 64-bit field at the given location.
     *
     * @param buffer to write from
     * @param offset to write at
     * @param byteOrder to encode with
     * @param value to encode represented as a 64-bit field
     */
    public static void doublePut(
        final AtomicBuffer buffer, final int offset, final double value, final ByteOrder byteOrder)
    {
        buffer.putDouble(offset, value, byteOrder);
    }

    /**
     * Return the 8-bit field at a given location as an unsigned integer.
     *
     * @param buffer to read from
     * @param offset to read from
     * @return short representation of the 8-bit unsigned value
     */
    public static short uint8Get(final AtomicBuffer buffer, final int offset)
    {
        return (short)(buffer.getByte(offset) & 0xFF);
    }

    /**
     * Encode a given value as an 8-bit unsigned integer at a given location.
     *
     * @param buffer to write to
     * @param offset to write at
     * @param value to encode represented as a short
     */
    public static void uint8Put(final AtomicBuffer buffer, final int offset, final short value)
    {
        buffer.putByte(offset, (byte)value);
    }

    /**
     * Return the 8-bit field at a given location as a signed integer.
     *
     * @param buffer to read from
     * @param offset to read from
     * @return byte representation of the 8-bit signed value
     */
    public static byte int8Get(final AtomicBuffer buffer, final int offset)
    {
        return buffer.getByte(offset);
    }

    /**
     * Encode a given value as an 8-bit signed integer at a given location.
     *
     * @param buffer to write to
     * @param offset to write at
     * @param value to encode represented as a byte
     */
    public static void int8Put(final AtomicBuffer buffer, final int offset, final byte value)
    {
        buffer.putByte(offset, value);
    }

    /**
     * Return the 16-bit field at a given location as an unsigned integer.
     *
     * @param buffer to read from
     * @param offset to read from
     * @param byteOrder to decode with
     * @return int representation of the 16-bit signed value
     */
    public static int uint16Get(final AtomicBuffer buffer, final int offset, final ByteOrder byteOrder)
    {
        return (int)(buffer.getShort(offset, byteOrder) & 0xFFFF);
    }

    /**
     * Encode a given value as an 16-bit unsigned integer at a given location.
     *
     * @param buffer to write to
     * @param offset to write at
     * @param value to encode represented as an int
     * @param byteOrder to encode with
     */
    public static void uint16Put(final AtomicBuffer buffer, final int offset, final int value,
                                 final ByteOrder byteOrder)
    {
        buffer.putShort(offset, (short)value, byteOrder);
    }

    /**
     * Return the 16-bit field at a given location as a signed integer.
     *
     * @param buffer to read from
     * @param offset to read from
     * @param byteOrder to decode with
     * @return short representation of the 16-bit signed value
     */
    public static short int16Get(final AtomicBuffer buffer, final int offset, final ByteOrder byteOrder)
    {
        return buffer.getShort(offset, byteOrder);
    }

    /**
     * Encode a given value as an 16-bit signed integer at a given location.
     *
     * @param buffer to write to
     * @param offset to write at
     * @param value to encode represented as a short
     * @param byteOrder to encode with
     */
    public static void int16Put(final AtomicBuffer buffer, final int offset, final short value,
                                final ByteOrder byteOrder)
    {
        buffer.putShort(offset, value, byteOrder);
    }

    /**
     * Return the 32-bit field at a given location as an unsigned integer.
     *
     * @param buffer to read from
     * @param offset to read from
     * @param byteOrder to decode with
     * @return long representation of the 32-bit signed value
     */
    public static long uint32Get(final AtomicBuffer buffer, final int offset, final ByteOrder byteOrder)
    {
        return (long)(buffer.getInt(offset, byteOrder) & 0xFFFFFFFFL);
    }

    /**
     * Encode a given value as an 32-bit unsigned integer at a given location.
     *
     * @param buffer to write to
     * @param offset to write at
     * @param value to encode represented as an long
     * @param byteOrder to encode with
     */
    public static void uint32Put(final AtomicBuffer buffer, final int offset, final long value,
                                 final ByteOrder byteOrder)
    {
        buffer.putInt(offset, (int)value, byteOrder);
    }

    /**
     * Return the 32-bit field at a given location as a signed integer.
     *
     * @param buffer to read from
     * @param offset to read from
     * @param byteOrder to decode with
     * @return int representation of the 32-bit signed value
     */
    public static int int32Get(final AtomicBuffer buffer, final int offset, final ByteOrder byteOrder)
    {
        return buffer.getInt(offset, byteOrder);
    }

    /**
     * Encode a given value as an 32-bit signed integer at a given location.
     *
     * @param buffer to write to
     * @param offset to write at
     * @param value to encode represented as a int
     * @param byteOrder to encode with
     */
    public static void int32Put(final AtomicBuffer buffer, final int offset, final int value,
                                final ByteOrder byteOrder)
    {
        buffer.putInt(offset, value, byteOrder);
    }

    /**
     * Return the 64-bit field at a given location as a signed integer.
     *
     * @param buffer to read from
     * @param offset to read from
     * @param byteOrder to decode with
     * @return long representation of the 64-bit signed value
     */
    public static long int64Get(final AtomicBuffer buffer, final int offset, final ByteOrder byteOrder)
    {
        return buffer.getLong(offset, byteOrder);
    }

    /**
     * Encode a given value as an 64-bit signed integer at a given location.
     *
     * @param buffer to write to
     * @param offset to write at
     * @param byteOrder to encode with
     * @param value to encode represented as a long
     */
    public static void int64Put(final AtomicBuffer buffer, final int offset, final long value,
                                final ByteOrder byteOrder)
    {
        buffer.putLong(offset, value, byteOrder);
    }

    /**
     * Is a bit set at a given index.
     *
     * @param buffer to read from.
     * @param offset of the beginning byte
     * @param bitIndex bit index to read
     * @return true if the bit is set otherwise false.
     */
    public static boolean bitSet(final AtomicBuffer buffer, final int offset, final int bitIndex)
    {
        return 0 != (buffer.getByte(offset) & (1 << bitIndex));
    }

    /**
     * Set a bit on or off at a given index.
     *
     * @param buffer to write the bit too.
     * @param offset of the beginning byte.
     * @param bitIndex bit index to set.
     * @param switchOn true sets bit to 1 and false sets it to 0.
     */
    public static void bitSet(final AtomicBuffer buffer, final int offset, final int bitIndex, final boolean switchOn)
    {
        byte bits = buffer.getByte(offset);
        bits = (byte)((switchOn ? bits | (1 << bitIndex) : bits & ~(1 << bitIndex)));
        buffer.putByte(offset, bits);
    }
}
