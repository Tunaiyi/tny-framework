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
package com.tny.game.common.scheduler;

import com.tny.game.common.TimeTaskTrigger;
import com.tny.game.common.scheduler.cycle.DurationTimeCycle;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * task-delivery-contracts 契约（fix-common-dormant-defects 组10）：
 * 投递失败可观测（告警含任务时间与处理器名）、批内幂等如实声明、
 * 备份为获取时刻快照、处理器重名显式冲突、进度零时长边界、超期追赶有界合并。
 */
class TaskDeliveryContractTest {

    /** 计数 handler 基类 */
    static class CountingHandler implements TimeTaskHandler {

        final List<String> calls = Collections.synchronizedList(new ArrayList<>());

        private final String name;

        private final HandleType handleType;

        CountingHandler(String name, HandleType handleType) {
            this.name = name;
            this.handleType = handleType;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public HandleType getHandleType() {
            return handleType;
        }

        @Override
        public boolean isHandleWith(TaskReceiverType type) {
            return true;
        }

        @Override
        public void handle(TaskReceiver receiver, long executeTime, TriggerContext context) {
            calls.add(name + "@" + executeTime);
        }
    }

    static class ThrowingHandler extends CountingHandler {

        ThrowingHandler(String name) {
            super(name, HandleType.ALWAYS);
        }

        @Override
        public void handle(TaskReceiver receiver, long executeTime, TriggerContext context) {
            throw new IllegalStateException("handler boom: " + getName());
        }
    }

    /** 可测接收器：暴露受保护投递入口 */
    static class TestReceiver extends TaskReceiver {

        TestReceiver() {
            this.type = DefaultTaskReceiverType.SYSTEM;
        }

        void deliver(Queue<TimeTaskEvent> events) {
            handle(events);
        }
    }

    private static TimeTaskEvent event(long time, TimeTaskHandler... handlers) {
        return new TimeTaskEvent(new TimeTask(Arrays.stream(handlers).map(TimeTaskHandler::getName).toList(), time),
                Arrays.asList(handlers));
    }

    // ---- 投递失败可观测 + 水位推进（at-most-once 声明） ----

    /** 处理器异常：批内其余处理器照常、水位照常推进（失败永久错过=契约，必须留痕） */
    @Test
    void handlerFailureAdvancesWatermarkAndIsObserved() {
        CountingHandler after = new CountingHandler("after", HandleType.ALWAYS);
        ThrowingHandler boom = new ThrowingHandler("boom");
        TestReceiver receiver = new TestReceiver();
        Queue<TimeTaskEvent> events = new ArrayDeque<>(List.of(event(5000, boom, after)));
        assertDoesNotThrow(() -> receiver.deliver(events));
        assertEquals(1, after.calls.size(), "同事件内后续处理器被异常连坐");
        assertEquals(5000, receiver.getLastHandlerTime(), "at-most-once：失败仍推进水位（可观测性由告警承担）");
        assertTrue(events.isEmpty(), "失败事件必须出队，不得阻塞批次");
    }

    /** 批内 ONCE 幂等：同名一次性处理器单批一次、跨批再投递仍会执行（如实声明） */
    @Test
    void onceSemanticsIsBatchScoped() {
        CountingHandler once = new CountingHandler("once", HandleType.ONCE);
        TestReceiver receiver = new TestReceiver();
        receiver.deliver(new ArrayDeque<>(List.of(event(1000, once), event(2000, once))));
        assertEquals(1, once.calls.size(), "同批次 ONCE 应至多一次");
        assertEquals(2000, receiver.getLastHandlerTime());
    }

    // ---- 备份快照 ----

    /** 备份持有获取时刻快照：入队侧后续变更不回灌已备份对象（真实装配路径） */
    @Test
    void backupHoldsSnapshotAtAcquisition() {
        TimeTaskScheduler scheduler = new TimeTaskScheduler(
                new DefaultTimeTaskHandlerHolder(List.of()), null, 10);
        TimeTaskQueue queue = scheduler.getTimeTaskQueue();
        queue.put(new TimeTask(List.of("h1"), 3000));
        SchedulerBackup backup = new SchedulerBackup(scheduler) { };
        queue.put(new TimeTask(List.of("h2"), 4000));
        List<TimeTask> snapshot = backup.getTimeTaskQueue();
        assertEquals(1, snapshot.size(), "备份读到快照之后的入队（活视图漂移）");
        assertNotNull(backup.toString());
    }

    // ---- 处理器重名冲突 ----

