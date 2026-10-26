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
package com.tny.game.net.message.codec;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.codec.typeprotobuf.*;
import io.netty.buffer.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 27（Wave-C）裁决用例：跨 event-loop 共享的 jprotobuf Codec 实例在并发编解码下
 * 往返一致性必须成立（design D11：通过=结案；失败=登记独立缺陷移交）。
 */
class ConcurrentCodecRoundTripTest {

    @com.tny.game.codec.typeprotobuf.annotation.TypeProtobuf(9001)
    @ProtobufClass
    public static class ProbePayload {

        @Protobuf(order = 1)
        public String name;

        @Protobuf(order = 2)
        public int value;

        public ProbePayload() {
        }

        public ProbePayload(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ProbePayload)) {
                return false;
            }
            ProbePayload other = (ProbePayload) o;
            return this.value == other.value && Objects.equals(this.name, other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }
    }

    static {
        TypeProtobufSchemeManager.getInstance().loadScheme(ProbePayload.class);
    }

    @Test
    @DisplayName("共享编解码器实例 8 线程 × 300 次往返全部一致")
    void sharedCodecIsConcurrencySafe() throws Exception {
        TypeProtobufMessageBodyCodec<ProbePayload> codec = new TypeProtobufMessageBodyCodec<>();
        int threads = 8;
        int iterations = 300;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        AtomicReference<Throwable> firstFailure = new AtomicReference<>();
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            int seed = t;
            futures.add(pool.submit(() -> {
                try {
                    start.await();
                    for (int i = 0; i < iterations; i++) {
                        ProbePayload payload = new ProbePayload("p-" + seed + "-" + i, i * 31 + seed);
                        ByteBuf buf = Unpooled.buffer();
                        codec.encode(payload, buf);
                        ProbePayload decoded = codec.decode(buf);
                        buf.release();
                        if (!payload.equals(decoded)) {
                            throw new AssertionError("往返不一致: " + payload + " vs " + decoded);
                        }
                    }
                } catch (Throwable e) {
                    firstFailure.compareAndSet(null, e);
                }
            }));
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(60, TimeUnit.SECONDS), "并发压测超时（疑似死锁/挂起）");
        for (Future<?> f : futures) {
            f.get(5, TimeUnit.SECONDS);
        }
        assertNull(firstFailure.get(), "并发编解码失败: " + firstFailure.get());
    }

}
