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

import java.io.IOException;
import java.nio.ByteBuffer;

public class LinkedBufferNode {

    public static final int DEFAULT_INIT_BUFFER_SIZE = 128;

    private final ByteBuffer buffer;

    private LinkedBufferNode next;

    private boolean sealed = false;

    public static LinkedBufferNode wrapBuffer(ByteBuffer buffer) {
        return new LinkedBufferNode(buffer, null);
    }

    public static LinkedBufferNode wrapBytes(byte[] buffer, int offset, int position) {
        return new LinkedBufferNode(buffer, offset, position);
    }

    public static LinkedBufferNode wrapBytes(byte[] buffer, int offset, int position, int limit) {
        return new LinkedBufferNode(buffer, offset, position, limit);
    }

    private LinkedBufferNode(byte[] buffer, int offset, int position) {
        this(buffer, offset, position, buffer.length - offset, null);
    }

    private LinkedBufferNode(byte[] buffer, int offset, int position, int length) {
        this(buffer, offset, position, length, null);
    }

    private LinkedBufferNode(byte[] buffer, int offset, int position, int length, LinkedBufferNode next) {
        this.buffer = ByteBuffer.wrap(buffer, offset, length);
        this.buffer.position(position);
        this.next = next;
    }

    private LinkedBufferNode(ByteBuffer buffer, LinkedBufferNode next) {
        this.buffer = buffer;
        this.next = next;
    }

    public int getOffset() {
        return this.buffer.arrayOffset();
    }

    public int getPosition() {
        return this.buffer.position();
    }

    protected LinkedBufferNode getNext() {
        return this.next;
    }

    protected void setNext(LinkedBufferNode next) {
        this.next = next;
    }

    protected ByteBuffer getBuffer() {
        return this.buffer;
    }

    //	public int length() {
    //		if (this.buffer.limit() >= 0) {
    //			return this.limitLength;
    //		}
    //		return this.position - this.start;
    //	}

    public int remaining() {
        return this.buffer.remaining();
    }

    public boolean isSealed() {
        return this.sealed;
    }

    //	public ByteBufferNode wrapRemainBuffer() {
    //		if (remainSize() <= 0)
    //			return null;
    //		return new ByteBufferNode(this);
    //	}

    public int write(byte value) throws IOException {
        if (this.isSealed()) {
            throw new IOException("bufferNode is sealed!");
        }
        this.buffer.put(value);
        return 1;
    }

    public int write(byte[] data) throws IOException {
        if (this.isSealed()) {
            throw new IOException("bufferNode is sealed!");
        }
        this.buffer.put(data);
        return data.length;
    }

    public int write(byte[] data, int offset, int length) throws IOException {
        if (this.isSealed()) {
            throw new IOException("bufferNode is sealed!");
        }
        this.buffer.put(data, offset, length);
        return data.length;
    }

    public boolean hasNext() {
        return this.next != null;
    }

    public static String toString(byte[] a, int offset, int length) {
        if (a == null) {
            return "null";
        }
        if (length == 0) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; i < length; i++) {
            b.append(a[offset + i]);
            b.append(", ");
        }
        return b.append(']').toString();
    }

    /**
     * 把剩余的空间剪切. 用于创建新的 Node, 并返回创建的新 Node
     *
     * @return 返回裁剪创建的 node
     */
    public LinkedBufferNode slice() {
        if (this.isSealed() || this.remaining() == 0) {
            return null;
        }
        ByteBuffer cut = this.buffer.slice();
        LinkedBufferNode cutOne = wrapBuffer(cut);
        this.seal();
        return cutOne;
    }

    private void seal() {
        this.buffer.limit(this.getPosition());
        this.sealed = true;
    }

    @Override
    public String toString() {
        return toString(this.buffer.array(), this.getOffset(), this.getPosition());
    }

}
