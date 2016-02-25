package com.tny.game.scheduler;

import com.tny.game.scheduler.cycle.TimeCycle;
import org.joda.time.DateTime;

/**
 * Created by Kun Yang on 16/2/25.
 */
public class TimeTriggerBuilder<C extends TimeCycle> {

    private C timeCycle = null;
    private DateTime previousTime = null;
    private DateTime endTime = null;
    private long speedMills = 0;
    private boolean stop = false;

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

    public TimeTriggerBuilder<C> setEndTime(long endTimeMills) {
        this.endTime = new DateTime(endTimeMills);
        return this;
    }

    public TimeTriggerBuilder<C> setEndTime(DateTime endTime) {
        this.endTime = endTime;
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

    public TimeTriggerBuilder<C> setStop() {
        this.stop = true;
        return this;
    }

    public TimeTriggerBuilder<C> setStop(boolean stop) {
        this.stop = stop;
        return this;
    }

    public TimeTrigger<C> build() {
        return new CycleTimeTrigger<>(previousTime == null ? DateTime.now() : null, endTime, timeCycle, speedMills, stop);
    }

    public TimeTrigger<C> buildStoped() {
        return new CycleTimeTrigger<>(previousTime == null ? DateTime.now() : null,  endTime, timeCycle, speedMills, true);
    }

}
