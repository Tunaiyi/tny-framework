/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
