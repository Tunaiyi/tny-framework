package com.tny.game.common.scheduler;

import com.tny.game.common.scheduler.cycle.TimeCycle;
import org.joda.time.DateTime;

/**
 * 时间触发器构建器
 * Created by Kun Yang on 16/2/25.
 */
public class TimeTriggerBuilder<C extends TimeCycle> {

    private C timeCycle = null;
    private DateTime startTime = null;
    private DateTime previousTime = null;
    private DateTime endTime = null;
    private long speedMills = 0;
    private DateTime suspendTime = null;

    private TimeTriggerBuilder() {
    }

    public static <TC extends TimeCycle> TimeTriggerBuilder<TC> newBuilder(Class<TC> clazz) {
        return new TimeTriggerBuilder<>();
    }

    public static <TC extends TimeCycle> TimeTriggerBuilder<TC> newBuilder() {
        return new TimeTriggerBuilder<>();
    }

    public TimeTriggerBuilder<C> setPreviousTime(long previousTimeMills) {
        this.previousTime = new DateTime(previousTimeMills);
        return this;
    }

    public TimeTriggerBuilder<C> setPreviousTime(DateTime previousTime) {
        this.previousTime = previousTime;
        return this;
    }

    public TimeTriggerBuilder<C> setStartTime(long StartTimeMills) {
        this.startTime = new DateTime(StartTimeMills);
        return this;
    }

    public TimeTriggerBuilder<C> setStartTime(DateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public TimeTriggerBuilder<C> setEndTime(long endTimeMills) {
        this.endTime = new DateTime(endTimeMills);
        return this;
    }

    public TimeTriggerBuilder<C> setEndTime(DateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public TimeTriggerBuilder<C> setSuspendTime(DateTime suspendTime) {
        this.suspendTime = suspendTime;
        return this;
    }

    public TimeTriggerBuilder<C> setSuspendTime(long suspendTime) {
        this.suspendTime = new DateTime(suspendTime);
        return this;
    }

    public TimeTriggerBuilder<C> setSpeedMills(long speedMills) {
        this.speedMills = speedMills;
        return this;
    }

    public TimeTriggerBuilder<C> setTimeCycle(C timeCycle) {
        this.timeCycle = timeCycle;
        return this;
    }

    public TimeTrigger<C> buildStart() {
        return new DefaultTimeTrigger<>(startTime, previousTime, endTime, suspendTime, timeCycle, speedMills, true);
    }

    public TimeTrigger<C> buildStop() {
        return new DefaultTimeTrigger<>(startTime, previousTime, endTime, suspendTime, timeCycle, speedMills, false);
    }

}
