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
package com.tny.game.net.command.dispatcher;

import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 4（Wave-A）红灯基线：分片 ID 生成的位宽正确性——index 编码位数必须是 ceil(log2(n))
 * 而非 bitCount(n)，否则跨分片产生相同 ID（消息 ID 碰撞 → 请求响应错配）。
 */
class AutoIncrementIdGeneratorTest {

    private static long generateIn(AutoIncrementIdGenerator generator, int shards, int shard, int times) throws Exception {
        long[] out = new long[times];
        Runnable body = () -> {
            for (int i = 0; i < times; i++) {
                out[i] = generator.generate();
            }
        };
        Thread pinned = null;
        for (int attempt = 0; attempt < 100000; attempt++) {
            Thread candidate = new Thread(body);
            if (Math.floorMod(candidate.getId(), shards) == shard) {
                pinned = candidate;
                break;
            }
        }
        assertNotNull(pinned, "测试环境应能在 10 万次构造内找到落点分片 " + shard + " 的线程");
        pinned.start();
        pinned.join(10000);
        return out[0];
    }

    @Test
    @DisplayName("n=8 时 shard4 与 shard6 的同计数值不得相同（位宽实锤）")
    void adjacentShardsDoNotCollide() throws Exception {
        AutoIncrementIdGenerator generator = new AutoIncrementIdGenerator(8);
        long firstOfShard4 = generateIn(generator, 8, 4, 1);
        long firstOfShard6 = generateIn(generator, 8, 6, 1);
        assertNotEquals(firstOfShard4, firstOfShard6,
                "修复前 (1<<1)|4 与 (1<<1)|6 同为 6——跨分片撞号");
    }

    @Test
    @DisplayName("n=6 全分片 × 2000 次生成无重复")
    void stressAcrossAllShardsNoDuplicate() throws Exception {
        int shards = 6;
        int perShard = 2000;
        AutoIncrementIdGenerator generator = new AutoIncrementIdGenerator(shards);
        Set<Long> unique = new HashSet<>();
        for (int shard = 0; shard < shards; shard++) {
            long[] out = new long[perShard];
            Thread pinned = null;
            for (int attempt = 0; attempt < 100000; attempt++) {
                Thread candidate = new Thread(() -> {
                    for (int i = 0; i < perShard; i++) {
                        out[i] = generator.generate();
                    }
                });
                if (Math.floorMod(candidate.getId(), shards) == shard) {
                    pinned = candidate;
                    break;
                }
            }
            assertNotNull(pinned);
            pinned.start();
            pinned.join(10000);
            for (long v : out) {
                assertTrue(unique.add(v), "检测到重复 ID: " + v);
            }
        }
        assertEquals(shards * perShard, unique.size());
    }

    @Test
    @DisplayName("兼容锚：单线程生成严格递增")
    void singleThreadMonotonic() {
        AutoIncrementIdGenerator generator = new AutoIncrementIdGenerator(8);
        long previous = generator.generate();
        for (int i = 0; i < 1000; i++) {
            long next = generator.generate();
            assertTrue(next > previous, "同线程必须严格递增");
            previous = next;
        }
    }

    @Test
    @DisplayName("兼容锚：单分片（n=1）退化为纯计数且无重叠")
    void singleShardDegradesToCounter() {
        AutoIncrementIdGenerator generator = new AutoIncrementIdGenerator(1);
        assertEquals(1L, generator.generate());
        assertEquals(2L, generator.generate());
    }

}
