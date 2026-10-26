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
import com.tny.game.common.scheduler.cycle.CronTimeCycle;
import com.tny.game.common.scheduler.cycle.DurationTimeCycle;
import org.junit.jupiter.api.*;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 周期计算与触发器定序契约：
 * Duration/Cron 的 getTimeAfter、无未来触发点 cron 的受控异常（原裸 NPE 连坐整条调度链）、
 * TimeTaskTrigger compareTo 全序化（原相等返回 -1 违反 Comparable 契约致跳表取 first 顺序未定义）。
 */
class TimeCycleAndTriggerOrderTest {

    private static final long BASE = 1_700_000_000_000L; // 非整点，UTC 2023-11-14T22:13:20Z

    @Test
    void durationTimeCycleArithmetic() {
        DurationTimeCycle cycle = DurationTimeCycle.of(500L);
        assertEquals(BASE + 500, cycle.getTimeAfter(Instant.ofEpochMilli(BASE)).toEpochMilli());
        assertEquals(500L, cycle.getDuration().toMillis());
    }

    /** 小时级 cron 与时区无关：恰为下一整点 */
    @Test
    void hourlyCronIsTimezoneIndependent() throws ParseException {
        CronTimeCycle cycle = CronTimeCycle.of("0 0 * * * ?");
        long nextHour = BASE - BASE % 3_600_000L + 3_600_000L;
        assertEquals(nextHour, cycle.getTimeAfter(Instant.ofEpochMilli(BASE)).toEpochMilli());
    }

    /** 固定年份已过期的 cron：受控 IllegalStateException（回归原裸 NPE） */
    @Test
    void expiredCronThrowsControlled() throws ParseException {
        CronTimeCycle cycle = CronTimeCycle.of("0 0 0 1 1 ? 2020");
        Instant now = Instant.ofEpochMilli(BASE);
        assertThrows(IllegalStateException.class, () -> cycle.getTimeAfter(now));
    }

    private static DefaultTimeTaskScheme scheme(String cron, String... tasks) {
        return new DefaultTimeTaskScheme()
                .setCron(cron)
                .setTasks(tasks.length == 0 ? null : Arrays.asList(tasks));
    }

    /** 两条同刻 cron 触发器：compareTo 反对称、互不相等，跳表可同存两条且 first 稳定 */
    @Test
    void sameSecondTriggersAreTotallyOrdered() {
        long anchor = BASE - BASE % 3_600_000L; // 整点，两条 cron 首触发同秒
        TimeTaskTrigger t1 = new TimeTaskTrigger(scheme("0 0 * * * ?", "h1"), anchor);
        TimeTaskTrigger t2 = new TimeTaskTrigger(scheme("0 0 * * * ? *", "h2"), anchor);
        assertEquals(t1.nextFireTime(), t2.nextFireTime(), "构造前提：首触发同刻");
        assertNotEquals(0, t1.compareTo(t2), "同刻不同实例不得判等（会被跳表去重丢任务）");
        assertEquals(0, t1.compareTo(t2) + t2.compareTo(t1), "compareTo 必须反对称（原实现相等返回 -1）");
        ConcurrentSkipListSet<TimeTaskTrigger> set = new ConcurrentSkipListSet<>();
        set.add(t1);
        set.add(t2);
        assertEquals(2, set.size());
        assertSame(set.first(), t1.compareTo(t2) < 0 ? t1 : t2);
    }

    /** 不同刻的触发器按 nextFireTime 升序 */
    @Test
    void triggersOrderedByFireTime() {
        // 每秒 vs 每分钟：同一锚点下每秒触发更早
        TimeTaskTrigger everySecond = new TimeTaskTrigger(scheme("0/1 * * * * ?", "s"), BASE);
        TimeTaskTrigger hourly = new TimeTaskTrigger(scheme("0 0 * * * ?", "h"), BASE);
        assertTrue(everySecond.compareTo(hourly) < 0);
        assertTrue(hourly.compareTo(everySecond) > 0);
    }

    /** tasks=null 的方案不再 NPE（回归 DefaultTimeTaskScheme.tasks 缺省路径） */
    @Test
    void schemeWithoutTasksConstructs() {
        TimeTaskTrigger trigger = new TimeTaskTrigger(scheme("0 0 * * * ?"), BASE);
        assertTrue(trigger.getHandlerList().isEmpty());
    }

    /** 过期 cron 触发器构造：受控异常（调度器 initSchedule 已改为按方案隔离） */
    @Test
    void expiredCronTriggerConstructionThrows() {
        assertThrows(IllegalStateException.class,
                () -> new TimeTaskTrigger(scheme("0 0 0 1 1 ? 2020", "x"), BASE));
    }

    /** TaskReceiverTypes：默认注册表查询语义 */
    @Test
    void taskReceiverTypesRegistry() {
        assertSame(DefaultTaskReceiverType.SYSTEM, TaskReceiverTypes.check("SYSTEM"));
        assertSame(DefaultTaskReceiverType.PLAYER, TaskReceiverTypes.check(2));
        assertSame(DefaultTaskReceiverType.PLAYER, TaskReceiverTypes.of("PLAYER"));
        assertEquals(Optional.of(DefaultTaskReceiverType.SYSTEM), TaskReceiverTypes.option(1));
        assertTrue(TaskReceiverTypes.all().size() >= 2);
        assertThrows(Exception.class, () -> TaskReceiverTypes.check("NOT_A_TYPE"));
    }

}
