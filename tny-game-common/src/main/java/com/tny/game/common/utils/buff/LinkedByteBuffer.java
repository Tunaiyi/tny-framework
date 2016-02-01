package com.tny.game.common.utils.buff;

import java.io.IOException;

public class LinkedByteBuffer {

    public static final int DEFAULT_BUFFER_SIZE = 256;

    private ByteBufferNode head;

    private ByteBufferNode tail;

    private int size = 0;

    private int nextBufferSize;

    public LinkedByteBuffer() {
        this(ByteBufferNode.newBuffer(DEFAULT_BUFFER_SIZE), DEFAULT_BUFFER_SIZE);
    }

    public LinkedByteBuffer(byte[] bytes) {
        this(ByteBufferNode.wrapBuffer(bytes, 0), DEFAULT_BUFFER_SIZE);
    }

    public LinkedByteBuffer(int initSize) {
        this(ByteBufferNode.newBuffer(initSize), DEFAULT_BUFFER_SIZE);
    }

    public LinkedByteBuffer(int initSize, int nextSize) {
        this(ByteBufferNode.newBuffer(initSize), nextSize);
    }

    public LinkedByteBuffer(ByteBufferNode head) {
        this(head, DEFAULT_BUFFER_SIZE);
    }

    public LinkedByteBuffer(ByteBufferNode head, int nextBufferSize) {
        if (head == null)
            throw new NullPointerException("head is null");
        this.tail = head;
        this.head = head;
        this.nextBufferSize = nextBufferSize;
        if (this.nextBufferSize <= 0)
            this.nextBufferSize = DEFAULT_BUFFER_SIZE;
    }

    public void clear() {
        this.head = ByteBufferNode.wrapBuffer(this.head.getBuffer(), 0, 0);
        this.tail = this.head;
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public ByteBufferNode getHead() {
        return this.head;
    }

    public ByteBufferNode getTail() {
        return this.tail;
    }

    private void expandNode() {
        this.tail.setNext(ByteBufferNode.newBuffer(this.nextBufferSize));
        this.moveTail();
    }

    public LinkedByteBuffer write(byte value) {
        if (this.tail.remainSize() >= 1) {
            try {
                this.size += this.tail.write(value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.expandNode();
            this.write(value);
        }
        return this;
    }

    public LinkedByteBuffer write(byte[] data) {
        if (this.tail.remainSize() > data.length) {
            try {
                this.size += this.tail.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ByteBufferNode remainNode = ByteBufferNode.cutBuffer(this.tail);
            ByteBufferNode value = ByteBufferNode.wrapBuffer(data, data.length);
            if (remainNode != null)
                value.setNext(remainNode);
            this.tail.setNext(value);
            this.moveTail();
            this.size += value.length();
        }
        return this;
    }

    private void moveTail() {
        while (this.tail.hasNext()) {
            this.tail = this.tail.getNext();
        }
    }

    public LinkedByteBuffer appand(LinkedByteBuffer other) {
        ByteBufferNode otherHead = ByteBufferNode.copyBuffer(other.getHead());
        this.tail.setNext(otherHead);
        this.moveTail();
        this.size += other.size();
        return this;
    }

    public final byte[] toByteArray() {
        ByteBufferNode node = this.head;
        int offset = 0;
        int length = 0;
        final byte[] buf = new byte[this.size];
        do {
            if ((length = node.length()) > 0) {
                System.arraycopy(node.getBuffer(), node.getStart(), buf, offset, node.length());
                offset += length;
            }
        } while ((node = node.getNext()) != null);
        return buf;
    }

    public int getNextBufferSize() {
        return this.nextBufferSize;
    }

    public int getInitBufferSize() {
        return this.head.getBuffer().length;
    }

}
