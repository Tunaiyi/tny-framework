package com.tny.game.common.buff;

import java.io.IOException;

public class ByteBufferNode {

    public static final int DEFAULT_INIT_BUFFER_SIZE = 128;

    private byte[] buffer;

    private final int start;

    private int offset;

    private int limitLength = -1;

    private ByteBufferNode next;

    public static ByteBufferNode newBuffer(int size) {
        return new ByteBufferNode(size);
    }

    public static ByteBufferNode cutBuffer(ByteBufferNode node) {
        if (node.isSealed() || node.remainSize() == 0)
            return null;
        ByteBufferNode cutOne = new ByteBufferNode(node.buffer, node.offset, node.offset, node.limitLength);
        node.seal();
        return cutOne;
    }

    public static ByteBufferNode copyBuffer(ByteBufferNode node) {
        ByteBufferNode copyOne = new ByteBufferNode(node);
        //		copyOne.next = node.next;
        ByteBufferNode copyTail = copyOne;
        ByteBufferNode next = node.next;
        while (next != null) {
            copyTail = copyTail.next = new ByteBufferNode(next);
            next = next.next;
        }
        return copyOne;
    }

    public static ByteBufferNode wrapBuffer(byte[] buffer, int offset) {
        return wrapBuffer(buffer, 0, offset);
    }

    public static ByteBufferNode wrapBuffer(byte[] buffer, int start, int offset) {
        return new ByteBufferNode(buffer, start, offset);
    }

    private ByteBufferNode(ByteBufferNode node) {
        this(node.buffer, node.start, node.offset, node.limitLength);
    }

    private ByteBufferNode(int size) {
        this(new byte[size <= 0 ? DEFAULT_INIT_BUFFER_SIZE : size], 0, 0);
    }

    private ByteBufferNode(byte[] buffer, int start, int offset) {
        this(buffer, start, offset, -1, null);
    }

    private ByteBufferNode(byte[] buffer, int start, int offset, int limitLength) {
        this(buffer, start, offset, limitLength, null);
    }

    private ByteBufferNode(byte[] buffer, int start, int offset, int limitLength, ByteBufferNode next) {
        this.buffer = buffer;
        this.start = start;
        this.offset = offset;
        this.limitLength = limitLength;
        this.next = next;
    }

    public int getStart() {
        return this.start;
    }

    public int getOffset() {
        return this.offset;
    }

    protected ByteBufferNode getNext() {
        return this.next;
    }

    protected void setNext(ByteBufferNode next) {
        this.next = next;
    }

    protected byte[] getBuffer() {
        return this.buffer;
    }

    public int length() {
        if (this.limitLength >= 0)
            return this.limitLength;
        return this.offset - this.start;
    }

    public int remainSize() {
        return Math.max(this.buffer.length - this.offset, 0);
    }

    public boolean isSealed() {
        return this.limitLength >= 0;
    }

    private void seal() {
        this.limitLength = this.offset - this.start;
    }

    //	public ByteBufferNode wrapRemainBuffer() {
    //		if (remainSize() <= 0)
    //			return null;
    //		return new ByteBufferNode(this);
    //	}

    public int write(byte value) throws IOException {
        if (this.isSealed())
            throw new IOException("bufferNode is sealed!");
        this.buffer[this.offset] = value;
        this.offset++;
        return 1;
    }

    public int write(byte[] data) throws IOException {
        if (this.isSealed())
            throw new IOException("bufferNode is sealed!");
        System.arraycopy(data, 0, this.buffer, this.offset, data.length);
        this.offset += data.length;
        return data.length;
    }

    public int write(byte[] data, int start, int length) throws IOException {
        if (this.isSealed())
            throw new IOException("bufferNode is sealed!");
        System.arraycopy(data, start, this.buffer, this.offset, length);
        this.offset += data.length;
        return data.length;
    }

    public boolean hasNext() {
        return this.next != null;
    }

    public static String toString(byte[] a, int start, int offset) {
        if (a == null)
            return "null";
        if (start == offset)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = start; ; i++) {
            b.append(a[i]);
            if (i == offset - 1)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    @Override
    public String toString() {
        return toString(this.buffer, this.start, this.offset);
    }

}
