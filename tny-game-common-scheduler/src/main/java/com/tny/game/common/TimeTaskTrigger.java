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

package com.tny.game.common;

import com.tny.game.common.scheduler.*;
import org.quartz.*;
import org.quartz.impl.triggers.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/30 12:10 下午
 */
public class TimeTaskTrigger implements Comparable<TimeTaskTrigger> {

    /**
     * 处理器名称列表
     *
     * @uml.property name="handlerList"
     */

    private List<String> handlerList;

    /**
     * 触发器
     *
     * @uml.property name="trigger"
     * @uml.associationEnd
     */
    private AbstractTrigger<CronTrigger> trigger;

    /**
     * @uml.property name="fireTime"
     */
    private long fireTime;

    private TimeTaskScheme scheme;

    /**
     * 同刻触发的定序序号：保证 compareTo 全序且不同实例互不相等
     * （原实现相等返回 -1，破坏 Comparable 契约，跳表取 first/pollFirst 顺序未定义）。
     */
    private static final AtomicLong SEQ = new AtomicLong();

    private final long sequence = SEQ.incrementAndGet();

    public TimeTaskTrigger(TimeTaskScheme scheme, long stopTime) {
        this.scheme = scheme;
        Date start = stopTime > 0 ? new Date(stopTime) : new Date();
        this.trigger = (AbstractTrigger<CronTrigger>) TriggerBuilder.newTrigger()
                .startAt(start)
                .withSchedule(CronScheduleBuilder.cronSchedule(scheme.getCron()))
                .build();
        CronTriggerImpl cronTrigger = (CronTriggerImpl) this.trigger;
        cronTrigger.setNextFireTime(start);
        List<String> tasks = scheme.getTasks();
        this.handlerList = tasks != null ? new ArrayList<>(tasks) : new ArrayList<>();
        this.trigger();
    }

    /**
     * 触发任务执行 <br>
     */
    public void trigger() {
        this.trigger.triggered(null);
        Date nextFireTime = this.trigger.getNextFireTime();
        if (nextFireTime == null) {
            // cron 无未来触发点（如固定年份已过期）：显式失败，交由调度器按方案隔离，不得 NPE 连坐
            throw new IllegalStateException("cron 方案 [" + this.scheme.getCron() + "] 无未来触发时间");
        }
        this.fireTime = nextFireTime.getTime();
    }

    /**
     * 获取下次时间 <br>
     *
     * @return 返回下次执行时间
     */
    public long nextFireTime() {
        return this.fireTime;
    }

    /**
     * 获取处理器名称列表 <br>
     *
     * @return 返回处理器名称列表
     */
    public Collection<String> getHandlerList() {
        return Collections.unmodifiableCollection(this.handlerList);
    }

    @Override
    public int compareTo(TimeTaskTrigger o) {
        int result = Long.compare(this.fireTime, o.fireTime);
        return result != 0 ? result : Long.compare(this.sequence, o.sequence);
    }

    @Override
    public String toString() {
        return "\nTimeTaskModel [cron=" + scheme.getCron() + ", handlerList=" + this.handlerList +
               ", trigger=" + this.fireTime + " - " + new Date(this.nextFireTime()) + "]\n";
    }

}
