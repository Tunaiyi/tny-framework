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

package com.tny.game.common.worker;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * reduce-code-duplication D3 钉桩（task 3.1）：AbstractFuture 与 FutureTask 两枚 Sync 状态机的现状语义，
 * 在实现收敛（Sync 段下沉包私有共享实现）之前逐路径钉死，重构后期望值一字不改仍须绿。
 * <p>
 * 覆盖：cancel（未开始/运行中/已完成三态）、get 与 get(timeout) 的超时/中断/唤醒、isDone/isCancelled、
 * done() 钩子触发次数、reset/runAndReset 后可重用。两类各自按本类现状钉桩，
 * 不假设两实现互等之外的新行为（D3：仓内零引用、已发布 public 类，只做单一事实源）。
 */
public class FutureStateMachineParityTest {

    private static final long WAIT_MILLIS = 2_000L;

    private static final long GET_TIMEOUT_MILLIS = 200L;

    /**
     * 钩子计次用：AbstractFuture 现状 protected done() 由 Sync 状态迁移点触发。
     */
    private static final class CountedAbstractFuture<V> extends AbstractFuture<V> {

        final AtomicInteger doneCount = new AtomicInteger();

        @Override
        protected void done() {
            this.doneCount.incrementAndGet();
        }
    }

    /**
     * 钩子计次用：FutureTask 现状 protected done() 由 Sync 状态迁移点触发。
     */
    private static final class CountedFutureTask<V> extends FutureTask<V> {

        final AtomicInteger doneCount = new AtomicInteger();

        CountedFutureTask(Callable<V> callable) {
            super(callable);
        }

        @Override
        protected void done() {
            this.doneCount.incrementAndGet();
        }
    }

    /**
     * 等待线程真正挂起（AQS park 态为 WAITING/TIMED_WAITING），保证唤醒/中断钉桩打在阻塞路径上而非抢跑完成。
     */
    private static void waitUntilBlocked(Thread waiter) {
        long deadline = System.currentTimeMillis() + WAIT_MILLIS;
        while (System.currentTimeMillis() < deadline) {
            Thread.State state = waiter.getState();
            if (state == Thread.State.WAITING || state == Thread.State.TIMED_WAITING) {
                return;
            }
            Thread.yield();
        }
        throw new AssertionError("waiter 未能进入挂起态: " + waiter.getState());
    }

    /**
     * 后台线程执行 get()，结果（值或异常）落 ref；供唤醒路径钉桩。
     */
    private static Thread blockingGet(Future<String> future, AtomicReference<Object> outcome) {
        Thread t = new Thread(() -> {
            try {
                outcome.set(future.get());
            } catch (Throwable e) {
                outcome.set(e);
            }
        }, "blocking-get");
        t.start();
        return t;
    }

    // ------------------------------------------------------------------
    // AbstractFuture 现状钉桩（其 Sync 无 RUNNING 态：计算进行中的可观察状态仍是"未置值"）
    // ------------------------------------------------------------------