    @Test
    void duplicateHandlerNameFailsExplicitly() {
        CountingHandler first = new CountingHandler("dup", HandleType.ALWAYS);
        CountingHandler second = new CountingHandler("dup", HandleType.ALWAYS);
        assertThrows(IllegalStateException.class,
                () -> new DefaultTimeTaskHandlerHolder(List.of(first, second)),
                "重名注册必须显式冲突（原静默覆盖）");
        // 不同名正常
        assertDoesNotThrow(() -> new DefaultTimeTaskHandlerHolder(
                List.of(first, new CountingHandler("other", HandleType.ALWAYS))));
    }

    // ---- 进度边界 ----

    @Test
    void progressIsFiniteForZeroDuration() {
        TimeMeter<DurationTimeCycle> meter = new TriggerContextProbe();
        assertEquals(0d, meter.getProgress(1_700_000_000_000L), 0d,
                "零/负时长进度必须为有限值（原 NaN/±Inf 泄漏给展示层）");
        assertEquals(0d, meter.getTotalProgress(1_700_000_000_000L), 0d);
    }

    /** 无 endTime 的 totalDuration 语义 -1：进度须为有限值 */
    static class TriggerContextProbe implements TimeMeter<DurationTimeCycle> {

        private static final java.time.Instant T = java.time.Instant.ofEpochMilli(1_700_000_000_000L);

        @Override
        public long getSpeedMills() {
            return 0;
        }

        @Override
        public DurationTimeCycle getTimeCycle() {
            return DurationTimeCycle.of(1000);
        }

        @Override
        public java.time.Instant getStartTime() {
            return T;
        }

        @Override
        public java.time.Instant getPreviousTime() {
            return T;
        }

        @Override
        public java.time.Instant getNextTime() {
            // 零时长：next == previous
            return T;
        }

        @Override
        public java.time.Instant getEndTime() {
            return null;
        }

        @Override
        public java.time.Instant getSuspendTime() {
            return null;
        }
    }

    // ---- 超期追赶有界合并 ----

    /** 宕机恢复：超窗口的过期槽位快进合并为至多一次投递，触发器追平到窗口内 */
    @Test
    void overdueTriggersFastForwardIntoSingleDelivery() {
        long now = System.currentTimeMillis();
        long tenMinutesAgo = now - 10 * 60_000L;
        NavigableSet<TimeTaskTrigger> triggers = new java.util.concurrent.ConcurrentSkipListSet<>();
        triggers.add(new TimeTaskTrigger(new DefaultTimeTaskScheme()
                .setCron("0/1 * * * * ?").setTasks(List.of("hA")), tenMinutesAgo));
        triggers.add(new TimeTaskTrigger(new DefaultTimeTaskScheme()
                .setCron("0/1 * * * * ?").setTasks(List.of("hB")), tenMinutesAgo));
        long latestBefore = triggers.first().nextFireTime();
        assertTrue(latestBefore < now - 60_000L, "构造前提：触发点远超 60s 窗口");

        TimeTask catchUp = TimeTaskScheduler.fastForwardOverdue(triggers, now, 60_000L);
        assertNotNull(catchUp, "存在超窗过期时必须产出一次合并任务");
        assertTrue(catchUp.getHandlerList().containsAll(List.of("hA", "hB")),
                "合并任务须汇集全部过期处理器: " + catchUp.getHandlerList());
        // 快进后所有触发器进入窗口内（下一投递不再逐秒追赶）
        assertTrue(triggers.first().nextFireTime() >= now - 60_000L, "触发器未追平");
        // 无过期场景返回 null 且不动状态
        TimeTask none = TimeTaskScheduler.fastForwardOverdue(triggers, now, 60_000L);
        assertNull(none);
    }

    // ---- 任务组注册表查找契约（requirement: 未注册查询显失败含候选集提示 / 别名查找入口不悬空） ----

    /** slf4j-simple 输出走 System.err：定向捕获告警/日志文本（照 ExecutionTraceConsistencyTest.captureErr 手法） */
    private static String captureErr(Runnable body) {
        PrintStream original = System.err;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setErr(new PrintStream(buffer, true));
            body.run();
        } finally {
            System.setErr(original);
        }
        return buffer.toString();
    }

