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
package com.tny.game.common.buff;

import org.junit.jupiter.api.*;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LinkedBuffer 写入/追加/扩容契约（回归：窗口 write 虚报 data.length 字节数，
 * protoex 线格式用 size 写长度前缀导致报文损坏；三参 append 游标传 0 静默丢字节；
 * growth 扩容不复合；release 后 size 残留）。
 */
class LinkedBufferWriteTest {

    private static int nodeCount(LinkedBuffer buffer) {
        int count = 0;
        LinkedBufferNode node = buffer.getHead();
        while (node != null) {
            count++;
            node = node.getNext();
        }
        return count;
    }

    /** 窗口写只计 length 个字节（原返回 data.length，size 虚高） */
    @Test
    void windowWriteCountsLengthNotArraySize() throws Exception {
        LinkedBuffer buffer = new LinkedBuffer(1024);
        byte[] data = new byte[100];
        Arrays.fill(data, (byte) 0x7F);
        buffer.write(data, 0, 1);
        assertEquals(1, buffer.size(), "只写入 1 字节，size 必须为 1");
        assertArrayEquals(new byte[]{0x7F}, buffer.toByteArray());
    }

    /** 窗口写内容正确（offset 处切片） */
    @Test
    void windowWriteContentAtOffset() {
        LinkedBuffer buffer = new LinkedBuffer(64);
        byte[] data = {1, 2, 3, 4, 5, 6};
        buffer.write(data, 2, 3);
        assertEquals(3, buffer.size());
        assertArrayEquals(new byte[]{3, 4, 5}, buffer.toByteArray());
    }

    /** 三参 append 与单参等价（原 position 传 0，追加字节静默丢失） */
    @Test
    void appendWindowEqualsFullAppend() {
        byte[] data = {10, 20, 30, 40, 50};
        LinkedBuffer a = new LinkedBuffer(16);
        a.append(data, 0, 5);
        LinkedBuffer b = new LinkedBuffer(16);
        b.append(data);
        assertEquals(b.size(), a.size());
        assertArrayEquals(b.toByteArray(), a.toByteArray());
        // 非零偏移窗口
        LinkedBuffer c = new LinkedBuffer(16);
        c.append(data, 1, 3);
        assertEquals(3, c.size());
        assertArrayEquals(new byte[]{20, 30, 40}, c.toByteArray());
    }

    /** 跨节点窗口写：size 恰好为 length */
    @Test
    void crossNodeWindowWrite() {
        LinkedBuffer buffer = new LinkedBuffer(4);
        byte[] data = new byte[10];
        for (int i = 0; i < 10; i++) {
            data[i] = (byte) (i + 1);
        }
        buffer.write(data, 0, 10);
        assertEquals(10, buffer.size());
        assertArrayEquals(data, buffer.toByteArray());
    }

    /** growth 复利扩容：节点序列 8→16→32 共 3 个（原实现每档都从 initSize 重算，永远 16） */
    @Test
    void growthCompounds() {
        LinkedBuffer buffer = new LinkedBuffer(8, 2.0F);
        for (int i = 0; i < 40; i++) {
            buffer.write((byte) i);
        }
        assertEquals(40, buffer.size());
        assertEquals(3, nodeCount(buffer), "复利扩容应 8+16+32=3 节点（不复合为 8+16+16+16=4）");
        byte[] expected = new byte[40];
        for (int i = 0; i < 40; i++) {
            expected[i] = (byte) i;
        }
        assertArrayEquals(expected, buffer.toByteArray());
    }

    /** release 后 size 归零（原残留旧值） */
    @Test
    void releaseResetsSize() {
        LinkedBuffer buffer = new LinkedBuffer(32);
        buffer.write(new byte[]{1, 2, 3});
        assertEquals(3, buffer.size());
        buffer.release();
        assertEquals(0, buffer.size(), "release 后 size 必须归零，不得残留");
    }

    /** clear 可复用 */
    @Test
    void clearAllowsReuse() {
        LinkedBuffer buffer = new LinkedBuffer(16);
        buffer.write(new byte[]{1, 2, 3, 4, 5});
        byte[] first = buffer.toByteArray();
        buffer.clear();
        assertEquals(0, buffer.size());
        buffer.write(new byte[]{9, 8});
        assertArrayEquals(new byte[]{9, 8}, buffer.toByteArray());
        assertEquals(5, first.length);
    }

    /** node 窗口写返回 length（底层原语契约） */
    @Test
    void nodeWindowWriteReturnsLength() throws Exception {
        LinkedBufferNode node = LinkedBufferNode.wrapBuffer(ByteBuffer.allocate(16));
        byte[] data = new byte[100];
        int written = node.write(data, 0, 7);
        assertEquals(7, written);
        assertEquals(7, node.getPosition());
    }

}
