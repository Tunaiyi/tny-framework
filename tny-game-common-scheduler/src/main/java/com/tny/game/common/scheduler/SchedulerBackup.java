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

import java.io.Serializable;
import java.util.*;

public abstract class SchedulerBackup implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * @uml.property name="stopTime"
     */
    private long stopTime;

    /**
     * @uml.property name="timeTaskQueue"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    private Collection<TimeTask> timeTaskQueue;

    protected SchedulerBackup() {
    }

    protected SchedulerBackup(TimeTaskScheduler scheduler) {
        this.stopTime = scheduler.getStopTime();
        TimeTaskQueue queue = scheduler.getTimeTaskQueue();
        // 获取时刻不可变快照（原持活视图：备份对象跨线程序列化读到继续演化的队列）
        this.timeTaskQueue = new ArrayList<>(queue.getTimeTaskList());
    }

    /**
     * @return
     * @uml.property name="stopTime"
     */
    protected long getStopTime() {
        return this.stopTime;
    }

    /**
     * @return
     * @uml.property name="timeTaskQueue"
     */
    protected List<TimeTask> getTimeTaskQueue() {
        return new ArrayList<>(this.timeTaskQueue);
    }

    @Override
    public String toString() {
        // 空/持久化默认形态（timeTaskQueue 为 null，无任何任务内容）报零任务数而非 -1 哨兵（契约：空备份报零计数）
        return "SchedulerBackup [stopTime=" + new Date(this.stopTime) + ", timeTaskQueueSize="
               + (this.timeTaskQueue == null ? 0 : this.timeTaskQueue.size()) + "]";
    }

}
