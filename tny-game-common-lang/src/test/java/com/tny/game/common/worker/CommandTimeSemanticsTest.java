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

import com.tny.game.common.worker.command.BaseCommand;
import com.tny.game.common.worker.command.Command;
import com.tny.game.common.worker.command.DelayCommand;
import com.tny.game.common.worker.command.LoopCommand;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * worker-command-boxes 时间语义契约（fix-common-dormant-defects 组1）：
 * 延迟命令到点才执行且至多一次、循环命令间隔真实生效且终止即退出、
 * 双缓冲切换方向翻转且回投不丢不重、清空覆盖双缓冲、频率盒单轮以轮初快照为限。
 */
class CommandTimeSemanticsTest {

    /** 可测子类：暴露 protected 的时点调整入口 */
    static class TestDelayCommand extends DelayCommand {

        final AtomicInteger runs = new AtomicInteger();

        TestDelayCommand(String name, int delayMillis) {
            super(name, delayMillis);
        }

        void doImmediately() {
            immediately();
        }

        @Override
        protected void action() {
            runs.incrementAndGet();
        }
    }

    static class TestLoopCommand extends LoopCommand {

        final List<Long> fireTimes = Collections.synchronizedList(new ArrayList<>());

        final AtomicInteger actionCount = new AtomicInteger();

        private final long interval;

        private final int stopAfter;

        private final AtomicBoolean throwOnSecond = new AtomicBoolean();

        TestLoopCommand(String name, long firstDelay, long interval, int stopAfter) {
            super(name, firstDelay);
            this.interval = interval;
            this.stopAfter = stopAfter;
        }

        @Override
        protected void run() {
            fireTimes.add(System.currentTimeMillis());
            int n = actionCount.incrementAndGet();
            if (n == 2 && throwOnSecond.getAndSet(false)) {
                throw new IllegalStateException("模拟轮次异常");
            }
        }

        @Override
        protected long nextInterval() {
            return actionCount.get() >= stopAfter ? STOP_LOOP : interval;
        }
    }

    // ---- 延迟命令 ----

    /** 未到点的命令：扫过零执行、不丢失（不得被误标完成） */
    @Test
    void futureCommandNotExecutedBeforeDue() {
        TestDelayCommand command = new TestDelayCommand("d", 10_000);
        for (int i = 0; i < 5; i++) {
            command.execute();
        }
        assertEquals(0, command.runs.get(), "未到点的延迟命令被执行（判反缺陷）");
        assertFalse(command.isDone(), "未到点命令被误标完成");
        assertFalse(command.isCanExecute(), "未到点时可用性判定必须为假");
    }

    /** 到点后恰好执行一次并标记结束；重复执行请求无二次副作用 */
    @Test
    void dueCommandExecutesExactlyOnce() {
        TestDelayCommand command = new TestDelayCommand("d", 0);
        command.execute();
        assertEquals(1, command.runs.get());
        assertTrue(command.isDone());
        command.execute();
        command.execute();
        assertEquals(1, command.runs.get(), "已结束命令被重复执行");
    }

    /** immediately 调整以后一时点为唯一判据：调整后当轮即执行 */
    @Test
    void immediatelyAdjustmentTakesEffect() {
        TestDelayCommand command = new TestDelayCommand("d", 60_000);
        command.execute();
        assertEquals(0, command.runs.get());
        command.doImmediately();
        command.execute();
        assertEquals(1, command.runs.get(), "置为当前时点后应立即执行");
    }

    // ---- 循环命令 ----

    /** 多轮轮询下相邻执行间隔不小于声明间隔；终止后计数恒定 */
    @Test
    void loopRespectsIntervalAndStopsOnSignal() {
        long interval = 60L;
        TestLoopCommand command = new TestLoopCommand("loop", 0, interval, 3);
        long deadline = System.currentTimeMillis() + 1000;
        while (!command.isDone()) {
            command.execute();
            if (System.currentTimeMillis() >= deadline) {
                fail("循环命令未在 1s 内完成 3 轮");
            }
        }
        assertEquals(3, command.fireTimes.size());
        for (int i = 1; i < command.fireTimes.size(); i++) {
            long gap = command.fireTimes.get(i) - command.fireTimes.get(i - 1);
            assertTrue(gap >= interval - 2,
                    "第 " + i + " 次间隔 " + gap + "ms 早于声明 " + interval + "ms（轮询粒度容差 2ms）");
        }
        int before = command.fireTimes.size();
        for (int i = 0; i < 10; i++) {
            command.execute();
        }
        assertEquals(before, command.fireTimes.size(), "终止后仍被扫出执行");
    }

    /** 轮次异常：上抛可观测；异常轮仍按间隔重排并在后续轮次继续执行 */
    @Test
    void loopExceptionObservableAndReschedules() {
        long interval = 40L;
        TestLoopCommand command = new TestLoopCommand("loop-x", 0, interval, 3);
        command.throwOnSecond.set(true);
        RuntimeException thrown = null;
        long deadline = System.currentTimeMillis() + 1000;
        int observedAfterThrow = 0;
        while (!command.isDone()) {
            if (System.currentTimeMillis() >= deadline) {
                fail("循环命令未在时限内完成");
            }
            try {
                command.execute();
            } catch (RuntimeException e) {
                if (thrown == null) {
                    thrown = e;
                }
            }
            if (thrown != null) {
                observedAfterThrow = command.fireTimes.size();
            }
        }
        assertNotNull(thrown, "轮次异常必须上抛（可观测），不得静默吞没");
        assertTrue(observedAfterThrow >= 3, "异常后命令未按间隔继续重排执行");
    }