    /** 宽松查找未注册身份（数字标识 + 身份名）返回空/空包装而全程不抛（错误路径的另一侧） */
    @Test
    void lenientLookupOfUnregisteredReturnsEmptyWithoutThrowing() {
        assertNull(TaskReceiverTypes.of("NOT_A_TYPE"), "身份名宽松查找须返回 null（空值）而非抛");
        assertNull(TaskReceiverTypes.of(999_999), "数字标识宽松查找须返回 null（空值）而非抛");
        assertDoesNotThrow(() -> TaskReceiverTypes.of("NOT_A_TYPE"));
        assertDoesNotThrow(() -> TaskReceiverTypes.of(999_999));
        assertEquals(Optional.empty(), TaskReceiverTypes.option("NOT_A_TYPE"), "身份名宽松包装须为空 Optional");
        assertEquals(Optional.empty(), TaskReceiverTypes.option(999_999), "数字标识宽松包装须为空 Optional");
    }

    /** 严格查找未注册身份显式失败并携带被查找身份；畸形文本经保留入口显式失败绝不下标越界（原 ofAlias heads[0] 崩溃） */
    @Test
    void strictLookupCarriesIdentityAndMalformedTextNeverGoesOutOfBounds() {
        // 未注册身份严格查找：显式失败 + 消息含被查找身份
        NullPointerException strictMiss = assertThrows(NullPointerException.class,
                () -> TaskReceiverTypes.check("NOT_A_TYPE"));
        assertTrue(strictMiss.getMessage().contains("NOT_A_TYPE"),
                "严格查找失败须携带被查找身份: " + strictMiss.getMessage());
        // 畸形文本（空串 / 仅分隔符 / 分隔符开头）经严格查找入口：受控失败，绝无下标越界类异常
        for (String malformed : new String[]{"", "$", "$foo"}) {
            Throwable thrown = assertThrows(Throwable.class, () -> TaskReceiverTypes.check(malformed),
                    "畸形文本 \"" + malformed + "\" 须显式失败而非静默返回");
            assertFalse(thrown instanceof IndexOutOfBoundsException,
                    "畸形文本触发下标越界类崩溃（原崩溃路径未消除）: " + thrown);
            assertFalse(thrown.getClass().getName().contains("OutOfBounds"),
                    "畸形文本触发越界类异常: " + thrown.getClass().getName());
            // 保留的宽松入口对同一畸形文本返回空值而完全不抛（调用方可受控分支）
            assertDoesNotThrow(() -> TaskReceiverTypes.of(malformed));
            assertNull(TaskReceiverTypes.of(malformed), "宽松入口对畸形文本应返回空值");
        }
    }

    /** 被移除的按别名公开查找入口不再可达：反射枚举注册表公开静态方法面，无任何“别名文本查找”入口（可验证的缺席） */
    @Test
    void removedAliasLookupEntryIsAbsentFromPublicMethodSurface() {
        Method[] publicMethods = TaskReceiverTypes.class.getMethods();
        Set<String> retained = new HashSet<>();
        for (Method m : publicMethods) {
            if (!Modifier.isStatic(m.getModifiers()) || m.isSynthetic()) {
                continue;
            }
            assertFalse(m.getName().toLowerCase(Locale.ROOT).contains("alias"),
                    "公开静态方法面仍暴露别名查找入口（悬空态未消除）: " + m.getName());
            assertFalse(m.getName().equalsIgnoreCase("ofAlias"),
                    "被裁决移除的 ofAlias 入口仍在方法面");
            retained.add(m.getName());
        }
        // 保留入口齐备：查找能力由身份名与数字标识通道完整承担（严格 check / 宽松 of / 宽松包装 option）
        assertTrue(retained.containsAll(Set.of("check", "of", "option", "all", "enumerator")),
                "保留查找入口签名缺失: " + retained);
    }

    /** 处理器抛异常：告警留痕同时含异常本身、任务到点时刻、处理器身份三要素（原静默残缺路径） */
    @Test
    void handlerFailureTraceCarriesThreeElements() {
        long executeTime = 5000L;
        String expectedTaskTime = new Date(executeTime).toString();
        ThrowingHandler boom = new ThrowingHandler("boom");
        CountingHandler after = new CountingHandler("after", HandleType.ALWAYS);
        TestReceiver receiver = new TestReceiver();
        Queue<TimeTaskEvent> events = new ArrayDeque<>(List.of(event(executeTime, boom, after)));
        String output = captureErr(() -> receiver.deliver(events));
        // 三要素之一：任务到点时刻（Date 文本随 locale 变，用同一 JVM 现算值钉死）
        assertTrue(output.contains(expectedTaskTime), "留痕缺任务到点时刻 [" + expectedTaskTime + "]:\n" + output);
        // 三要素之二：处理器身份（名字 boom）
        assertTrue(output.contains("boom"), "留痕缺处理器身份:\n" + output);
        // 三要素之三：失败原因 / 异常本身（抛出消息 + 异常类型进入栈）
        assertTrue(output.contains("handler boom"), "留痕缺失败原因（异常消息）:\n" + output);
        assertTrue(output.contains("IllegalStateException"), "留痕缺异常本身（类型未入栈）:\n" + output);
        // 同事件后续处理器仍执行（失败不连坐）
        assertEquals(1, after.calls.size(), "留痕路径连带阻断了后续处理器");
    }