    @Test
    void afCancelNotStartedThenLateSetIgnored() {
        CountedAbstractFuture<String> future = new CountedAbstractFuture<>();
        assertFalse(future.isDone());
        assertFalse(future.isCancelled());
        assertEquals(0, future.doneCount.get());

        assertTrue(future.cancel(true), "未开始（状态 0，等价计算进行中）cancel 现状恒可取消");
        assertTrue(future.isCancelled());
        assertTrue(future.isDone());
        assertEquals(1, future.doneCount.get());

        future.set("late");
        assertEquals(1, future.doneCount.get(), "已取消后置值走 aggressive-release 分支，不再触发 done");
        assertThrows(CancellationException.class, () -> future.get());
        assertThrows(CancellationException.class, () -> future.get(GET_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
        assertFalse(future.cancel(true), "已取消再 cancel 返回 false");
    }

    @Test
    void afCancelAfterSetReturnsFalseAndValueKept() {
        CountedAbstractFuture<String> future = new CountedAbstractFuture<>();
        future.set("v");
        assertEquals(1, future.doneCount.get());
        assertTrue(future.isDone());
        assertFalse(future.isCancelled());
        assertFalse(future.cancel(true), "已完成 cancel 返回 false");
        assertEquals("v", assertDoesNotThrow(() -> future.get()));
        assertEquals(1, future.doneCount.get());
    }

    @Test
    void afDoubleSetOnlyFirstWins() {
        CountedAbstractFuture<String> future = new CountedAbstractFuture<>();
        future.set("a");
        future.set("b");
        assertEquals(1, future.doneCount.get(), "RAN 态二次 set 直接返回，不再触发 done");
        assertEquals("a", assertDoesNotThrow(() -> future.get()));
    }

    @Test
    void afSetExceptionThenFurtherWritesIgnored() {
        CountedAbstractFuture<String> future = new CountedAbstractFuture<>();
        RuntimeException cause = new RuntimeException("boom");
        future.setException(cause);
        assertEquals(1, future.doneCount.get());
        future.set("late");
        assertEquals(1, future.doneCount.get());
        ExecutionException e = assertThrows(ExecutionException.class, () -> future.get());
        assertSame(cause, e.getCause());
        ExecutionException te = assertThrows(ExecutionException.class,
                                             () -> future.get(GET_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
        assertSame(cause, te.getCause());
    }

    @Test
    void afGetTimeoutOnIncompleteThrows() {
        CountedAbstractFuture<String> future = new CountedAbstractFuture<>();
        assertThrows(TimeoutException.class, () -> future.get(GET_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
        assertEquals(0, future.doneCount.get());
        assertFalse(future.isDone());
    }

    @Test
    void afBlockedGetWakesOnSet() throws Exception {
        CountedAbstractFuture<String> future = new CountedAbstractFuture<>();
        AtomicReference<Object> outcome = new AtomicReference<>();
        Thread t = blockingGet(future, outcome);
        waitUntilBlocked(t);
        future.set("v");
        t.join(WAIT_MILLIS);
        assertEquals("v", outcome.get());
        assertEquals(1, future.doneCount.get());
    }

    @Test
    void afBlockedGetWakesOnCancel() throws Exception {
        CountedAbstractFuture<String> future = new CountedAbstractFuture<>();
        AtomicReference<Object> outcome = new AtomicReference<>();
        Thread t = blockingGet(future, outcome);
        waitUntilBlocked(t);
        assertTrue(future.cancel(false));
        t.join(WAIT_MILLIS);
        assertInstanceOf(CancellationException.class, outcome.get());
        assertEquals(1, future.doneCount.get());
    }

    @Test
    void afBlockedGetInterruptedThrows() throws Exception {
        CountedAbstractFuture<String> future = new CountedAbstractFuture<>();
        AtomicReference<Object> outcome = new AtomicReference<>();
        Thread t = blockingGet(future, outcome);
        waitUntilBlocked(t);
        t.interrupt();
        t.join(WAIT_MILLIS);
        assertInstanceOf(InterruptedException.class, outcome.get());
        assertEquals(0, future.doneCount.get(), "等待者被中断不改变 future 状态、不触发 done");
    }

    @Test
    void afResetFreshReturnsTrue() {
        CountedAbstractFuture<String> future = new CountedAbstractFuture<>();
        assertTrue(future.reset(), "现状 reset 为 CAS(state,0)：状态 0 时 CAS(0,0) 仍成功");
        assertFalse(future.isDone());
        assertEquals(0, future.doneCount.get());
    }

    @Test
    void afResetThenReuse() throws Exception {
        CountedAbstractFuture<String> future = new CountedAbstractFuture<>();
        future.set("a");
        assertEquals("a", future.get());
        assertTrue(future.reset());
        assertFalse(future.isDone());
        assertEquals(1, future.doneCount.get());
        assertThrows(TimeoutException.class, () -> future.get(GET_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
        future.set("b");
        assertEquals(2, future.doneCount.get());
        assertEquals("b", future.get());
    }

    // ------------------------------------------------------------------
    // FutureTask 现状钉桩（含独有 RUNNING 态与 callable 执行路径）
    // ------------------------------------------------------------------

    @Test
    void ftFreshStateMatrix() {
        FutureTask<String> task = new FutureTask<>(() -> "v");
        assertFalse(task.isDone());
        assertFalse(task.isCancelled());
    }

    @Test
    void ftCancelNotStartedRunIgnored() throws Exception {
        AtomicInteger callCount = new AtomicInteger();
        CountedFutureTask<String> task = new CountedFutureTask<>(() -> {
            callCount.incrementAndGet();
            return "v";
        });
        assertTrue(task.cancel(true), "未开始 cancel(true) 现状成功（runner 尚为 null，中断为无副作用）");
        assertTrue(task.isCancelled());
        assertTrue(task.isDone());
        assertEquals(1, task.doneCount.get());

        task.run();
        assertEquals(0, callCount.get(), "已取消 run() CAS(0,RUNNING) 失败，callable 不得执行");
        assertEquals(1, task.doneCount.get());
        assertThrows(CancellationException.class, () -> task.get());
        assertThrows(CancellationException.class, () -> task.get(GET_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
        assertFalse(task.cancel(true));
    }

    @Test
    void ftCancelAfterRunCompletedFalseAndReRunIgnored() throws Exception {
        AtomicInteger callCount = new AtomicInteger();
        CountedFutureTask<String> task = new CountedFutureTask<>(() -> {
            callCount.incrementAndGet();
            return "v";
        });
        task.run();
        assertEquals(1, callCount.get());
        assertEquals(1, task.doneCount.get());
        assertTrue(task.isDone());
        assertFalse(task.isCancelled());
        assertFalse(task.cancel(true), "已完成 cancel 返回 false");
        task.run();
        assertEquals(1, callCount.get(), "RAN 态重复 run() CAS 失败不重执行");
        assertEquals("v", task.get());
    }

    @Test
    void ftCancelWhileRunningInterruptsRunner() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        AtomicBoolean interruptedSeen = new AtomicBoolean();
        CountedFutureTask<String> task = new CountedFutureTask<>(() -> {
            started.countDown();
            try {
                new CountDownLatch(1).await();
                return "not-reached";
            } catch (InterruptedException e) {
                interruptedSeen.set(true);
                throw e;
            }
        });
        Thread runner = new Thread(task, "ft-runner");
        runner.start();
        assertTrue(started.await(WAIT_MILLIS, TimeUnit.MILLISECONDS), "callable 未按时启动");

        assertTrue(task.cancel(true), "RUNNING 态 cancel(true) 现状可取消");
        runner.join(WAIT_MILLIS);
        assertFalse(runner.isAlive());
        assertTrue(interruptedSeen.get(), "cancel(true) 现状中断 runner");
        assertEquals(1, task.doneCount.get(), "done 仅由 innerCancel 触发一次；callable 异常落入 CANCELLED 分支不再计次");
        assertTrue(task.isCancelled());
        assertTrue(task.isDone());
        assertThrows(CancellationException.class, () -> task.get());
    }

    @Test
    void ftGetTimeoutOnNotStartedThrows() {
        CountedFutureTask<String> task = new CountedFutureTask<>(() -> "v");
        assertThrows(TimeoutException.class, () -> task.get(GET_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
        assertEquals(0, task.doneCount.get());
    }

    @Test
    void ftBlockedGetWakesOnRun() throws Exception {
        CountDownLatch gate = new CountDownLatch(1);
        CountedFutureTask<String> task = new CountedFutureTask<>(() -> {
            gate.await();
            return "v";
        });
        AtomicReference<Object> outcome = new AtomicReference<>();
        Thread waiter = blockingGet(task, outcome);
        waitUntilBlocked(waiter);
        gate.countDown();
        Thread runner = new Thread(task, "ft-runner");
        runner.start();
        runner.join(WAIT_MILLIS);
        waiter.join(WAIT_MILLIS);
        assertEquals("v", outcome.get());
        assertEquals(1, task.doneCount.get());
    }

    @Test
    void ftBlockedGetWakesOnCancel() throws Exception {
        CountedFutureTask<String> task = new CountedFutureTask<>(() -> "v");
        AtomicReference<Object> outcome = new AtomicReference<>();
        Thread waiter = blockingGet(task, outcome);
        waitUntilBlocked(waiter);
        assertTrue(task.cancel(false));
        waiter.join(WAIT_MILLIS);
        assertInstanceOf(CancellationException.class, outcome.get());
        assertEquals(1, task.doneCount.get());
    }

    @Test
    void ftBlockedGetInterruptedThrows() throws Exception {
        CountedFutureTask<String> task = new CountedFutureTask<>(() -> "v");
        AtomicReference<Object> outcome = new AtomicReference<>();
        Thread waiter = blockingGet(task, outcome);
        waitUntilBlocked(waiter);
        waiter.interrupt();
        waiter.join(WAIT_MILLIS);
        assertInstanceOf(InterruptedException.class, outcome.get());
        assertEquals(0, task.doneCount.get());
    }

    @Test
    void ftCallableFailureVisibleAsExecutionException() throws Exception {
        RuntimeException boom = new RuntimeException("boom");
        CountedFutureTask<String> task = new CountedFutureTask<>(() -> {
            throw boom;
        });
        task.run();
        assertEquals(1, task.doneCount.get());
        assertTrue(task.isDone());
        assertFalse(task.isCancelled());
        ExecutionException e = assertThrows(ExecutionException.class, () -> task.get());
        assertSame(boom, e.getCause());
        ExecutionException te = assertThrows(ExecutionException.class,
                                             () -> task.get(GET_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
        assertSame(boom, te.getCause());
        assertFalse(task.cancel(true), "失败完成后（RAN 态）cancel 返回 false");
    }

    @Test
    void ftRunAndResetSuccessKeepsInitialStates() throws Exception {
        AtomicInteger runCount = new AtomicInteger();
        CountedFutureTask<Void> task = new CountedFutureTask<>(() -> {
            runCount.incrementAndGet();
            return null;
        });
        assertTrue(task.runAndReset());
        assertEquals(1, runCount.get());
        assertTrue(task.runAndReset());
        assertEquals(2, runCount.get());
        assertEquals(0, task.doneCount.get(), "runAndReset 成功不置结果、不触发 done");
        assertFalse(task.isDone());
        assertFalse(task.isCancelled());
    }

    @Test
    void ftRunAndResetFailureRecordsException() throws Exception {
        RuntimeException boom = new RuntimeException("boom");
        CountedFutureTask<Void> task = new CountedFutureTask<>(() -> {
            throw boom;
        });
        assertFalse(task.runAndReset());
        assertEquals(1, task.doneCount.get());
        assertTrue(task.isDone());
        ExecutionException e = assertThrows(ExecutionException.class, () -> task.get());
        assertSame(boom, e.getCause());
    }

    @Test
    void ftSetDirectlyThenRunIgnored() throws Exception {
        AtomicInteger callCount = new AtomicInteger();
        CountedFutureTask<String> task = new CountedFutureTask<>(() -> {
            callCount.incrementAndGet();
            return "callable";
        });
        task.set("direct");
        assertEquals(1, task.doneCount.get());
        task.run();
        assertEquals(0, callCount.get(), "已置结果（RAN 态）run() CAS 失败，callable 不得执行");
        assertEquals("direct", task.get());
        assertFalse(task.cancel(true));
    }

    @Test
    void ftResetThenReuseReRunsCallable() throws Exception {
        AtomicInteger callCount = new AtomicInteger();
        CountedFutureTask<String> task = new CountedFutureTask<>(() -> "v" + callCount.incrementAndGet());
        task.run();
        assertEquals("v1", task.get());
        assertEquals(1, task.doneCount.get());

        assertTrue(task.reset());
        assertFalse(task.isDone());
        task.run();
        assertEquals(2, callCount.get(), "reset 后状态回 0，run() 现状重新执行 callable");
        assertEquals(2, task.doneCount.get());
        assertEquals("v2", task.get());
    }

    @Test
    void ftProtectedWritersAfterCompletionIgnored() throws Exception {
        CountedFutureTask<String> task = new CountedFutureTask<>(() -> "first");
        task.run();
        task.set("late");
        task.setException(new RuntimeException("late"));
        assertEquals(1, task.doneCount.get(), "完成后的 set/setException 均走 RAN 直接返回分支");
        assertEquals("first", task.get());
    }

}
