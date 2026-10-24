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

import com.tny.game.common.*;
import com.tny.game.common.utils.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class TimeTask implements Comparable<TimeTask>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * @uml.property name="executeTime"
     */
    private long executeTime;

    /**
     * @uml.property name="handlerList"
     */
    private List<String> handlerList = new ArrayList<>();

    public TimeTask() {
    }

    protected TimeTask(TimeTaskTrigger holder) {
        this.executeTime = holder.nextFireTime() / 1000 * 1000;
        this.addTaskHandler(holder);
    }

    public TimeTask(List<String> handlerList, long executeTime) {
        this.executeTime = executeTime;
        this.handlerList.addAll(handlerList);
    }

    /**
     * @return
     * @uml.property name="executeTime"
     */
    public long getExecuteTime() {
        return this.executeTime;
    }

    public List<String> getHandlerList() {
        return Collections.unmodifiableList(this.handlerList);
    }

    protected void addTaskHandler(TimeTaskTrigger taskModel) {
        this.handlerList.addAll(taskModel.getHandlerList());
    }

    /**
     * 合并处理器列表（同执行时间任务去重时补挂 handler，重复名不叠加）。
     */
    public void addHandlers(Collection<String> handlers) {
        for (String handler : handlers) {
            if (!this.handlerList.contains(handler)) {
                this.handlerList.add(handler);
            }
        }
    }

    @Override
    public int compareTo(TimeTask handler) {
        long value = handler.executeTime - this.executeTime;
        if (value == 0L) {
            return 0;
        }
        return value < 0L ? -1 : 1;
    }

    @Override
    public String toString() {
        return "\nTimeTaskHandlerHolder [executeTime=" +
               DateTimeAide.DATE_TIME_FORMAT.format(Instant.ofEpochMilli(this.executeTime))
               + ", handlerList=" + this.handlerList + "]\n";
    }

    public int size() {
        return this.handlerList.size();
    }

}
