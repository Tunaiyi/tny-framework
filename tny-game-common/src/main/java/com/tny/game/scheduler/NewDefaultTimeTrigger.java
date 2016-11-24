package com.tny.game.scheduler;

import com.tny.game.common.ExceptionUtils;
import com.tny.game.scheduler.cycle.TimeCycle;
import org.joda.time.DateTime;

class NewDefaultTimeTrigger<C extends TimeCycle> implements TimeTrigger<C> {

    private C timeCycle = null;
    private DateTime progressStartTime = null;
    private DateTime previousTime = null;
    private DateTime nextTime = null;
    private DateTime progressNextTime = null;
    private DateTime progressPreviousTime = null;
    private DateTime progressEndTime = null;
    private long speedUpMills = 0;
    private long suspendMills = 0;
    private boolean currentSuspended = false;
    private DateTime suspendAt = null;

    protected NewDefaultTimeTrigger(DateTime progressStartTime, DateTime progressEndTime,
                                    DateTime progressPreviousTime, DateTime progressNextTime,
                                    DateTime previousTime, DateTime nextTime, DateTime suspendAt,
                                    C timeCycle, long speedMills, long suspendMills) {
        this.timeCycle = timeCycle;
        this.progressStartTime = progressStartTime;
        this.progressEndTime = progressEndTime;
        this.progressPreviousTime = progressPreviousTime;
        this.progressNextTime = progressNextTime;
        this.previousTime = previousTime;
        this.nextTime = nextTime;
        this.speedUpMills = speedMills;
        this.suspendAt = suspendAt;
        this.suspendMills = suspendMills;
    }

    @Override
    public boolean start(C timeCycle, DateTime startTime, DateTime endTime) {
        if (this.nextTime != null && this.progressNextTime != null)
            return false;
        this.restart(timeCycle, startTime, endTime);
        return !this.isFinish();
    }

    @Override
    public void restart(C timeCycle, DateTime startTime, DateTime endTime) {
        if (startTime == null && this.progressStartTime == null)
            throw new NullPointerException("progressStartTime is null");
        if (startTime == null)
            startTime = this.progressStartTime;
        if (timeCycle == null && this.timeCycle == null)
            throw new NullPointerException("timeCycle is null");
        if (timeCycle == null)
            timeCycle = this.timeCycle;
        if (endTime != null) {
            ExceptionUtils.checkArgument(endTime.isAfter(startTime));
            this.progressEndTime = endTime;
        }
        this.timeCycle = timeCycle;
        this.progressStartTime = startTime;
        this.progressPreviousTime = startTime;
        this.previousTime = startTime;
        this.reset();
        setup(timeCycle, this.progressPreviousTime, false);
    }

    private void setup(C timeCycle, DateTime time, boolean stop) {
        if (timeCycle != null)
            this.timeCycle = timeCycle;
        this.previousTime = time;
        if (!stop && (this.nextTime == null || this.progressNextTime == null)) {
            this.triggerNext(this.progressStartTime);
        }
    }

    private DateTime getNextTimeAfter(DateTime time) {
        if (timeCycle == null)
            return null;
        DateTime pot = timeCycle.getTimeAfter(time);
        if (this.progressEndTime != null && pot.isAfter(this.progressEndTime)) {
            return null;
        }
        return pot;
    }

    @Override
    public DateTime getStartTime() {
        return progressStartTime;
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
        if (progressEndTime == null)
            return null;
        if (this.speedUpMills == 0)
            return progressEndTime;
        DateTime endTime = progressEndTime.minus(this.speedUpMills);
        DateTime preTime = progressPreviousTime;
        if (preTime == null)
            return endTime;
        return endTime.isBefore(preTime) ? preTime : endTime;
    }

    public long getSpeedUpMills() {
        return speedUpMills;
    }

    @Override
    public C getTimeCycle() {
        return timeCycle;
    }

