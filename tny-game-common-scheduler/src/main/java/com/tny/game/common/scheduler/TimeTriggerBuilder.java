package com.tny.game.common.scheduler;

import com.tny.game.common.scheduler.cycle.*;

import java.time.Instant;

/**
 * 时间触发器构建器
 * Created by Kun Yang on 16/2/25.
 */
public class TimeTriggerBuilder<C extends TimeCycle> {

    private C timeCycle = null;

    private Instant startTime = null;

    private Instant previousTime = null;

    private Instant endTime = null;

    private long speedMills = 0;

    private Instant suspendTime = null;

    private TimeTriggerBuilder() {
    }

    public static <TC extends TimeCycle> TimeTriggerBuilder<TC> newBuilder(Class<TC> clazz) {
        return new TimeTriggerBuilder<>();
    }

    public static <TC extends TimeCycle> TimeTriggerBuilder<TC> newBuilder() {
        return new TimeTriggerBuilder<>();
    }

    public TimeTriggerBuilder<C> setPreviousTime(long previousTimeMills) {
        this.previousTime = Instant.ofEpochMilli(previousTimeMills);
        return this;
    }

    public TimeTriggerBuilder<C> setPreviousTime(Instant previousTime) {
        this.previousTime = previousTime;
        return this;
    }

    public TimeTriggerBuilder<C> setStartTime(long StartTimeMills) {
        this.startTime = Instant.ofEpochMilli(StartTimeMills);
        return this;
    }

    public TimeTriggerBuilder<C> setStartTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public TimeTriggerBuilder<C> setEndTime(long endTimeMills) {
        this.endTime = Instant.ofEpochMilli(endTimeMills);
        return this;
    }

    public TimeTriggerBuilder<C> setEndTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public TimeTriggerBuilder<C> setSuspendTime(Instant suspendTime) {
        this.suspendTime = suspendTime;
        return this;
    }

    public TimeTriggerBuilder<C> setSuspendTime(long suspendTime) {
        this.suspendTime = Instant.ofEpochMilli(suspendTime);
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
        return new DefaultTimeTrigger<>(this.startTime, this.previousTime, this.endTime, this.suspendTime, this.timeCycle, this.speedMills, true);
    }

    public TimeTrigger<C> buildStop() {
        return new DefaultTimeTrigger<>(this.startTime, this.previousTime, this.endTime, this.suspendTime, this.timeCycle, this.speedMills, false);
    }

}
