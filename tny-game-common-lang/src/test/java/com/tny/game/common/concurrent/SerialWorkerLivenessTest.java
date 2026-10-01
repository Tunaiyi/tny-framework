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
package com.tny.game.common.concurrent;

import com.tny.game.common.concurrent.worker.AsyncAction;
import com.tny.game.common.concurrent.worker.AsyncWorker;
import com.tny.game.common.concurrent.worker.SerialAsyncWorker;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 串行 worker 活性契约（生产热路径 SerialCommandExecutor 每命令必经）：
 * A1——isDone 检查与 WAITING 置位间的竞态、以及"内层永不完成+外层超时"都不得让环永久停摆；
 * A2——排队期间已超时的任务不得再执行副作用；
 * A4——worker 线程内联提交异常不得返回 null future。
 */
class SerialWorkerLivenessTest {

    private ExecutorService master;
    private SerialAsyncWorker worker;

    @BeforeEach
    void setUp() {
        master = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "serial-master");
            t.setDaemon(true);
            return t;
        });
        worker = AsyncWorker.createSerialWorker("liveness-test", master);
    }

    @AfterEach
    void tearDown() {
        master.shutdownNow();
    }

    /** 外层 future 超时脱身的正确形态：ExecutionException(cause=TimeoutException) */
    private static void assertTimedOut(CompletableFuture<?> future) {
        ExecutionException e = assertThrows(ExecutionException.class,
                () -> future.get(10, TimeUnit.SECONDS));
        assertInstanceOf(TimeoutException.class, e.getCause());
    }

    /**
     * A1 严重形态：内层永不完成、外层超时后，后续任务必须继续被消费
     * （原实现续环挂在内层上，超时脱身不唤醒 → 整条命令队列停摆）。
     */
    @Test
    void workerAliveAfterNeverCompletingActionTimesOut() throws Exception {
        CompletableFuture<String> never = new CompletableFuture<>();
        CompletableFuture<String> outer = worker.await(() -> never, 100, TimeUnit.MILLISECONDS);
        assertTimedOut(outer);

        CompletableFuture<Integer> ping = worker.apply(() -> 42);
        assertEquals(42, ping.get(5, TimeUnit.SECONDS), "超时后串行 worker 停摆（A1 复现）");
        never.complete("late");
        // 迟到完成不得干扰已恢复的队列
        assertEquals(43, worker.apply(() -> 43).get(5, TimeUnit.SECONDS));
    }

    /**
     * A2：队首任务把环吊在永不完成的外层超时下，排队中的 B 超时后，
     * 环恢复时 B 必须被跳过（副作用不得在调用方脱身后发生）。
     */
    @Test
    void timedOutQueuedTaskSideEffectSkipped() throws Exception {
        CompletableFuture<String> never = new CompletableFuture<>();
        CompletableFuture<String> blocker = worker.await(() -> never, 500, TimeUnit.MILLISECONDS);

        AtomicBoolean bRan = new AtomicBoolean();
        CompletableFuture<Void> bFuture = worker.run(() -> bRan.set(true), 100, TimeUnit.MILLISECONDS);

        assertTimedOut(bFuture);   // B 先超时（此时环仍吊在 blocker 内层）
        assertTimedOut(blocker);   // blocker 超时唤醒环（afterTimeout 钩子）

        CompletableFuture<Integer> ping = worker.apply(() -> 42);
        assertEquals(42, ping.get(5, TimeUnit.SECONDS));
        assertFalse(bRan.get(), "排队期间已超时的任务不得执行副作用（A2）");
        never.complete("late");
    }

    /**
     * A1 竞态形态压力：内层由独立线程完成（与 WAITING 置位窗口竞速），
     * 200 轮后 worker 仍存活且全部外层正确完成。
     */
    @Test
    void raceStressKeepsWorkerAlive() throws Exception {
        ExecutorService completers = Executors.newFixedThreadPool(4);
        try {
            int rounds = 200;
            List<CompletableFuture<Integer>> futures = new ArrayList<>(rounds);
            for (int i = 0; i < rounds; i++) {
                final int value = i;
                CompletableFuture<Integer> inner = new CompletableFuture<>();
                completers.execute(() -> inner.complete(value));
                futures.add(worker.await(() -> inner));
            }
            for (int i = 0; i < rounds; i++) {
                assertEquals(i, futures.get(i).get(10, TimeUnit.SECONDS),
                        "第 " + i + " 轮 await 未完成（疑似 WAITING 停摆）");
            }
            assertEquals(999, worker.apply(() -> 999).get(5, TimeUnit.SECONDS));
        } finally {
            completers.shutdownNow();
        }
    }

    /**
     * A4：worker 线程内部再提交且 action 抛异常——返回异常完成的 future，不得为 null。
     */
    @Test
    void inlineSubmitFailureReturnsFailedFuture() throws Exception {
        AtomicReference<CompletableFuture<Integer>> captured = new AtomicReference<>();
        CompletableFuture<Void> outer = worker.run(() ->
                captured.set(worker.apply(() -> {
                    throw new IllegalStateException("boom-inline");
                })));
        outer.get(5, TimeUnit.SECONDS);
        CompletableFuture<Integer> inner = captured.get();
        assertNotNull(inner, "内联提交异常不得返回 null future（A4）");
        ExecutionException e = assertThrows(ExecutionException.class, () -> inner.get(5, TimeUnit.SECONDS));
        assertNotNull(e.getCause());
    }

    /** 串行序保持：任意时刻至多一个任务在运行 */
    @Test
    void serialOrderNeverOverlaps() throws Exception {
        AtomicInteger concurrent = new AtomicInteger();
        AtomicInteger maxConcurrent = new AtomicInteger();
        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            tasks.add(worker.await(() -> {
                int now = concurrent.incrementAndGet();
                maxConcurrent.accumulateAndGet(now, Math::max);
                concurrent.decrementAndGet();
                return CompletableFuture.completedFuture(null);
            }));
        }
        for (CompletableFuture<Void> task : tasks) {
            task.get(10, TimeUnit.SECONDS);
        }
        assertEquals(1, maxConcurrent.get(), "串行 worker 任何时刻只允许一个任务在跑");
    }

}
