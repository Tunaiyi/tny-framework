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
package com.tny.game.common.utils;

import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * identifier-generation 契约（fix-common-dormant-defects 组5）：
 * 小幅回拨等待追平不抛不重号、超阈回拨显式失败不产号、恢复后序列不回卷、
 * 相同身份位双实例构造期显式冲突、计数生成器同毫秒多实例互不撞。
 * 时钟经包内注入缝驱动（公共构造签名不变）。
 */
class IdentifierGenerationContractTest {

    /** 脚本时钟：按队列推进，队列耗尽后回退到最后一个值继续 +1（可控追平） */
    static class ScriptedClock {

        final AtomicLong now;

        final Deque<Long> script = new ArrayDeque<>();

        final AtomicInteger reads = new AtomicInteger();

        ScriptedClock(long start) {
            this.now = new AtomicLong(start);
        }

        void push(long value) {
            script.addLast(value);
        }

        long get() {
            reads.incrementAndGet();
            Long scripted;
            while ((scripted = script.pollFirst()) != null) {
                now.set(scripted);
                return now.get();
            }
            // 脚本耗尽后自然前进（模拟时钟追平，避免等待循环永挂）
            return now.incrementAndGet();
        }
    }

    @Test
    void monotonicIdsUniqueAndParseable() {
        long creatorWorker = FREE_WORKER_ID.getAndIncrement();
        SnowflakeIdCreator creator = new SnowflakeIdCreator(creatorWorker);
        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            assertTrue(ids.add(creator.createId()), "第 " + i + " 个号重复");
        }
        long id = creator.createId();
        assertEquals(creatorWorker, SnowflakeIdCreator.parseWorkerId(id), "身份位可解析回读");
    }

    /** 小幅回拨（≤阈值）：不抛异常、等待追平后恢复、绝不与已发号重复 */
    @Test
    void smallBackwardWaitsAndNeverDuplicates() {
        ScriptedClock clock = new ScriptedClock(1_800_000_000_000L);
        SnowflakeIdCreator creator = newClockSeeded(clock);
        Set<Long> issued = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            issued.add(creator.createId());
        }
        long before = clock.reads.get();
        clock.push(1_800_000_000_000L - 80); // 回拨 80ms（阈值内）
        // 追平需要时钟推进：脚本恢复前进由 get() 自增发完成
        long id = assertDoesNotThrow(creator::createId, "小幅回拨不得切断产号");
        assertTrue(issued.add(id), "回拨恢复后产出重号");
        assertTrue(clock.reads.get() > before + 1, "等待追平未重读时钟（可能直接用了回拨旧值产号）");
    }

    /** 超阈回拨：显式失败且不产出号码；时间涨回后恢复正常且与历史不重 */
    @Test
    void largeBackwardFailsExplicitlyThenRecovers() {
        ScriptedClock clock = new ScriptedClock(1_800_000_000_000L);
        SnowflakeIdCreator creator = newClockSeeded(clock);
        Set<Long> issued = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            issued.add(creator.createId());
        }
        long lastTime = SnowflakeIdCreator.parseTime(issued.stream().reduce(0L, Math::max));
        clock.push(1_800_000_000_000L - 5000);
        assertThrows(RuntimeException.class, creator::createId, "超阈回拨必须显式失败");
        // 涨回历史最高点之后恢复
        clock.push(lastTime + 1);
        long revived = assertDoesNotThrow(creator::createId);
        assertTrue(issued.add(revived), "恢复后与历史号重复");
    }

    /** 同毫秒序列耗尽走下一毫秒承接；回拨恢复同毫秒序列不回卷（防重发） */
    @Test
    void sequenceNeverRollsBackInSameMillisecond() {
        ScriptedClock clock = new ScriptedClock(1_800_000_000_000L);
        SnowflakeIdCreator creator = newClockSeeded(clock);
        Set<Long> issued = new HashSet<>();
        // 冻结在同一毫秒发满一批（时钟不再前进→序列推进或跨毫秒等待）
        for (int i = 0; i < 50; i++) {
            clock.push(1_800_000_000_000L);
            issued.add(creator.createId());
        }
        assertEquals(50, issued.size(), "同毫秒序列出现回卷重号");
    }

    /** 身份位越界构造期拒绝；相同身份位双实例显式冲突（不得静默同码） */
    @Test
    void workerIdValidationAndConflict() {
        assertThrows(RuntimeException.class, () -> new SnowflakeIdCreator(-1));
        assertThrows(RuntimeException.class, () -> new SnowflakeIdCreator(1 << 12));
        SnowflakeIdCreator first = new SnowflakeIdCreator(4095);
        assertNotNull(first);
        assertThrows(IllegalStateException.class, () -> new SnowflakeIdCreator(4095),
                "同 workerID 双实例必须构造期显式冲突（同毫秒同序列必撞号）");
    }

    /** 计数型：同毫秒新建两实例，各自号段互不碰撞（rpc 双校验器同毫秒初始化形态） */
    @Test
    void hashIdCreatorInstancesDisjoint() {
        HashIDCreator a = new HashIDCreator(16);
        HashIDCreator b = new HashIDCreator(16);
        Set<Long> seen = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            assertTrue(seen.add(a.createId()), "计数生成器实例间/自身产号碰撞于第 " + i + " 次");
            assertTrue(seen.add(b.createId()), "计数生成器实例间/自身产号碰撞于第 " + i + " 次");
        }
        assertEquals(2000, seen.size());
    }

    /** 多线程并发产号零重号（真实时钟） */
    @Test
    void concurrentCreationNoDuplicates() throws Exception {
        SnowflakeIdCreator creator = new SnowflakeIdCreator(FREE_WORKER_ID.getAndIncrement());
        int threads = 8;
        int per = 2000;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Set<Long>>> futures = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            futures.add(pool.submit(() -> {
                Set<Long> own = ConcurrentHashMap.newKeySet();
                start.await();
                for (int i = 0; i < per; i++) {
                    own.add(creator.createId());
                }
                return own;
            }));
        }
        start.countDown();
        Set<Long> all = new HashSet<>();
        for (Future<Set<Long>> f : futures) {
            all.addAll(f.get(60, TimeUnit.SECONDS));
        }
        pool.shutdownNow();
        assertEquals(threads * per, all.size(), "并发产号出现重号");
    }

    private static final AtomicLong FREE_WORKER_ID = new AtomicLong(1);

    /** 包内注入缝：workerID/wBits/sBits/时钟源（公共签名不变） */
    private static SnowflakeIdCreator newClockSeeded(ScriptedClock clock) {
        return new SnowflakeIdCreator(FREE_WORKER_ID.getAndIncrement(), 12L, 10L, clock::get);
    }

}
