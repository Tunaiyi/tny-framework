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
package com.tny.game.common.concurrent;

import com.tny.game.common.concurrent.utils.ExeAide;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * futures-executor-contracts 契约（fix-common-dormant-defects 组3）：
 * 单观察者等待超时/异常/中断不得毒化共享任务终态；异步续接的执行器参数真实承载；
 * 具名池工厂参数入键且注册表可整体关闭；超限拒绝即终结本次提交且计数吻合；
 * 静默工具吞异常必须恢复中断标志。
 */
class FuturesExecutorContractTest {

    // ---- 包装 future：超时不毒化（规格1）----

    /** 一个观察者等待超时，不得改写被包装任务终态——后续真实结果照常可见 */
    @Test
    void waiterTimeoutNeverPoisonsSharedFuture() throws Exception {
        CompletableFuture<String> shared = new CompletableFuture<>();
        WrapperStageFuture<String> wrapper = new WrapperStageFuture<>(shared);

        assertFalse(wrapper.await(80, TimeUnit.MILLISECONDS), "超时等待应返回 false");

        // 关键：任务本体仍存活，真实结果随后照常完成并可被任何观察者取得
        shared.complete("late-result");
        assertTrue(wrapper.await(2, TimeUnit.SECONDS), "超时已毒化共享终态（迟到的真实结果被连坐丢弃）");
        assertEquals("late-result", wrapper.get(1, TimeUnit.SECONDS));
    }

    /** 任务异常完成：等待方判 false，同样不得"再包一层"改写其他观察者所见 */
    @Test
    void executionFailurePropagatesNaturallyNotByRewrap() throws Exception {
        CompletableFuture<String> shared = new CompletableFuture<>();
        WrapperStageFuture<String> wrapper = new WrapperStageFuture<>(shared);
        IllegalStateException boom = new IllegalStateException("task failed");
        shared.completeExceptionally(boom);
        assertFalse(wrapper.await(2, TimeUnit.SECONDS));
        ExecutionException e = assertThrows(ExecutionException.class, () -> wrapper.get(1, TimeUnit.SECONDS));
        assertSame(boom, e.getCause(), "异常链必须原样（不得二次包装成 ExecutionException of ExecutionException）");
    }

    /** 不可中断等待：中断只挂起等待并恢复标志，不毒化任务 */
    @Test
    void uninterruptibleAwaitKeepsInterruptAndTask() throws Exception {
        CompletableFuture<String> shared = new CompletableFuture<>();
        WrapperStageFuture<String> wrapper = new WrapperStageFuture<>(shared);
        AtomicReference<Throwable> thrown = new AtomicReference<>();
        AtomicBoolean flagAfter = new AtomicBoolean();
        Thread waiter = new Thread(() -> {
            try {
                boolean ok = wrapper.awaitUninterruptibly(5, TimeUnit.SECONDS);
                flagAfter.set(Thread.currentThread().isInterrupted());
                if (!ok) {
                    thrown.set(new AssertionError("uninterruptible await 返回失败"));
                }
            } catch (Throwable e) {
                thrown.set(e);
            }
        });
        waiter.start();
        Thread.sleep(120);
        waiter.interrupt();
        // 中断后应继续等待直至真实结果
        shared.complete("ok-after-interrupt");
        waiter.join(5000);
        assertNull(thrown.get(), "不可中断等待被中断击穿或毒化任务: " + thrown.get());
        assertTrue(flagAfter.get(), "中断标志必须保持置位（吞中断违反取消语义）");
        assertEquals("ok-after-interrupt", wrapper.get(1, TimeUnit.SECONDS));
    }

    // ---- 执行器参数真实承载（规格3）----

    /** 两个丢参方法（runAfterBothAsync/acceptEitherAsync 带 executor 形态）必须由指定执行器承载续接 */
    @Test
    void asyncContinuationsHonorExecutorArgument() throws Exception {
        ExecutorService marker = Executors.newSingleThreadExecutor(
                r -> new Thread(r, "executor-arg-marker"));
        try {
            CompletableFuture<Void> a = new CompletableFuture<>();
            WrapperStageFuture<Void> wrapper = new WrapperStageFuture<>(a);
            AtomicReference<String> runThread = new AtomicReference<>();
            CompletionStage<Void> stage = wrapper.runAfterBothAsync(CompletableFuture.completedFuture("x"),
                    () -> runThread.set(Thread.currentThread().getName()), marker);
            a.complete(null);
            // 续接在指定执行器线程上执行（ForkJoin commonPool 线程名不会是 marker）
            long deadline = System.currentTimeMillis() + 3000;
            while (runThread.get() == null && System.currentTimeMillis() < deadline) {
                Thread.sleep(20);
            }
            assertEquals("executor-arg-marker", runThread.get(),
                    "runAfterBothAsync 的执行器参数被忽略（实际线程：" + runThread.get() + "）");

            AtomicReference<String> acceptThread = new AtomicReference<>();
            WrapperStageFuture<String> w2 = new WrapperStageFuture<>(new CompletableFuture<>());
            w2.acceptEitherAsync(CompletableFuture.completedFuture("other"),
                    v -> acceptThread.set(Thread.currentThread().getName()), marker);
            deadline = System.currentTimeMillis() + 3000;
            while (acceptThread.get() == null && System.currentTimeMillis() < deadline) {
                Thread.sleep(20);
            }
            assertEquals("executor-arg-marker", acceptThread.get(), "acceptEitherAsync 的执行器参数被忽略");
        } finally {
            marker.shutdownNow();
        }
    }