    @Override
    public boolean stop() {
        if (this.nextTime != null || this.progressNextTime != null) {
            this.nextTime = null;
            this.progressNextTime = null;
            this.reset();
            return true;
        }
        return false;
    }

    public boolean isCanStart() {
        return (this.nextTime == null || this.progressNextTime == null) && this.progressStartTime != null && this.timeCycle != null;
    }

    private void reset() {
        this.suspendAt = null;
        this.suspendMills = 0;
        this.speedUpMills = 0;
    }

    private void triggerNext(DateTime dateTime) {
        DateTime progressNextTime = this.progressNextTime;
        this.progressPreviousTime = progressNextTime;
        progressNextTime = this.progressNextTime = getNextTimeAfter(this.progressNextTime);
        if (progressNextTime != null) {
            this.previousTime = dateTime;
            long nextDuration = progressNextTime.getMillis() - this.speedUpMills - nextTime.getMillis();
            this.nextTime = dateTime.plus(Math.max(nextDuration, 0));
            this.currentSuspended = false;
        } else {
            this.nextTime = null;
            this.currentSuspended = false;
        }
    }

    @Override
    public long getTotalDuration() {
        DateTime dateTime = this.getStartTime();
        DateTime endTime = this.getEndTime();
        if (endTime == null)
            return -1L;
        return Math.max(endTime.getMillis() - dateTime.getMillis(), 0L);
    }

    @Override
    public long getDuration() {
        DateTime previous = this.getPreviousTime();
        DateTime next = this.getNextTime();
        if (previous == null || next == null)
            return -1;
        return next.getMillis() - previous.getMillis();
    }


    @Override
    public boolean trigger(long timeMillis) {
        DateTime nextTime = this.nextTime;
        DateTime suspend = this.suspendAt;
        if (progressNextTime == null && suspend == null && timeMillis >= nextTime.getMillis()) {
            this.triggerNext(nextTime);
            return true;
        }
        return false;
    }

    @Override
    public boolean triggerForce(long timeMillis) {
        DateTime nextTime = this.nextTime;
        DateTime suspend = this.suspendAt;
        DateTime time = new DateTime(timeMillis);
        if (progressNextTime == null && suspend == null) {
            this.triggerNext(time.isBefore(nextTime) ? time : nextTime);
            return true;
        }
        return false;
    }

    @Override
    public boolean speedUp(long speedUpMills) {
        if (!isWorking() || speedUpMills <= 0)
            return false;
        this.speedUpMills = Math.max(this.speedUpMills + speedUpMills, 0L);
        return true;
    }

    public DateTime getSuspendAt() {
        return this.suspendAt;
    }

    @Override
    public boolean resume(DateTime time) {
        DateTime nextTime = this.progressNextTime;
        DateTime suspendTime = this.suspendAt;
        if (nextTime == null || suspendTime == null || suspendTime.isAfter(time))
            return false;
        long suspendMills = Math.max(time.getMillis() - suspendTime.getMillis(), 0);
        this.progressNextTime = nextTime.plusMillis((int) suspendMills);
        this.nextTime = nextTime.plusMillis((int) suspendMills);
        DateTime endTime = this.progressEndTime;
        if (endTime != null)
            this.progressEndTime = endTime.plusMillis((int) suspendMills);
        this.suspendAt = null;
        this.suspendMills += suspendMills;
        return true;
    }

    @Override
    public boolean suspend(DateTime time) {
        if (!isWorking() && time.plusMillis((int) this.getSpeedUpMills())
                .isAfter(this.getNextTime()))
            return false;
        this.suspendAt = time;
        this.currentSuspended = true;
        return true;
    }

    @Override
    public boolean lengthen(long timeMillis) {
        if (isFinish() || timeMillis <= 0)
            return false;
        DateTime progressNextTime = this.progressNextTime;
        DateTime endTime = this.progressEndTime;
        this.progressNextTime = progressNextTime.plusMillis((int) timeMillis);
        if (endTime != null)
            this.progressEndTime = endTime.plusMillis((int) timeMillis);
        return false;
    }

}
