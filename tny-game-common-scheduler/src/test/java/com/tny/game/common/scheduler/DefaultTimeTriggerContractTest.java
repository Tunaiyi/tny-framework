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
package com.tny.game.common.scheduler;

import com.tny.game.common.scheduler.cycle.DurationTimeCycle;
import org.junit.jupiter.api.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DefaultTimeTrigger 修复契约（长于 Integer.MAX_VALUE 毫秒的挂起/延长不再 int 截断、
 * lengthen 成功返回 true、stop 清理挂起态、restart 空 startTime 不再覆盖/NPE、
 * 已结束 trigger 上 suspend 不再 NPE）。固定基准时间，不用真实时钟。
 */
class DefaultTimeTriggerContractTest {

    private static final long BASE = 1_700_000_000_000L;

    private static DurationTimeCycle cycle1000() {
        return DurationTimeCycle.of(1000L);
    }

    private static DefaultTimeTrigger<DurationTimeCycle> started(long cycleMillis, long startTime) {
        return (DefaultTimeTrigger<DurationTimeCycle>) TimeTriggerBuilder
                .<DurationTimeCycle>newBuilder()
                .setTimeCycle(DurationTimeCycle.of(cycleMillis))
                .setStartTime(Instant.ofEpochMilli(startTime))
                .buildStart();
    }

    /** 挂起 30 天后恢复：nextTime/endTime 整体后移完整 30 天（回归 (int) 截断回退数周） */
    @Test
    void resumeAfterLongSuspendKeepsFullDuration() {
        DefaultTimeTrigger<DurationTimeCycle> trigger = started(1000, BASE);
        long suspended = BASE + 500;
        assertTrue(trigger.suspend(Instant.ofEpochMilli(suspended)));
        long resumeAt = suspended + 30L * 86_400_000L; // 30 天，超 Integer.MAX_VALUE 毫秒
        assertTrue(trigger.resume(Instant.ofEpochMilli(resumeAt)));
        assertEquals(BASE + 1000 + 30L * 86_400_000L, trigger.getNextTime().toEpochMilli(),
                "nextTime 应后移完整挂起时长");
    }

    /** 短挂起对照用例（原实现同样通过，作为行为参照） */
    @Test
    void resumeShortSuspend() {
        DefaultTimeTrigger<DurationTimeCycle> trigger = started(1000, BASE);
        trigger.suspend(Instant.ofEpochMilli(BASE + 300));
        assertTrue(trigger.resume(Instant.ofEpochMilli(BASE + 600)));
        assertEquals(BASE + 1000 + 300, trigger.getNextTime().toEpochMilli());
    }

    /** resume 时 endTime 同步后移 */
    @Test
    void resumeShiftsEndTimeToo() {
        DefaultTimeTrigger<DurationTimeCycle> trigger = (DefaultTimeTrigger<DurationTimeCycle>)
                TimeTriggerBuilder.<DurationTimeCycle>newBuilder()
                        .setTimeCycle(cycle1000())
                        .setStartTime(Instant.ofEpochMilli(BASE))
                        .setEndTime(Instant.ofEpochMilli(BASE + 10_000))
                        .buildStart();
        trigger.suspend(Instant.ofEpochMilli(BASE + 100));
        trigger.resume(Instant.ofEpochMilli(BASE + 2100)); // 挂起 2000ms
        assertEquals(BASE + 10_000 + 2000, trigger.getEndTime().toEpochMilli());
    }

    /** lengthen：成功返回 true，nextTime/endTime 各自后移（原实现恒 false） */
    @Test
    void lengthenReturnsTrueAndShifts() {
        DefaultTimeTrigger<DurationTimeCycle> trigger = (DefaultTimeTrigger<DurationTimeCycle>)
                TimeTriggerBuilder.<DurationTimeCycle>newBuilder()
                        .setTimeCycle(cycle1000())
                        .setStartTime(Instant.ofEpochMilli(BASE))
                        .setEndTime(Instant.ofEpochMilli(BASE + 10_000))
                        .buildStart();
        assertTrue(trigger.lengthen(5000));
        assertEquals(BASE + 6000, trigger.getNextTime().toEpochMilli());
        assertEquals(BASE + 15_000, trigger.getEndTime().toEpochMilli());
        // 非法参数仍 false
        assertFalse(trigger.lengthen(0));
        assertFalse(trigger.lengthen(-10));
    }

    /** lengthen 超 Integer.MAX_VALUE 毫秒不再截断 */
    @Test
    void lengthenLongDuration() {
        DefaultTimeTrigger<DurationTimeCycle> trigger = started(1000, BASE);
        long extend = 30L * 86_400_000L;
        assertTrue(trigger.lengthen(extend));
        assertEquals(BASE + 1000 + extend, trigger.getNextTime().toEpochMilli());
    }

