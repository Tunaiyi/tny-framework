package com.tny.game.common.scheduler;

import org.quartz.*;
import org.quartz.impl.triggers.*;

import java.text.ParseException;
import java.util.*;

/**
 * @author KGTny
 * @ClassName: TimeTaskHolder
 * @Description: 时间任务
 * @date 2011-10-28 下午3:50:29
 * <p>
 * <p>
 * <br>
 */
class TimeTaskModel implements Comparable<TimeTaskModel> {

    /**
     * 初始化建筑位置
     *
     * @uml.property name="timeExpression"
     */
    private String timeExpression;

    /**
     * 处理器名称列表
     *
     * @uml.property name="handlerList"
     */

    private List<String> handlerList = new ArrayList<>();

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

    @SuppressWarnings("unchecked")
    protected void setStopTime(long stopTime) throws ParseException {
        Date start = stopTime > 0 ? new Date(stopTime) : new Date();
        this.trigger = (AbstractTrigger<CronTrigger>) TriggerBuilder.newTrigger()
                                                                    .startAt(start)
                                                                    .withSchedule(CronScheduleBuilder.cronSchedule(this.timeExpression))
                                                                    .build();
        CronTriggerImpl cronTrigger = (CronTriggerImpl) this.trigger;
        cronTrigger.setNextFireTime(start);
        cronTrigger.triggered(null);
        this.fireTime = this.trigger.getNextFireTime().getTime();
    }

    /**
     * 触发任务执行 <br>
     */
    public void trigger() {
        this.trigger.triggered(null);
        this.fireTime = this.trigger.getNextFireTime().getTime();
    }

    /**
     * 获取下次时间 <br>
     *
     * @return 返回下次执行时间
     */
    public Date nextFireTime() {
        return new Date(this.fireTime);
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
    public int compareTo(TimeTaskModel o) {
        long value = this.fireTime - o.fireTime;
        return value > 0 ? 1 : -1;
    }

    @Override
    public String toString() {
        return "\nTimeTaskModel [timeExpression=" + this.timeExpression + ", handlerList=" + this.handlerList +
               ", trigger=" + this.fireTime + " - " + this.nextFireTime() + "]\n";
    }

}
