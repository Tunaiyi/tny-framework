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
