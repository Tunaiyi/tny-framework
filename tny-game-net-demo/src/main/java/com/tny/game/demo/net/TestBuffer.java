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

package com.tny.game.demo.net;

import com.tny.game.common.runtime.*;
import io.netty.buffer.*;

import java.nio.ByteBuffer;
import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/12 4:33 上午
 */
public class TestBuffer {

    private static final ProcessWatcher HEAP_READ_WATCHER = ProcessWatcher
            .of("Heap read", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    private static final ProcessWatcher HEAP_WRITE_WATCHER = ProcessWatcher
            .of("Heap write", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    private static final ProcessWatcher DIRECT_READ_WATCHER = ProcessWatcher
            .of("Direct read", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    private static final ProcessWatcher DIRECT_WRITE_WATCHER = ProcessWatcher
            .of("Direct write", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    private static final ProcessWatcher NETTY_READ_WATCHER = ProcessWatcher
            .of("Netty read", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    private static final ProcessWatcher NETTY_WRITE_WATCHER = ProcessWatcher
            .of("Netty write", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static void main(String[] args) throws Throwable {
        int size = 1024 * 1024;
        ByteBuffer heap = ByteBuffer.allocate(size);
        ByteBuffer direct = ByteBuffer.allocateDirect(size);
        ByteBuf nettyBuf = ByteBufAllocator.DEFAULT.directBuffer(size);
        byte[] heapData = new byte[size / 10];
        byte[] directData = new byte[size / 10];
        byte[] nettyData = new byte[size / 10];
        for (int i = 0; i < size; i++) {
            heap.put((byte) -1);
            direct.put((byte) -1);
            nettyBuf.writeByte((byte) -1);
            heap.position(0);
            direct.position(0);
        }
        int times = 100000000;
        ExecutorService service = Executors.newCachedThreadPool();
        Future<?> heapFuture = service.submit(() -> {
            try {
                test(times, heap, heapData, HEAP_READ_WATCHER, HEAP_WRITE_WATCHER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Future<?> directFuture = service.submit(() -> {
            try {
                test(times, direct, directData, DIRECT_READ_WATCHER, DIRECT_WRITE_WATCHER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Future<?> nettyFuture = service.submit(() -> {
            try {
                test(times, nettyBuf, nettyData, NETTY_READ_WATCHER, NETTY_WRITE_WATCHER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        heapFuture.get();
        directFuture.get();
        nettyFuture.get();
    }

    private static void test(int times, ByteBuffer buffer, byte[] data, ProcessWatcher readWatcher, ProcessWatcher writeWatcher) throws Exception {
        buffer.mark();
        for (int index = 0; index < times; index++) {
            try (ProcessTracer ignored = readWatcher.trace()) {
                buffer.get();
                buffer.getInt();
                buffer.getDouble();
                buffer.getLong();
                buffer.getFloat();
                buffer.getShort();
                buffer.get(data);
            }

            buffer.reset();

            try (ProcessTracer ignored = writeWatcher.trace()) {
                buffer.put((byte) -1);
                buffer.putInt(0xffffffff);
                buffer.putDouble(-1);
                buffer.putLong(-1L);
                buffer.putFloat(-1);
                buffer.putShort((short) -1);
                buffer.put(data);
            }

            buffer.reset();
        }
    }

    private static void test(int times, ByteBuf buffer, byte[] data, ProcessWatcher readWatcher, ProcessWatcher writeWatcher) throws Exception {
        buffer.markReaderIndex();
        buffer.markWriterIndex();
        for (int index = 0; index < times; index++) {
            try (ProcessTracer ignored = readWatcher.trace()) {
                buffer.readByte();
                buffer.readInt();
                buffer.readDouble();
                buffer.readLong();
                buffer.readFloat();
                buffer.readShort();
                buffer.readBytes(data);
            }

            buffer.resetReaderIndex();

            try (ProcessTracer ignored = writeWatcher.trace()) {
                buffer.writeByte((byte) -1);
                buffer.writeInt(0xffffffff);
                buffer.writeDouble(-1);
                buffer.writeLong(-1L);
                buffer.writeFloat(-1);
                buffer.writeShort((short) -1);
                buffer.writeBytes(data);
            }

            buffer.resetWriterIndex();
        }
    }

}
