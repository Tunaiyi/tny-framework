package com.tny.game.scheduler;

import com.tny.game.common.ExceptionUtils;
import com.tny.game.scheduler.cycle.TimeCycle;
import org.joda.time.DateTime;

class CycleTimeTrigger<C extends TimeCycle> implements TimeTrigger<C> {

    private C timeCycle = null;
    private DateTime endTime = null;
    private DateTime nextTime = null;
    private DateTime previousTime = null;
    private long speedMills = 0;

    protected CycleTimeTrigger(DateTime previousTime, DateTime endTime, C timeCycle, long speedMills, boolean stop) {
        this.endTime = endTime;
        this.speedMills = speedMills;
        operate(timeCycle, previousTime, stop);
    }

    @Override
    public DateTime getNextTime() {
        return nextTime;
    }

    @Override
    public DateTime getPreviousTime() {
        return previousTime;
    }

    @Override
    public DateTime getEndTime() {
        return endTime;
    }

    @Override
    public long getSpeedMills() {
        return speedMills;
    }

    @Override
    public C getTimeCycle() {
        return timeCycle;
    }

    @Override
    public boolean stop() {
        if (this.nextTime != null) {
            this.nextTime = null;
            this.speedMills = 0;
            return true;
        }
        return false;
    }

    @Override
    public void restartAt(C timeCycle, DateTime time, DateTime endTime) {
        if (endTime != null) {
            ExceptionUtils.checkArgument(endTime.isAfter(time));
            this.endTime = endTime;
            this.speedMills = 0;
        }
        operate(timeCycle, time, false);
    }

    private void operate(C timeCycle, DateTime time, boolean stop) {
        if (timeCycle != null)
            this.timeCycle = timeCycle;
        this.previousTime = time;
        if (!stop)
            this.nextTime = getNextTimeAfter(time);
    }

    @Override
    public boolean trigger(long timeMillis) {
        DateTime next = this.nextTime;
        if (next != null && getCheckMills(timeMillis) >= next.getMillis()) {
            this.previousTime = next;
            this.nextTime = getNextTimeAfter(this.previousTime);
            return true;
        }
        return false;
    }

    @Override
    public void speedUp(long timeMills) {
        this.speedMills = Math.max(this.speedMills + timeMills, 0L);
    }

    @Override
    public boolean triggerForce() {
        DateTime next = this.nextTime;
        if (next != null) {
            this.previousTime = next;
            this.nextTime = getNextTimeAfter(this.previousTime);
            return true;
        }
        return false;
    }

    private DateTime getNextTimeAfter(DateTime time) {
        if (timeCycle == null)
            return null;
        DateTime pot = timeCycle.getTimeAfter(time);
        if (this.endTime != null && pot.isAfter(this.endTime)) {
            this.nextTime = null;
            return null;
        }
        return pot;
    }

    private long getCheckMills(long mills) {
        return this.speedMills + mills;
    }

}
