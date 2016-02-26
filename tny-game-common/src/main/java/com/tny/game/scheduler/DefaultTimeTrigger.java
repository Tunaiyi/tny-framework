package com.tny.game.scheduler;

import com.tny.game.common.ExceptionUtils;
import com.tny.game.scheduler.cycle.TimeCycle;
import org.joda.time.DateTime;

class DefaultTimeTrigger<C extends TimeCycle> implements TimeTrigger<C> {

    private C timeCycle = null;
    private DateTime endTime = null;
    private DateTime nextTime = null;
    private DateTime startTime = null;
    private DateTime previousTime = null;
    private long speedMills = 0;

    protected DefaultTimeTrigger(DateTime startTime, DateTime previousTime, DateTime endTime, C timeCycle, long speedMills, boolean start) {
        this.startTime = startTime;
        this.timeCycle = timeCycle;
        this.previousTime = previousTime;
        this.endTime = endTime;
        this.speedMills = speedMills;
        if (start && isCanStart())
            this.start();
    }


    @Override
    public DateTime getStartTime() {
        return startTime;
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

    public boolean isCanStart() {
        return this.nextTime == null && this.startTime != null && this.timeCycle != null;
    }

    @Override
    public boolean start(C timeCycle, DateTime startTime, DateTime endTime) {
        if (this.nextTime != null)
            return false;
        if (startTime == null && this.startTime == null)
            throw new NullPointerException("startTime is null");
        if (timeCycle == null && this.timeCycle == null)
            throw new NullPointerException("timeCycle is null");
        if (endTime != null) {
            ExceptionUtils.checkArgument(endTime.isAfter(startTime));
            this.endTime = endTime;
            this.speedMills = 0;
        }
        if (timeCycle != null)
            this.timeCycle = timeCycle;
        if (startTime != null) {
            this.startTime = startTime;
            this.previousTime = startTime;
        }
        if (this.previousTime == null)
            this.previousTime = this.startTime;
        triggerNext(timeCycle, this.previousTime, false);
        return !this.isFinish();
    }

    @Override
    public void restart(C timeCycle, DateTime startTime, DateTime endTime) {
        if (startTime == null && this.startTime == null)
            throw new NullPointerException("startTime is null");
        if (timeCycle == null && this.timeCycle == null)
            throw new NullPointerException("timeCycle is null");
        if (endTime != null) {
            ExceptionUtils.checkArgument(endTime.isAfter(startTime));
            this.endTime = endTime;
            this.speedMills = 0;
        }
        this.startTime = startTime;
        triggerNext(timeCycle, startTime, false);
    }

    private void triggerNext(C timeCycle, DateTime time, boolean stop) {
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
