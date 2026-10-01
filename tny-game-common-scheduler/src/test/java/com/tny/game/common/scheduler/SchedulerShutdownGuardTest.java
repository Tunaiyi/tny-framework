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

import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TimeTaskScheduler 关闭守卫契约（task-delivery-contracts 二轮 REAL_GAP 4）：
 * 关闭是终态——进行中的 run 链在 state=false 后必须止损：不再向监听器回调、
 * 不再向任务队列入队、不再向已关池排任务；reload 在关闭后保持惰性不复活投递。
 */
class SchedulerShutdownGuardTest {

    private static TimeTaskSchemesSetting everySecondSetting() {
        return () -> List.of(new DefaultTimeTaskScheme()
                .setCron("0/1 * * * * ?")
                .setTasks(List.of("tick")));
    }

    /** 反射构造私有在途链步（不改动公共 API，专测"关闭后仍有一次在途 run"的止损） */
    private static Runnable newChainStep(TimeTaskScheduler scheduler, TimeTask task) throws Exception {
        Class<?> stepClass = Class.forName(
                "com.tny.game.common.scheduler.TimeTaskScheduler$CreateTimeTaskRunnable");
        Constructor<?> ctor = stepClass.getDeclaredConstructor(TimeTaskScheduler.class, TimeTask.class);
        ctor.setAccessible(true);
        return (Runnable) ctor.newInstance(scheduler, task);
    }

    /**
     * 红基线用例：关闭后的在途链步不得产生回调/入队。
     * 修复前：run() 无 state 守卫——照旧 stopTime 覆写、队列 put、fireTrigger 回调监听器。
     */
    @Test
    void inFlightChainStepStopsAfterShutdown() throws Exception {
        AtomicInteger fired = new AtomicInteger();
        TimeTaskScheduler scheduler = new TimeTaskScheduler(
                new DefaultTimeTaskHandlerHolder(List.of()), null, 10);
        scheduler.addListener(timeTask -> fired.incrementAndGet());
        scheduler.shutdown();
        assertFalse(scheduler.isStart(), "关闭后 isStart 必须为 false");

        Runnable step = newChainStep(scheduler,
                new TimeTask(new ArrayList<>(List.of("tick")), System.currentTimeMillis()));
        step.run();

        assertEquals(0, fired.get(), "已关调度器的在途链步仍触发了监听器回调（state 守卫缺失）");
        assertEquals(0, scheduler.getTimeTaskQueue().size(),
                "已关调度器的在途链步仍向任务队列入队");
    }

    /**
     * 活体链闭环：启动→观测到回调→静默窗（无在途 run）→关闭→有界等待内零新回调；
     * 关闭后 reload 惰性（不复活投递）。
     */
    @Test
    void noCallbacksAfterQuiescentShutdownAndReloadStaysInert() throws Exception {
        AtomicInteger fired = new AtomicInteger();
        TimeTaskScheduler scheduler = new TimeTaskScheduler(
                new DefaultTimeTaskHandlerHolder(List.of()), null, 10);
        scheduler.addListener(timeTask -> fired.incrementAndGet());
        scheduler.start(everySecondSetting());
        assertTrue(scheduler.isStart());

        long deadline = System.currentTimeMillis() + 8_000L;
        int first = fired.get();
        while (fired.get() == first && System.currentTimeMillis() < deadline) {
            Thread.sleep(50);
        }
        assertTrue(fired.get() > first, "有界等待内未观测到任何投递回调（测试前提：链在跑）");

        // 静默窗：等当前链步的尾段（重排下一次 schedule）落地，确认此刻无在途 run
        Thread.sleep(400);
        scheduler.shutdown();
        int atShutdown = fired.get();
        Thread.sleep(3_500);
        assertEquals(atShutdown, fired.get(), "关闭后仍出现监听器回调（链未止损）");

        // 关闭后 reload：state 已为 false，必须惰性不复活
        scheduler.reload(everySecondSetting());
        assertFalse(scheduler.isStart(), "关闭后 reload 不得复活调度链");
        int afterReload = fired.get();
        Thread.sleep(2_500);
        assertEquals(afterReload, fired.get(), "关闭后 reload 复活了投递");
    }

}
