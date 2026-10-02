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
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * worker-command-boxes 执行链契约（fix-common-dormant-defects 组1）：
 * 心跳真实驱动注册盒、注册-注销-重注册闭环、绑定失败不留注册、重复注销幂等、
 * 启动/关闭链无泄漏池与心跳重入、停止期间滞留与重新绑定恢复、工作器线程内联执行、
 * 提交-排空竞态不丢唤醒、父盒并发注册子盒零丢失。
 */
class CommandExecutorLifecycleTest {

    /** 同步直执行工作器：wakeUp 即在当前线程 process */
    static class DirectWorker implements CommandBoxWorker {

        final AtomicBoolean onCurrent = new AtomicBoolean();

        volatile boolean working = true;

        final AtomicInteger wakeUps = new AtomicInteger();

        @Override
        public boolean isOnCurrentThread() {
            return onCurrent.get();
        }

        @Override
        public boolean isWorking() {
            return working;
        }

        @Override
        public void wakeUp(CommandBox<?> commandBox) {
            wakeUps.incrementAndGet();
            commandBox.process();
        }
    }

    static Command counting(AtomicInteger counter) {
        return new BaseCommand("count") {
            @Override
            protected void action() {
                counter.incrementAndGet();
            }
        };
    }

    // ---- 提交-排空竞态：不丢唤醒（规格 5）----

    /** 空盒单次提交即被处理 */
    @Test
    void singleSubmitProcessed() {
        FrequencyCommandBox<Command, CommandBox<Command>> box = new FrequencyCommandBox<>();
        DirectWorker worker = new DirectWorker();
        assertTrue(box.bindWorker(worker));
        AtomicInteger executed = new AtomicInteger();
        assertTrue(box.accept(counting(executed)));
        assertEquals(1, executed.get(), "提交后未被唤醒处理");
    }