    /** 未启动/已结束的 trigger 上 suspend 返回 false（原实现 NPE） */
    @Test
    void suspendOnStoppedTriggerIsFalseNotNpe() {
        DefaultTimeTrigger<DurationTimeCycle> trigger = started(1000, BASE);
        assertTrue(trigger.stop());
        assertDoesNotThrow(() -> trigger.suspend(Instant.ofEpochMilli(BASE + 1)));
        assertFalse(trigger.suspend(Instant.ofEpochMilli(BASE + 1)));
    }

    /** stop 清除挂起态：随后 start 的新周期不会被陈旧 suspendTime 毒化 */
    @Test
    void stopClearsSuspendState() {
        DefaultTimeTrigger<DurationTimeCycle> trigger = started(1000, BASE);
        trigger.suspend(Instant.ofEpochMilli(BASE + 200));
        trigger.stop();
        assertNull(trigger.getSuspendTime(), "stop 应终结挂起态");
        // 重启新周期：trigger 检查（挂起会阻止推进）
        assertTrue(trigger.start(cycle1000(), Instant.ofEpochMilli(BASE + 60_000)));
        assertEquals(BASE + 61_000, trigger.getNextTime().toEpochMilli());
        assertTrue(trigger.trigger(BASE + 61_000), "已重启的 trigger 应正常推进");
    }

    /** restart(null, endTime)：以既有 startTime 为基准校验，不把 startTime 覆盖为 null（回归 B4 NPE） */
    @Test
    void restartWithNullStartTimeUsesExisting() {
        DefaultTimeTrigger<DurationTimeCycle> trigger = (DefaultTimeTrigger<DurationTimeCycle>)
                TimeTriggerBuilder.<DurationTimeCycle>newBuilder()
                        .setTimeCycle(cycle1000())
                        .setStartTime(Instant.ofEpochMilli(BASE))
                        .buildStart();
        assertDoesNotThrow(() -> trigger.restart(null, null, Instant.ofEpochMilli(BASE + 5000)));
        assertEquals(Instant.ofEpochMilli(BASE), trigger.getStartTime());
        assertEquals(BASE + 1000, trigger.getNextTime().toEpochMilli());
    }

    /** misfire 追赶语义：一次 trigger 调用只推进一格（钉死逐格策略，宕机后由调用方循环追赶） */
    @Test
    void triggerAdvancesOneSlotPerCall() {
        DefaultTimeTrigger<DurationTimeCycle> trigger = started(1000, BASE);
        // 时钟跳 2.5 格（next 初始 BASE+1000）：每次调用只前进一格，需连续调用才能追平
        assertTrue(trigger.trigger(BASE + 2500));
        assertEquals(BASE + 2000, trigger.getNextTime().toEpochMilli());
        assertTrue(trigger.trigger(BASE + 2500));
        assertEquals(BASE + 3000, trigger.getNextTime().toEpochMilli());
        assertFalse(trigger.trigger(BASE + 2500));
        assertEquals(BASE + 3000, trigger.getNextTime().toEpochMilli());
    }

    /** speedUp 累加且 trigger 使用虚拟时间 */
    @Test
    void speedUpAccumulatesForTriggerCheck() {
        DefaultTimeTrigger<DurationTimeCycle> trigger = started(1000, BASE);
        assertTrue(trigger.speedUp(1500));
        assertTrue(trigger.speedUp(1000));
        assertEquals(2500, trigger.getSpeedMills());
        // now=BASE，speed 2500 → 有效时间 BASE+2500 ≥ next BASE+1000 → 推进一格
        assertTrue(trigger.trigger(BASE));
        assertEquals(BASE + 2000, trigger.getNextTime().toEpochMilli());
    }

    /** buildStop：不调度（isFinish），start(null...) 补启动（builder 默认语义钉桩） */
    @Test
    void buildStopThenStart() {
        TimeTrigger<DurationTimeCycle> stopped = TimeTriggerBuilder.<DurationTimeCycle>newBuilder()
                .setTimeCycle(cycle1000())
                .setStartTime(Instant.ofEpochMilli(BASE))
                .buildStop();
        assertNull(stopped.getNextTime());
        assertEquals(Instant.ofEpochMilli(BASE), stopped.getStartTime());
        assertTrue(stopped.start());
        assertEquals(BASE + 1000, stopped.getNextTime().toEpochMilli());
    }

    /** builder 缺 timeCycle 时 buildStart 静默不启动（B9-② 现状记录） */
    @Test
    void buildStartWithoutCycleStaysUnstarted() {
        TimeTrigger<DurationTimeCycle> trigger = TimeTriggerBuilder.<DurationTimeCycle>newBuilder()
                .setStartTime(Instant.ofEpochMilli(BASE))
                .buildStart();
        assertNull(trigger.getNextTime(), "无 timeCycle 的 buildStart 静默返回未启动 trigger（应告警——契约缺陷记录）");
    }

}
