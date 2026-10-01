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
package com.tny.game.bench.net.routine;

import com.tny.game.common.type.ReferenceType;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.MessageQueue;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 发送缓存成本画像（任务 2.2）：禁用态 volatile 短路 vs 启用态锁路径 vs 环形窗口读取。
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
public class MessageQueueBenchmark {

    @Param({"0", "64"})
    private int capacity;

    private MessageQueue queue;
    private Message message;

    @Setup(Level.Trial)
    public void setUp() {
        queue = new MessageQueue(capacity);
        message = new BenchMessage();
    }

    @Benchmark
    public void addMessage() {
        queue.addMessage(message);
    }

    @Benchmark
    public void snapshotRead(Blackhole blackhole) {
        blackhole.consume(queue.getAllMessages());
    }

    @Benchmark
    public void filteredRead(Blackhole blackhole) {
        blackhole.consume(queue.getMessages(m -> true));
    }

    /** bench 专用消息桩（最小实现，同 RpcContextFixture 思路） */
    static final class BenchMessage extends com.tny.game.common.context.AttributeHolder implements NetMessage {
        @Override
        public MessageHead getHead() {
            return this;
        }

        @Override
        public long getId() {
            return 1L;
        }

        @Override
        public long getTime() {
            return System.currentTimeMillis();
        }

        @Override
        public long getToMessage() {
            return 0L;
        }

        @Override
        public int getCode() {
            return 0;
        }

        @Override
        public int getProtocolId() {
            return 1000;
        }

        @Override
        public MessageMode getMode() {
            return MessageMode.PUSH;
        }

        @Override
        public boolean existBody() {
            return false;
        }

        @Override
        public Object getBody() {
            return null;
        }

        @Override
        public <T> T bodyAs(Class<T> clazz) {
            return null;
        }

        @Override
        public <T> T bodyAs(ReferenceType<T> clazz) {
            return null;
        }

        @Override
        public void allotMessageId(long id) {
        }

        @Override
        public boolean isRelay() {
            return false;
        }

        @Override
        public void relay(boolean value) {
        }

        @Override
        public <T extends MessageHeader<?>> T getHeader(String key, Class<T> headerClass) {
            return null;
        }

        @Override
        public <T extends MessageHeader<?>> T getHeader(MessageHeaderKey<T> key) {
            return null;
        }

        @Override
        public <T extends MessageHeader<?>> List<T> getHeaders(Class<T> headerClass) {
            return List.of();
        }

        @Override
        public boolean isHasHeaders() {
            return false;
        }

        @Override
        public List<MessageHeader<?>> getAllHeaders() {
            return List.of();
        }

        @Override
        public java.util.Map<String, MessageHeader<?>> getAllHeaderMap() {
            return java.util.Map.of();
        }

        @Override
        public boolean existHeader(String key) {
            return false;
        }

        @Override
        public boolean existHeader(MessageHeaderKey<?> key) {
            return false;
        }

        @Override
        public boolean existHeader(String key, Class<? extends MessageHeader<?>> headerClass) {
            return false;
        }

        @Override
        public <H extends MessageHeader<H>> MessageHeader<H> putHeader(MessageHeader<H> header) {
            return header;
        }

        @Override
        public <H extends MessageHeader<H>> MessageHeader<H> putHeaderIfAbsent(MessageHeader<H> header) {
            return header;
        }

        @Override
        public <T extends MessageHeader<?>> T removeHeader(String key) {
            return null;
        }

        @Override
        public <T extends MessageHeader<?>> T removeHeader(String key, Class<T> headerClass) {
            return null;
        }

        @Override
        public <T extends MessageHeader<?>> T removeHeader(MessageHeaderKey<T> key) {
            return null;
        }

        @Override
        public void removeHeaders(Iterable<String> keys) {
        }

        @Override
        public void removeAllHeaders() {
        }
    }

}
