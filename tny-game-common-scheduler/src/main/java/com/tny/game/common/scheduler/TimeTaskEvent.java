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

import java.util.*;

/**
 * @author KGTny
 * @ClassName: TimeTaskEvent
 * @Description: 时间任务触发事件
 * @date 2011-10-28 下午5:03:53
 * <p>
 * 时间任务触发事件
 * <p>
 * <br>
 */
public class TimeTaskEvent {

    /**
     * 时间任务
     *
     * @uml.property name="timeTask"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    private TimeTask timeTask;

    /**
     * 处理器列表
     *
     * @uml.property name="handlerList"
     */
    private List<TimeTaskHandler> handlerList = new ArrayList<>();

    protected TimeTaskEvent(TimeTask timeTask, List<TimeTaskHandler> handlerList) {
        super();
        this.timeTask = timeTask;
        this.handlerList = handlerList;
    }

    /**
     * 获取时间任务 <br>
     *
     * @return 时间任务
     * @uml.property name="timeTask"
     */
    public TimeTask getTimeTask() {
        return this.timeTask;
    }

    /**
     * 获取时间任务处理器 <br>
     *
     * @return 处理器
     */
    public List<TimeTaskHandler> getHandlerList() {
        return Collections.unmodifiableList(this.handlerList);
    }

}