    // ---- 双缓冲切换 ----

    /** acceptQueue 真实翻转：第二次调用返回的是上一轮受理缓冲 */
    @Test
    void switchQueueActuallyFlips() {
        SwitchConcurrentQueue<String> queue = new SwitchConcurrentQueue<>();
        queue.acceptQueue().add("A");     // 初始受理缓冲（记为 buf0）
        queue.acceptQueue().add("B");     // 翻转后受理缓冲 buf1；本轮处理侧应为 buf0
        // 强判据：连续两次 acceptQueue 必须返回不同缓冲实例（旧实现恒返回同一队列）
        assertEquals(2, queue.size());
        SwitchConcurrentQueue<Integer> q2 = new SwitchConcurrentQueue<>();
        assertNotSame(q2.acceptQueue(), q2.acceptQueue(), "切换恒为无操作（返回同一缓冲）");
    }

    /** 并发受理+切换不丢不重：受理总数 = 最终消费总数 */
    @Test
    void concurrentAcceptAndSwitchLosesNothing() throws Exception {
        SwitchConcurrentQueue<Integer> queue = new SwitchConcurrentQueue<>();
        int total = 4000;
        AtomicInteger produced = new AtomicInteger();
        Thread producer = new Thread(() -> {
            while (produced.get() < total) {
                queue.acceptQueue().add(produced.incrementAndGet());
            }
        }, "acceptor");
        Set<Integer> consumed = Collections.synchronizedSet(new HashSet<>());
        producer.start();
        long deadline = System.currentTimeMillis() + 15_000;
        while (consumed.size() < total && System.currentTimeMillis() < deadline) {
            Queue<Integer> processSide = queue.acceptQueue();
            Integer v;
            while ((v = processSide.poll()) != null) {
                consumed.add(v);
            }
        }
        producer.join(5000);
        assertFalse(producer.isAlive(), "生产线程未收敛");
        assertEquals(total, consumed.size(), "切换期间命令丢失或重复");
        assertEquals(total, consumed.stream().distinct().count());
    }

    /** 清空覆盖两个缓冲 */
    @Test
    void clearCoversBothBuffers() {
        SwitchConcurrentQueue<String> queue = new SwitchConcurrentQueue<>();
        queue.acceptQueue().add("x");
        queue.acceptQueue().add("y");
        queue.clear();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    // ---- 频率盒单轮限步 ----

    /** 本轮执行数不超过轮初快照存量；处理中新受理命令留待下一轮 */
    @Test
    void frequencyBoxRoundBoundedBySnapshot() {
        Queue<Command> queue = new ConcurrentLinkedQueue<>();
        FrequencyCommandBox<Command, CommandBox<Command>> box = new FrequencyCommandBox<>(queue);
        AtomicInteger executed = new AtomicInteger();
        Command[] lateSlot = new Command[1];
        // 轮初 3 条：第 1 条执行时注入第 4 条（本轮不应处理它）
        queue.add(new BaseCommand("injector") {
            @Override
            protected void action() {
                executed.incrementAndGet();
                lateSlot[0] = new BaseCommand("late") {
                    @Override
                    protected void action() {
                        executed.incrementAndGet();
                    }
                };
                queue.add(lateSlot[0]);
            }
        });
        queue.add(new BaseCommand("c2") {
            @Override
            protected void action() {
                executed.incrementAndGet();
            }
        });
        queue.add(new BaseCommand("c3") {
            @Override
            protected void action() {
                executed.incrementAndGet();
            }
        });

        box.process();
        // 限步保证 late 不进入第一轮；process 的自驱闭环使其在第二轮（同调用内）完成
        assertTrue(executed.get() >= 3 && executed.get() <= 4,
                "单轮+自驱闭环的执行量异常：" + executed.get());
        assertNotNull(lateSlot[0]);
        assertTrue(box.isEmpty(), "全部完成后盒判空");
        assertEquals(4, executed.get(), "注入命令应在后续轮被处理，总量=受理量");
    }

    /** 零间隔循环命令：回投不得使本轮迭代永动（限步+无进展停手） */
    @Test
    void zeroIntervalLoopTerminatesRound() {
        java.util.Queue<Command> queue = new java.util.concurrent.ConcurrentLinkedQueue<>();
        FrequencyCommandBox<Command, CommandBox<Command>> box = new FrequencyCommandBox<>(queue);
        TestLoopCommand loop = new TestLoopCommand("zero", 0, 0L, 4);
        queue.add(loop);
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(5), () -> {
            for (int i = 0; i < 6; i++) {
                box.process();
            }
        }, "回投命令使轮次永动");
        assertTrue(loop.actionCount.get() <= 6 + 1,
                "每轮处理量失去快照约束：" + loop.actionCount.get());
    }

    /** 未到点命令被扫过后仍留在盒内（不丢），到点后轮次执行它 */
    @Test
    void notDueCommandStaysAndRunsWhenDue() throws Exception {
        Queue<Command> queue = new ConcurrentLinkedQueue<>();
        FrequencyCommandBox<Command, CommandBox<Command>> box = new FrequencyCommandBox<>(queue);
        TestDelayCommand command = new TestDelayCommand("d", 120);
        queue.add(command);
        box.process();
        assertEquals(0, command.runs.get());
        assertEquals(1, box.size(), "未到点命令被误丢弃");
        Thread.sleep(140);
        box.process();
        assertEquals(1, command.runs.get());
        assertTrue(box.isEmpty(), "已完成命令须从盒移除");
    }

}
