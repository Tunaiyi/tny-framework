/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.buff;

import com.tny.game.common.buff.exception.*;
import org.slf4j.*;

import java.io.*;
import java.nio.ByteBuffer;

import static com.tny.game.common.utils.StringAide.*;

public class LinkedBuffer {

    public static final Logger LOGGER = LoggerFactory.getLogger(LinkedBuffer.class);

    public static final int DEFAULT_BUFFER_SIZE = 256;

    public static final float DEFAULT_BUFFER_GROWTH_FACTOR = 1.5F;

    private LinkedBufferNode head;

    private LinkedBufferNode tail;

    private ByteBufferAllocator allocator = new NioByteBufferAllocator();

    private int size = 0;

    private int initSize;

    private int nextSize;

    private float growthFactor;

    public LinkedBuffer(ByteBufferAllocator allocator) {
        this(allocator, DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_GROWTH_FACTOR);
    }

    public LinkedBuffer() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public LinkedBuffer(byte[] bytes) {
        init(LinkedBufferNode.wrapBytes(bytes, 0, 0), DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_GROWTH_FACTOR);
    }

    public LinkedBuffer(byte[] bytes, int offset, int position) {
        init(LinkedBufferNode.wrapBytes(bytes, offset, position), DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_GROWTH_FACTOR);
    }

    public LinkedBuffer(byte[] bytes, int offset, int position, int limit) {
        init(LinkedBufferNode.wrapBytes(bytes, offset, position, limit), DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_GROWTH_FACTOR);
    }

    public LinkedBuffer(int initSize) {
        this(initSize, DEFAULT_BUFFER_GROWTH_FACTOR);
    }

    public LinkedBuffer(int initSize, float growthFactor) {
        ByteBuffer buffer = allocator.alloc(initSize);
        init(LinkedBufferNode.wrapBuffer(buffer), initSize, growthFactor);
    }

    public LinkedBuffer(ByteBufferAllocator allocator, int initSize, float growthFactor) {
        this.allocator = allocator;
        ByteBuffer buffer = allocator.alloc(initSize);
        init(LinkedBufferNode.wrapBuffer(buffer), initSize, growthFactor);
    }

    public LinkedBuffer(LinkedBufferNode head) {
        init(head, DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_GROWTH_FACTOR);
    }

    private void init(LinkedBufferNode head, int initSize, float growthFactor) {
        if (head == null) {
            throw new NullPointerException("head is null");
        }
        this.tail = head;
        this.head = head;
        this.initSize = initSize;
        this.growthFactor = growthFactor;
        if (this.growthFactor <= 0.0F) {
            this.growthFactor = DEFAULT_BUFFER_GROWTH_FACTOR;
        }
        this.nextSize = (int)(this.initSize * this.growthFactor);
    }

    public void clear() {
        allocator.release();
        this.head = LinkedBufferNode.wrapBuffer(allocator.alloc(initSize));
        this.tail = this.head;
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public LinkedBufferNode getHead() {
        return this.head;
    }

    public LinkedBufferNode getTail() {
        return this.tail;
    }

    public LinkedBuffer write(byte value) {
        if (this.tail.remaining() <= 0) {
            growth();
        }
        try {
            this.size += this.tail.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public LinkedBuffer write(byte[] data, int offset, int length) {
        if (offset + length > data.length) {
            throw new IllegalArgumentException(format(
                    "offset {} + length {} is {} > data.length", offset, length, offset + length));
        }
        try {
            if (this.tail.remaining() > length) {
                this.size += this.tail.write(data, offset, length);
            } else {
                LinkedBufferNode remainNode = this.tail.slice();
                LinkedBufferNode newNode = newNode(length);
                newNode.write(data, offset, length);
                if (remainNode != null) {
                    newNode.setNext(remainNode);
                }
                this.tail.setNext(newNode);
                this.moveTail();
                this.size += newNode.getPosition();
            }
        } catch (IOException e) {
            throw new LinkBufferException("write {} byte of bytes exception", length);
        }
        return this;
    }

    public LinkedBuffer write(byte[] data) {
        return this.write(data, 0, data.length);
    }

    public LinkedBuffer append(byte[] data) {
        this.append(data, 0, data.length, data.length);
        return this;
    }

    public LinkedBuffer append(byte[] data, int offset, int length) {
        return append(data, offset, 0, length);
    }

    public LinkedBuffer append(byte[] data, int offset, int position, int length) {
        LinkedBufferNode remainNode = this.tail.slice();
        LinkedBufferNode newNode = LinkedBufferNode.wrapBytes(data, offset, position, length);
        if (remainNode != null) {
            newNode.setNext(remainNode);
        }
        this.tail.setNext(newNode);
        this.moveTail();
        this.size += newNode.getPosition();
        return this;
    }

    public LinkedBuffer append(LinkedBuffer other) {
        LinkedBufferNode otherHead = other.getHead();
        this.tail.setNext(otherHead);
        this.moveTail();
        this.size += other.size();
        return this;
    }

    public final byte[] toByteArray() {
        LinkedBufferNode node = this.head;
        int offset = 0;
        int length = 0;
        final byte[] target = new byte[this.size];
        do {
            if ((length = node.getPosition()) > 0) {
                ByteBuffer nodeBuffer = node.getBuffer();
                System.arraycopy(nodeBuffer.array(), nodeBuffer.arrayOffset(), target, offset, length);
                offset += length;
            }
        } while ((node = node.getNext()) != null);
        return target;
    }

    public void toBuffer(ByteBuffer buffer) {
        LinkedBufferNode node = this.head;
        int length;
        do {
            if ((length = node.getPosition()) > 0) {
                ByteBuffer nodeBuffer = node.getBuffer();
                buffer.put(nodeBuffer.array(), nodeBuffer.arrayOffset(), length);
            }
        } while ((node = node.getNext()) != null);
    }

    public void toBuffer(OutputStream output) throws IOException {
        LinkedBufferNode node = this.head;
        do {
            int length;
            if ((length = node.getPosition()) > 0) {
                ByteBuffer nodeBuffer = node.getBuffer();
                output.write(nodeBuffer.array(), nodeBuffer.arrayOffset(), length);
            }
        } while ((node = node.getNext()) != null);
    }

    private void moveTail() {
        while (this.tail.hasNext()) {
            this.tail = this.tail.getNext();
        }
    }

    private void growth() {
        this.growth(this.nextSize);
        this.nextSize = (int)(this.initSize * this.growthFactor);
    }

    private void growth(int size) {
        this.tail.setNext(newNode(size));
        this.moveTail();
    }

    private LinkedBufferNode newNode(int size) {
        return LinkedBufferNode.wrapBuffer(allocator.alloc(size));
    }

    public LinkedBuffer slice() {
        LinkedBufferNode remainNode = this.getTail().slice();
        LinkedBuffer sliceBuffer;
        if (remainNode == null) {
            sliceBuffer = new LinkedBuffer(this.allocator, this.initSize, this.growthFactor);
        } else {
            sliceBuffer = new LinkedBuffer(remainNode);
        }
        return sliceBuffer;
    }

    public void release() {
        this.head = null;
        this.tail = null;
        this.allocator.release();
    }

}