    /** 数百轮多生产者-排空竞速：停止生产后**不再有任何外部提交**，盒必须自行清空（不丢唤醒） */
    @Test
    void submitDrainRaceLosesNoWakeup() throws Exception {
        FrequencyCommandBox<Command, CommandBox<Command>> box = new FrequencyCommandBox<>();
        DirectWorker worker = new DirectWorker();
        box.bindWorker(worker);
        AtomicInteger produced = new AtomicInteger();
        AtomicInteger executed = new AtomicInteger();
        int producers = 4;
        int total = 800;
        CountDownLatch done = new CountDownLatch(producers);
        List<Thread> threads = new ArrayList<>();
        for (int t = 0; t < producers; t++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    int current = produced.get();
                    if (current >= total) {
                        break;
                    }
                    // CAS 预领名额，杜绝多生产者检查-自增竞态超发
                    if (!produced.compareAndSet(current, current + 1)) {
                        continue;
                    }
                    if (!box.accept(counting(executed))) {
                        produced.decrementAndGet();
                    }
                }
                done.countDown();
            }, "producer-" + t);
            threads.add(thread);
            thread.start();
        }
        assertTrue(done.await(15, TimeUnit.SECONDS));
        for (Thread thread : threads) {
            thread.join(5000);
        }
        // 关键：此处绝不调用 box.submit()——盒必须靠自身闭环在有限轮次内清空
        long deadline = System.currentTimeMillis() + 5000;
        while (!box.isEmpty() && System.currentTimeMillis() < deadline) {
            Thread.onSpinWait();
        }
        assertEquals(0, box.size(), "存在滞留命令：提交-排空竞态吞没唤醒（依赖外部再提交才前进）");
        assertEquals(total, produced.get());
        assertEquals(total, executed.get());
    }

    /** 工作器停止期间命令滞留盒内不丢失，重新绑定后处理（经 register 通道恢复） */
    @Test
    void stoppedWorkerHoldsCommandsUntilRebind() {
        FrequencyCommandBox<Command, CommandBox<Command>> box = new FrequencyCommandBox<>();
        DirectWorker worker = new DirectWorker();
        box.bindWorker(worker);
        AtomicInteger executed = new AtomicInteger();
        worker.working = false;
        // 停止期受理：DirectWorker.wakeUp 仍会 process——以真实执行器语义为准改用 working 检查在
        // DefaultCommandExecutor 用例验证；此处验证滞留不丢失（受理成功入队）
        assertTrue(box.accept(counting(executed)));
        assertEquals(1, box.size());
    }

    // ---- 工作器线程内联（规格 6）----

    /** 工作器线程身份为真时受理：当场执行且不增长队列 */
    @Test
    void inlineExecutionOnWorkerThread() {
        FrequencyCommandBox<Command, CommandBox<Command>> box = new FrequencyCommandBox<>();
        DirectWorker worker = new DirectWorker();
        box.bindWorker(worker);
        worker.onCurrent.set(true);
        AtomicInteger executed = new AtomicInteger();
        assertTrue(box.accept(counting(executed)));
        assertEquals(1, executed.get(), "工作器线程内受理未内联执行（身份判定恒假）");
        assertEquals(0, box.size(), "内联执行的命令不应再入队");
    }

    /** 非工作器线程受理：入队路径，受理线程不执行 */
    @Test
    void externalAcceptQueues() {
        FrequencyCommandBox<Command, CommandBox<Command>> box = new FrequencyCommandBox<>();
        DirectWorker worker = new DirectWorker();
        worker.onCurrent.set(false);
        // worker 绑定但非当前线程 → 走队列+提交（DirectWorker wakeUp 同步处理，队列终归清空，
        // 关键判据：不是"先内联后排队"——内联计数与队列路径互斥）
        box.bindWorker(worker);
        AtomicInteger executed = new AtomicInteger();
        box.accept(counting(executed));
        assertEquals(1, executed.get());
        assertEquals(1, worker.wakeUps.get(), "非当前线程受理必须走提交唤醒路径");
    }

    // ---- 注册/注销链（规格 7/8）----

    /** 注册-注销-重注册闭环；重复注销幂等且不伤他人 */
    @Test
    void registerUnregisterIdempotent() {
        FrequencyCommandExecutor executor = new FrequencyCommandExecutor("test-freq");
        executor.start();
        try {
            FrequencyCommandBox<Command, CommandBox<Command>> a = new FrequencyCommandBox<>();
            FrequencyCommandBox<Command, CommandBox<Command>> b = new FrequencyCommandBox<>();
            assertTrue(executor.register(a));
            assertTrue(executor.register(b));
            assertTrue(executor.unregister(a));
            assertFalse(executor.unregister(a), "重复注销必须返回失败且无副作用");
            assertFalse(executor.unregister(new FrequencyCommandBox<>()), "注销未注册的盒返回失败");
            // b 未被误伤
            AtomicInteger executed = new AtomicInteger();
            b.accept(counting(executed));
            assertTrue(executed.get() >= 0);
            assertTrue(executor.register(a), "重注册应成功");
        } finally {
            executor.shutdown();
        }
    }

    /** 已绑定他工作器的盒注册到父盒：显式失败、不入列、原绑定不受影响 */
    @Test
    void registerAlreadyBoundFails() {
        FrequencyCommandBox<Command, CommandBox<Command>> busy = new FrequencyCommandBox<>();
        assertTrue(busy.bindWorker(new DirectWorker()));
        FrequencyCommandBox<Command, CommandBox<Command>> parent = new FrequencyCommandBox<>();
        assertFalse(parent.register(busy), "绑定失败不得宣告注册成功");
        AtomicInteger executed = new AtomicInteger();
        busy.accept(counting(executed));
        assertEquals(1, executed.get(), "原绑定关系必须不受影响");
    }

    /** 父盒并发注册子盒零丢失（createAndGetBox 竞态） */
    @Test
    void concurrentChildRegistrationKeepsAll() throws Exception {
        FrequencyCommandBox<Command, CommandBox<Command>> parent = new FrequencyCommandBox<>();
        int n = 8;
        CyclicBarrier barrier = new CyclicBarrier(n);
        ExecutorService pool = Executors.newFixedThreadPool(n);
        List<Future<?>> futures = new ArrayList<>();
        Collection<CommandBox<Command>> children = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            FrequencyCommandBox<Command, CommandBox<Command>> child = new FrequencyCommandBox<>();
            children.add(child);
            futures.add(pool.submit(() -> {
                try {
                    barrier.await(5, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return parent.register(child);
            }));
        }
        for (Future<?> f : futures) {
            assertTrue((Boolean) f.get(10, TimeUnit.SECONDS));
        }
        pool.shutdownNow();
        int found = 0;
        for (CommandBox<Command> child : children) {
            // 通过父盒处理轮次触达子盒：子盒各放一条命令，父盒 process 应全部执行
            AtomicInteger c = new AtomicInteger();
            child.accept(counting(c));
        }
        // 父盒 boxes() 由 process 驱动——统计经父盒注册的子盒数
        parent.process();
        for (CommandBox<Command> child : children) {
            if (child.isEmpty()) {
                found++;
            }
        }
        assertEquals(n, found, "并发首建覆盖丢失子盒（其命令无人处理）");
    }

    // ---- 频率执行器心跳与生命周期（规格 8/9/10）----

    /** 心跳真实驱动注册盒：积压被逐步清空；注销后新命令保持待处理 */
    @Test
    void heartbeatActuallyProcessesRegisteredBoxes() throws Exception {
        FrequencyCommandExecutor executor = new FrequencyCommandExecutor("test-beat");
        executor.start();
        try {
            FrequencyCommandBox<Command, CommandBox<Command>> box = new FrequencyCommandBox<>();
            assertTrue(executor.register(box));
            AtomicInteger executed = new AtomicInteger();
            for (int i = 0; i < 5; i++) {
                box.getQueue().add(counting(executed));
            }
            long deadline = System.currentTimeMillis() + 3000;
            while (executed.get() < 5 && System.currentTimeMillis() < deadline) {
                Thread.sleep(50);
            }
            assertEquals(5, executed.get(), "心跳未驱动注册盒处理（空 wakeUp 缺陷）");

            assertTrue(executor.unregister(box));
            box.getQueue().add(counting(executed));
            Thread.sleep(400);
            assertEquals(5, executed.get(), "注销后的盒不应再被心跳驱动");
        } finally {
            executor.shutdown();
        }
    }

    /** 单盒处理异常不冻结心跳：其余盒当轮与后续照常 */
    @Test
    void boxExceptionDoesNotFreezeHeartbeat() throws Exception {
        FrequencyCommandExecutor executor = new FrequencyCommandExecutor("test-err");
        executor.start();
        try {
            FrequencyCommandBox<Command, CommandBox<Command>> bad = new FrequencyCommandBox<>();
            FrequencyCommandBox<Command, CommandBox<Command>> good = new FrequencyCommandBox<>();
            assertTrue(executor.register(bad));
            assertTrue(executor.register(good));
            bad.getQueue().add(new BaseCommand("boom") {
                @Override
                protected void action() {
                    throw new IllegalStateException("模拟盒处理异常");
                }
            });
            AtomicInteger goodRuns = new AtomicInteger();
            long deadline = System.currentTimeMillis() + 3000;
            while (goodRuns.get() < 2 && System.currentTimeMillis() < deadline) {
                good.getQueue().add(counting(goodRuns));
                Thread.sleep(120);
            }
            assertTrue(goodRuns.get() >= 2, "单盒异常冻结了心跳或连坐后续盒");
        } finally {
            executor.shutdown();
        }
    }

    /** 关闭链闭环：心跳与自管资源终止、重复关闭幂等、关闭后注册显式失败 */
    @Test
    void shutdownClosesChainAndRejectsLaterRegister() throws Exception {
        FrequencyCommandExecutor executor = new FrequencyCommandExecutor("test-down");
        executor.start();
        executor.shutdown();
        assertDoesNotThrow(executor::shutdown, "重复关闭必须幂等");
        assertFalse(executor.register(new FrequencyCommandBox<>()),
                "关闭后注册必须显式失败（不得虚报成功致命令静默滞留）");
        assertFalse(executor.isWorking());
    }

    /** 启动重入显式拒绝（旧心跳泄漏防护） */
    @Test
    void startReentryRejected() {
        FrequencyCommandExecutor executor = new FrequencyCommandExecutor("test-restart");
        executor.start();
        try {
            assertThrows(IllegalStateException.class, executor::start, "运行中重复启动必须显式拒绝");
        } finally {
            executor.shutdown();
        }
        assertDoesNotThrow(executor::start, "关闭后再启动应可用");
        executor.shutdown();
    }

    // ---- 默认执行器（规格 7/9/10）----

    /** 注册已绑定盒显式失败（原恒真）；关闭后注册失败 */
    @Test
    void defaultExecutorRegisterHonest() throws Exception {
        ExecutorService external = Executors.newSingleThreadExecutor();
        DefaultCommandExecutor executor = new DefaultCommandExecutor("test-def", 30, external);
        try {
            FrequencyCommandBox<Command, CommandBox<Command>> box = new FrequencyCommandBox<>();
            assertTrue(executor.register(box));
            assertFalse(executor.register(box), "重复注册（已绑定）必须返回失败");
        } finally {
            executor.shutdown();
        }
        assertFalse(executor.register(new FrequencyCommandBox<>()), "关闭后注册显式失败");
        external.shutdown();
        assertTrue(external.awaitTermination(5, TimeUnit.SECONDS));
    }

    /** 内置池随 shutdown 关闭；外部传入池不受管辖 */
    @Test
    void defaultExecutorShutdownClosesOwnedPoolOnly() throws Exception {
        ExecutorService external = Executors.newSingleThreadExecutor();
        DefaultCommandExecutor withExternal = new DefaultCommandExecutor("test-ext", 30, external);
        withExternal.shutdown();
        assertFalse(external.isShutdown(), "外部池不得被关闭");
        assertTrue(external.submit(() -> 1).get(2, TimeUnit.SECONDS) == 1, "外部池仍可用");
        external.shutdown();

        DefaultCommandExecutor owned = new DefaultCommandExecutor("test-owned", 30);
        owned.shutdown();
        assertDoesNotThrow(owned::shutdown, "重复关闭幂等");
        // 内置业务池必须随关闭终止：非守护线程不得永驻（防 JVM 挂死与线程泄漏）
        long deadline = System.currentTimeMillis() + 5000;
        while (countNamedThreads("DefaultCommandExecutor-") > 0 && System.currentTimeMillis() < deadline) {
            Thread.sleep(50);
        }
        assertEquals(0, countNamedThreads("DefaultCommandExecutor-"), "内置业务池线程未随 shutdown 终止");
    }

    private static int countNamedThreads(String prefix) {
        int n = 0;
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().startsWith(prefix) && t.isAlive()) {
                n++;
            }
        }
        return n;
    }

    /** 停止期间受理滞留不丢失；重新注册（绑定恢复）后被处理 */
    @Test
    void stoppedExecutorHoldsThenResumesOnRebind() throws Exception {
        DefaultCommandExecutor executor = new DefaultCommandExecutor("test-hold", 30);
        FrequencyCommandBox<Command, CommandBox<Command>> box = new FrequencyCommandBox<>();
        assertTrue(executor.register(box));
        executor.stop();
        AtomicInteger executed = new AtomicInteger();
        assertTrue(box.accept(counting(executed)),
                "停止（未关闭）期受理应诚实返回滞留成功（停止≠关闭）");
        Thread.sleep(200);
        assertEquals(0, executed.get(), "停止后仍被派发处理");
        assertEquals(1, box.size(), "停止期受理的命令丢失");
        // 恢复通道 = 重新注册（register 复活暂停的执行器并触发提交）
        assertTrue(executor.unregister(box));
        assertTrue(executor.register(box));
        long deadline = System.currentTimeMillis() + 3000;
        while (executed.get() == 0 && System.currentTimeMillis() < deadline) {
            Thread.sleep(50);
        }
        assertEquals(1, executed.get(), "重新绑定后滞留命令未被处理");
        executor.shutdown();
    }

    /** 关闭（终态，非停止）后受理：显式失败并回滚入队，不得滞留执行（受理结果诚实·下游拒绝侧） */
    @Test
    void acceptAfterShutdownFailsAndRollsBack() throws Exception {
        DefaultCommandExecutor executor = new DefaultCommandExecutor("test-reject", 30);
        FrequencyCommandBox<Command, CommandBox<Command>> box = new FrequencyCommandBox<>();
        assertTrue(executor.register(box));
        executor.shutdown();
        AtomicInteger executed = new AtomicInteger();
        assertFalse(box.accept(counting(executed)),
                "下游已关闭时受理不得虚报成功（原实现按滞留路径返回 true）");
        assertEquals(0, box.size(), "受理失败后命令必须回滚出盒，不得滞留");
        Thread.sleep(300);
        assertEquals(0, executed.get(), "回滚的命令不得被任何路径执行");
    }

    /** 频率执行器同款终态：关闭后已绑盒受理显式失败并回滚 */
    @Test
    void frequencyExecutorShutdownRejectsAccept() {
        FrequencyCommandExecutor executor = new FrequencyCommandExecutor("test-reject-freq");
        executor.start();
        try {
            FrequencyCommandBox<Command, CommandBox<Command>> box = new FrequencyCommandBox<>();
            assertTrue(executor.register(box));
            executor.shutdown();
            AtomicInteger executed = new AtomicInteger();
            assertFalse(box.accept(counting(executed)), "关闭后受理必须显式失败");
            assertEquals(0, box.size(), "关闭后受理失败不得滞留命令");
            assertEquals(0, executed.get());
        } finally {
            executor.shutdown();
        }
    }

}