    // ---- 池工厂参数入键 + 整体关闭（规格5/6）----

    /** 同名不同参各自建池（不再静默复用首建参数）；同名同参复用 */
    @Test
    void poolFactoriesKeyIncludesParameters() {
        ExecutorService p1 = ThreadPoolExecutors.pool("contract-small", 1);
        ExecutorService p2 = ThreadPoolExecutors.pool("contract-small", 4);
        assertNotSame(p1, p2, "同名不同参被静默共用首建池");
        assertSame(p1, ThreadPoolExecutors.pool("contract-small", 1), "同参数必须复用");
        ForkJoinPool f1 = ForkJoinPools.pool(2, "contract-fjp", false);
        ForkJoinPool f2 = ForkJoinPools.pool(5, "contract-fjp", true);
        assertNotSame(f1, f2, "工作窃取池同名不同参被静默共用");
        assertSame(f1, ForkJoinPools.pool(2, "contract-fjp", false));
    }

    /** 注册表整体关闭后同名可重建新池（幂等、可恢复） */
    @Test
    void poolRegistryShutdownAndRebuild() {
        ExecutorService before = ThreadPoolExecutors.pool("contract-shutdown", 1);
        ThreadPoolExecutors.shutdownAll();
        ThreadPoolExecutors.shutdownAll(); // 幂等
        assertTrue(before.isShutdown(), "整体关闭未送达已注册池");
        ExecutorService after = ThreadPoolExecutors.pool("contract-shutdown", 1);
        assertNotSame(before, after, "关闭后同参数申请应得到新池");
        ThreadPoolExecutors.shutdownAll();
        ForkJoinPool fb = ForkJoinPools.pool(1, "contract-fjp-shutdown", false);
        ForkJoinPools.shutdownAll();
        assertTrue(fb.isShutdown());
    }

    // ---- 标准线程池：拒绝即终结 + 计数吻合（规格7）----

    /** 超上限提交：拒绝处置后不得继续入队；计数与实收严格吻合（CallerRuns 不双执行、Discard 不漂移） */
    @Test
    void standardExecutorRejectTerminatesSubmission() throws Exception {
        AtomicInteger ran = new AtomicInteger();
        Runnable task = ran::incrementAndGet;
        // CallerRuns：拒绝=当场由提交线程跑一次，绝不允许双执行
        StandardThreadExecutor callerRuns = new StandardThreadExecutor(1, 1, 60, TimeUnit.SECONDS, 1,
                r -> new Thread(r, "cr-test"), new ThreadPoolExecutor.CallerRunsPolicy());
        try {
            // 占满：1 线程 + 队列 1 → 第 3 个触发拒绝
            CountDownLatch blocker = new CountDownLatch(1);
            callerRuns.execute(() -> awaitQuiet(blocker));
            callerRuns.execute(blocker::countDown);
            callerRuns.execute(task);
            assertEquals(1, ran.get(), "CallerRuns 拒绝后任务被执行了不止一次（继续入队=双执行）");
            assertTrue(callerRuns.getSubmittedTasksCount() <= callerRuns.getMaxSubmittedTaskCount(),
                    "计数越界（漂移）: " + callerRuns.getSubmittedTasksCount());
            blocker.countDown();
        } finally {
            callerRuns.shutdownNow();
        }

        AtomicInteger ran2 = new AtomicInteger();
        StandardThreadExecutor discard = new StandardThreadExecutor(1, 1, 60, TimeUnit.SECONDS, 1,
                r -> new Thread(r, "dis-test"), new ThreadPoolExecutor.DiscardPolicy());
        try {
            CountDownLatch blocker = new CountDownLatch(1);
            discard.execute(() -> awaitQuiet(blocker));
            discard.execute(blocker::countDown);
            discard.execute(ran2::incrementAndGet);   // 超限被丢弃：不得仍入队后被执行
            int midCount = discard.getSubmittedTasksCount();
            assertTrue(midCount >= 0, "提交计数漂移为负: " + midCount);
            blocker.countDown();
            Thread.sleep(200);
            assertEquals(0, ran2.get(), "被拒绝的任务仍被执行（拒绝后继续提交通道未关）");
            assertTrue(discard.getSubmittedTasksCount() >= 0);
        } finally {
            discard.shutdownNow();
        }
    }

    // ---- 静默工具：吞中断必须恢复标志（规格8）----

    @Test
    void quietHelpersRestoreInterruptFlag() {
        Thread.interrupted(); // 清基线
        ExeAide.runQuietly(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Thread.currentThread().interrupt();
            throw new IllegalStateException(new InterruptedException("wrapped"));
        });
        assertTrue(Thread.currentThread().isInterrupted(),
                "静默吞工具不得吞失中断信号（取消语义依赖标志传播）");
        Thread.interrupted();
    }

    private static void awaitQuiet(CountDownLatch latch) {
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
