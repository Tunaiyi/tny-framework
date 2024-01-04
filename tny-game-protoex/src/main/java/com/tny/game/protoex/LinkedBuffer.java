/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.protoex;

import java.io.*;

/**
 * A buffer that wraps a byte array and has a reference to the next buffer for
 * dynamic increase.
 *
 * @author David Yu
 * @created May 18, 2010
 */
public final class LinkedBuffer {

    /**
     * The minimum buffer size for a {@link LinkedBuffer}.
     */
    public static final int MIN_BUFFER_SIZE = 256;

    /**
     * The default buffer size for a {@link LinkedBuffer}.
     */
    public static final int DEFAULT_BUFFER_SIZE = 512;

    /**
     * Allocates a new buffer with the specified size.
     */
    public static LinkedBuffer allocate(int size) {
        //		if (size < MIN_BUFFER_SIZE)
        //			throw new IllegalArgumentException(MIN_BUFFER_SIZE + " is the minimum buffer size.");

        return new LinkedBuffer(size);
    }

    /**
     * Allocates a new buffer with the specified size and appends it to the
     * previous buffer.
     */
    public static LinkedBuffer allocate(int size, LinkedBuffer previous) {
        if (size < MIN_BUFFER_SIZE) {
            throw new IllegalArgumentException(MIN_BUFFER_SIZE + " is the minimum buffer size.");
        }

        return new LinkedBuffer(size, previous);
    }

    /**
     * Wraps the byte array buffer as a read-only buffer.
     */
    public static LinkedBuffer wrap(byte[] array, int offset, int length) {
        return new LinkedBuffer(array, offset, offset + length);
    }

    /**
     * Uses the existing byte array as the internal buffer.
     */
    public static LinkedBuffer use(byte[] buffer) {
        return use(buffer, 0);
    }

    /**
     * Uses the existing byte array as the internal buffer.
     */
    public static LinkedBuffer use(byte[] buffer, int start) {
        assert start >= 0;
        if (buffer.length - start < MIN_BUFFER_SIZE) {
            throw new IllegalArgumentException(MIN_BUFFER_SIZE + " is the minimum buffer size.");
        }

        return new LinkedBuffer(buffer, start, start);
    }

    /**
     * Writes the contents of the {@link LinkedBuffer} into the
     * {@link OutputStream}.
     *
     * @return the total content size of the buffer.
     */
    public static int writeTo(final OutputStream out, LinkedBuffer node) throws IOException {
        int contentSize = 0, len;
        do {
            if ((len = node.offset - node.start) > 0) {
                out.write(node.buffer, node.start, len);
                contentSize += len;
            }
        } while ((node = node.next) != null);

        return contentSize;
    }

    /**
     * Writes the contents of the {@link LinkedBuffer} into the
     * {@link DataOutput}.
     *
     * @return the total content size of the buffer.
     */
    public static int writeTo(final DataOutput out, LinkedBuffer node) throws IOException {
        int contentSize = 0, len;
        do {
            if ((len = node.offset - node.start) > 0) {
                out.write(node.buffer, node.start, len);
                contentSize += len;
            }
        } while ((node = node.next) != null);

        return contentSize;
    }

    final byte[] buffer;

    final int start;

    int offset;

    LinkedBuffer next;

    /**
     * Creates a buffer with the specified {@code size}.
     */
    LinkedBuffer(int size) {
        this(new byte[size], 0, 0);
    }

    /**
     * Creates a buffer with the specified {@code size} and appends to the
     * provided buffer {@code appendTarget}.
     */
    LinkedBuffer(int size, LinkedBuffer appendTarget) {
        this(new byte[size], 0, 0, appendTarget);
    }

    /**
     * Uses the buffer starting at the specified {@code offset}
     */
    LinkedBuffer(byte[] buffer, int offset) {
        this(buffer, offset, offset);
    }

    LinkedBuffer(byte[] buffer, int start, int offset) {
        this.buffer = buffer;
        this.start = start;
        this.offset = offset;
    }

    /**
     * Uses the buffer starting at the specified {@code offset} and appends to
     * the provided buffer {@code appendTarget}.
     */
    LinkedBuffer(byte[] buffer, int offset, LinkedBuffer appendTarget) {
        this(buffer, offset, offset);
        appendTarget.next = this;
    }

    LinkedBuffer(byte[] buffer, int start, int offset, LinkedBuffer appendTarget) {
        this(buffer, start, offset);
        appendTarget.next = this;
    }

    /**
     * Creates a view from the buffer {@code viewSource} and appends the view to
     * the provided buffer {@code appendTarget}.
     */
    LinkedBuffer(LinkedBuffer viewSource, LinkedBuffer appendTarget) {
        this.buffer = viewSource.buffer;
        this.offset = this.start = viewSource.offset;
        appendTarget.next = this;
    }

    /**
     * The offset will be reset to its starting position. The buffer next to
     * this will be dereferenced.
     */
    public LinkedBuffer clear() {
        this.next = null;
        this.offset = this.start;
        return this;
    }

}
