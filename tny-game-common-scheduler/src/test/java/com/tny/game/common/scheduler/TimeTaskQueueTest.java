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

import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TimeTaskQueue 契约（回归修复：maxSize<=0 死循环挂死调度线程；
 * 同执行时间任务整批 handler 静默丢失 → 合并；restore 方向错误保留最旧丢最新）。
 */
class TimeTaskQueueTest {

    private static TimeTask task(long executeTime, String... handlers) {
        return new TimeTask(Arrays.asList(handlers), executeTime);
    }

    private static List<String> names(Collection<TimeTask> list) {
        List<String> out = new ArrayList<>();
        for (TimeTask t : list) {
            out.add(t.getExecuteTime() + ":" + t.getHandlerList());
        }
        return out;
    }

    /** 队列按执行时间降序迭代；满员驱逐最旧（最小 executeTime） */
    @Test
    void descendingOrderAndEvictsOldest() {
        TimeTaskQueue queue = new TimeTaskQueue(2);
        queue.put(task(3000, "c"));
        queue.put(task(1000, "a"));
        queue.put(task(2000, "b"));
        List<String> snapshot = names(queue.getTimeTaskList());
        assertEquals(List.of("3000:[c]", "2000:[b]"), snapshot, "1000 应被驱逐，剩余按时间降序");
        // getTimeTaskHandlerByLast：返回晚于 last 的任务，按执行时间升序（add(0,..) 反转）
        List<TimeTask> due = queue.getTimeTaskHandlerByLast(1500);
        assertEquals(2, due.size());
        assertEquals(2000, due.get(0).getExecuteTime(), "升序：先到点在前");
        assertEquals(3000, due.get(1).getExecuteTime());
    }

    /** 同执行时间再入队：handler 合并进存量任务，不得静默丢失（回归 ConcurrentSkipListSet 去重吞批） */
    @Test
    void sameSecondTaskMergesHandlers() {
        TimeTaskQueue queue = new TimeTaskQueue(10);
        queue.put(task(5000, "A"));
        queue.put(task(5000, "B"));
        assertEquals(1, queue.size());
        TimeTask only = queue.getTimeTaskList().iterator().next();
        assertEquals(List.of("A", "B"), only.getHandlerList(), "同秒 handler 必须合并保留");
    }

    /** maxSize=0（无参构造/配置 0）时 put 不得死循环（回归 while(size>=0) 永真） */
    @Test
    void zeroMaxSizeDoesNotDeadlock() {
        TimeTaskQueue zero = new TimeTaskQueue();
        assertTimeoutPreemptively(Duration.ofMillis(2000), () -> zero.put(task(1000, "x")),
                "maxSize=0 时 put 原实现无限循环");
        assertEquals(1, zero.size());
        TimeTaskQueue negative = new TimeTaskQueue(-5);
        assertTimeoutPreemptively(Duration.ofMillis(2000), () -> negative.put(task(1000, "x")));
    }

    /** restore：输入降序快照，超限保留队首（最新），不得保留尾部最旧（回归取反） */
    @Test
    void restoreKeepsNewest() {
        TimeTaskQueue queue = new TimeTaskQueue(2);
        List<TimeTask> snapshot = new ArrayList<>(List.of(task(3000, "c"), task(2000, "b"), task(1000, "a")));
        queue.restore(snapshot);
        assertEquals(List.of("3000:[c]", "2000:[b]"), names(queue.getTimeTaskList()), "应保留最新两条");
    }

    /** maxSize<=0 时 restore 保留全部（原实现 subList(size,size) 清空吞任务） */
    @Test
    void restoreKeepsAllWhenNoLimit() {
        TimeTaskQueue queue = new TimeTaskQueue();
        queue.restore(new ArrayList<>(List.of(task(3000, "c"), task(1000, "a"))));
        assertEquals(2, queue.size());
    }

    /** addHandlers 去重合并 */
    @Test
    void timeTaskMergeDeduplicatesHandlers() {
        TimeTask task = task(5000, "A", "B");
        task.addHandlers(List.of("B", "C"));
        assertEquals(List.of("A", "B", "C"), task.getHandlerList());
    }

}
