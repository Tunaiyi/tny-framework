/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.message;

import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 15（Wave-C）红灯基线：消息头容器跨线程读写安全与读取快照。
 */
class HeaderContainerConcurrencyTest {

    private static final class TestHeader extends MessageHeader<TestHeader> {

        private final String key;

        TestHeader(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public boolean isTransitive() {
            return false;
        }
    }

    @Test
    @DisplayName("并发 putHeader：全部头可回读（修复前并发首写整表覆盖）")
    void concurrentPutsAllRetained() throws Exception {
        BaseMessageHeaderContainer container = new BaseMessageHeaderContainer();
        int threads = 16;
        int perThread = 50;
        CyclicBarrier barrier = new CyclicBarrier(threads);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            int tid = t;
            futures.add(pool.submit(() -> {
                try {
                    barrier.await(5, TimeUnit.SECONDS);
                } catch (Exception ignored) {
                }
                for (int i = 0; i < perThread; i++) {
                    container.putHeader(new TestHeader("h-" + tid + "-" + i));
                }
            }));
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));
        for (Future<?> f : futures) {
            f.get(5, TimeUnit.SECONDS);
        }
        assertEquals(threads * perThread, container.getAllHeaders().size(), "并发写入不得丢失头部");
    }

    @Test
    @DisplayName("getAllHeaderMap 返回快照：后续写入不回灌已交付的遍历视图")
    void allHeaderMapIsSnapshot() {
        BaseMessageHeaderContainer container = new BaseMessageHeaderContainer();
        container.putHeader(new TestHeader("first"));
        Map<String, MessageHeader<?>> snapshot = container.getAllHeaderMap();
        container.putHeader(new TestHeader("second"));
        assertEquals(1, snapshot.size(), "编码器持有的声明计数视图必须与交付时刻一致（修复前 live 视图随写膨胀）");
        assertEquals(2, container.getAllHeaderMap().size());
    }

    @Test
    @DisplayName("遍历与并发写不互相炸裂")
    void iterationSafeUnderConcurrentWrites() throws Exception {
        BaseMessageHeaderContainer container = new BaseMessageHeaderContainer();
        for (int i = 0; i < 50; i++) {
            container.putHeader(new TestHeader("seed-" + i));
        }
        java.util.concurrent.atomic.AtomicBoolean running = new java.util.concurrent.atomic.AtomicBoolean(true);
        ExecutorService pool = Executors.newFixedThreadPool(5);
        Future<?> reader = pool.submit(() -> {
            while (running.get()) {
                container.getAllHeaderMap().size();
                container.getAllHeaders().size();
            }
        });
        List<Future<?>> writers = new ArrayList<>();
        for (int t = 0; t < 4; t++) {
            int tid = t;
            writers.add(pool.submit(() -> {
                for (int i = 0; i < 2000; i++) {
                    container.putHeader(new TestHeader("w" + tid + "-" + i));
                }
            }));
        }
        for (Future<?> w : writers) {
            w.get(20, TimeUnit.SECONDS);
        }
        running.set(false);
        reader.get(20, TimeUnit.SECONDS);
        pool.shutdown();
        assertTrue(container.getAllHeaders().size() >= 50);
    }

    @Test
    @DisplayName("tracing 头拷贝：属性表别名切断")
    void tracingHeaderCopyIsolatesAttributes() {
        RpcTracingHeader original = new RpcTracingHeader();
        original.put("k", "v1");
        RpcTracingHeader copy = original.copy();
        original.put("k", "v2");
        assertEquals("v1", copy.getOrDefault("k", null), "拷贝后不得随源表变化");
        copy.put("k2", "x");
        assertNull(original.getOrDefault("k2", null));
    }

}