    /** 空备份（持久化默认形态读回、内部无内容）描述正常返回并报零任务数（原 size=-1 与“报零”口径不符） */
    @Test
    void emptyBackupDescriptionReportsZeroCountWithoutCrashing() {
        // 无参受保护构造 = 序列化/持久化默认形态：timeTaskQueue 为 null（无任何任务内容）
        SchedulerBackup empty = new SchedulerBackup() { };
        String desc = assertDoesNotThrow(empty::toString, "空备份描述不得崩溃");
        assertTrue(desc.contains("stopTime"), "描述须含停止时间: " + desc);
        assertTrue(desc.contains("timeTaskQueueSize=0"), "空备份须报零任务数（不得为 -1）: " + desc);
        assertFalse(desc.contains("-1"), "空备份描述不得出现 -1 哨兵: " + desc);
    }

    /** 注册表族 N6 钉桩（tasks 6.1）：TaskReceiverTypes 转发六件套行为账 + static 注册块装载现状。
     *  scheduler 侧未并入 basics 泛型中间层（跨模块耦合收益为负，见 verification-group6 遗留登记），
     *  本用例保证后续任何触碰均不回归现状语义。 */
    @Test
    void registryForwardingSixPieceAndStaticBlockLoadingPinned() throws Exception {
        // static 注册块装载：DefaultTaskReceiverType 两枚举常量按数字标识与身份名双通道可达
        assertSame(DefaultTaskReceiverType.SYSTEM, TaskReceiverTypes.of(1), "static 注册块 SYSTEM 数字通道");
        assertSame(DefaultTaskReceiverType.PLAYER, TaskReceiverTypes.of("PLAYER"), "static 注册块 PLAYER 身份通道");
        assertSame(DefaultTaskReceiverType.SYSTEM, TaskReceiverTypes.check(1));
        assertSame(DefaultTaskReceiverType.PLAYER, TaskReceiverTypes.check("PLAYER"));
        assertEquals(Optional.of(DefaultTaskReceiverType.SYSTEM), TaskReceiverTypes.option("SYSTEM"));
        assertEquals(Optional.of(DefaultTaskReceiverType.PLAYER), TaskReceiverTypes.option(2));
        // 严格失败消息模板现状
        assertEquals("获取 ID为 987654321 的 TaskReceiverType 不存在",
                assertThrows(NullPointerException.class, () -> TaskReceiverTypes.check(987654321)).getMessage());
        assertEquals("获取 MISS_RTT TaskReceiverType 不存在",
                assertThrows(NullPointerException.class, () -> TaskReceiverTypes.check("MISS_RTT")).getMessage());
        // 宽松通道未命中现状
        assertNull(TaskReceiverTypes.of(987654321));
        assertEquals(Optional.empty(), TaskReceiverTypes.option("MISS_RTT"));
        // all/enumerator 现状
        assertTrue(TaskReceiverTypes.all().containsAll(Arrays.asList(DefaultTaskReceiverType.values())),
                "static 注册块装载值须在 all() 中");
        assertThrows(UnsupportedOperationException.class,
                () -> TaskReceiverTypes.<TaskReceiverType>all().add(DefaultTaskReceiverType.SYSTEM),
                "all() 现状为不可修改视图");
        assertSame(TaskReceiverTypes.enumerator(), TaskReceiverTypes.enumerator(), "enumerator() 应返回同一 holder");
        // register 入口现状：包私有静态（装载入口 SchedulerEnumClassLoader::createSelector 同包方法引用）
        Method register = TaskReceiverTypes.class.getDeclaredMethod("register", TaskReceiverType.class);
        assertTrue(Modifier.isStatic(register.getModifiers()));
        assertFalse(Modifier.isPublic(register.getModifiers()) || Modifier.isProtected(register.getModifiers()),
                "TaskReceiverTypes.register 现状包私有，不得借收敛提升可见性");
    }

}
