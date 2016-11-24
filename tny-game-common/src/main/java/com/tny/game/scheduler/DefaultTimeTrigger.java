package com.tny.game.scheduler;

import com.tny.game.common.ExceptionUtils;
import com.tny.game.scheduler.cycle.TimeCycle;
import org.joda.time.DateTime;

class DefaultTimeTrigger<C extends TimeCycle> implements TimeTrigger<C> {

    private C timeCycle = null;
    private DateTime startTime = null;
    private DateTime previousTime = null;
    private DateTime nextTime = null;
    private DateTime endTime = null;
    private long speedMills = 0;
    private DateTime suspendTime = null;

    protected DefaultTimeTrigger(DateTime startTime, DateTime previousTime, DateTime endTime, DateTime suspendTime, C timeCycle, long speedMills, boolean start) {
        this.startTime = startTime;
        this.timeCycle = timeCycle;
        this.previousTime = previousTime;
        this.endTime = endTime;
        this.speedMills = speedMills;
        this.suspendTime = suspendTime;
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

    public long getSpeedUpMills() {
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
            this.reset();
        }
        if (timeCycle != null)
            this.timeCycle = timeCycle;
        if (startTime != null) {
            this.startTime = startTime;
            this.previousTime = startTime;
        }
        if (this.previousTime == null)
            this.previousTime = this.startTime;
        setup(timeCycle, this.previousTime, false);
        return !this.isFinish();
    }

    private void reset() {
        this.suspendTime = null;
        this.speedMills = 0;
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
            this.reset();
        }
        this.startTime = startTime;
        setup(timeCycle, startTime, false);
    }

    private void setup(C timeCycle, DateTime time, boolean stop) {
        if (timeCycle != null)
            this.timeCycle = timeCycle;
        this.previousTime = time;
        if (!stop)
            this.nextTime = getNextTimeAfter(time);
    }

    @Override
    public boolean trigger(long timeMillis) {
        DateTime next = this.nextTime;
        DateTime suspend = this.suspendTime;
        DateTime checkAt = new DateTime(timeMillis);
        if (next != null && suspend == null && getCheckMills(timeMillis) >= next.getMillis()) {
            this.previousTime = checkAt.isBefore(next) ? checkAt : next;
            this.nextTime = getNextTimeAfter(next);
            return true;
        }
        return false;
    }

    @Override
    public boolean speedUp(long timeMills) {
        if (!isWorking() || timeMills <= 0)
            return false;
        this.speedMills = Math.max(this.speedMills + timeMills, 0L);
        return true;
    }

    @Override
    public boolean triggerForce(long timeMillis) {
        DateTime next = this.nextTime;
        DateTime time = new DateTime(timeMillis);
        if (next != null) {
            this.previousTime = time.isBefore(next) ? time : next;
            this.nextTime = getNextTimeAfter(next);
            return true;
        }
        return false;
    }

    private DateTime getNextTimeAfter(DateTime time) {
        if (timeCycle == null)
            return null;
        DateTime pot = timeCycle.getTimeAfter(time);
        if (this.endTime != null && pot.isAfter(this.endTime)) {
            return null;
        }
        return pot;
    }

    private long getCheckMills(long mills) {
        return this.speedMills + mills;
    }


    public DateTime getSuspendAt() {
        return this.suspendTime;
    }

    @Override
    public boolean resume(DateTime time) {
        DateTime nextTime = this.nextTime;
        DateTime suspendTime = this.suspendTime;
        if (nextTime == null || suspendTime == null || suspendTime.isAfter(time))
            return false;
        long suspendMills = Math.max(time.getMillis() - suspendTime.getMillis(), 0);
        this.nextTime = nextTime.plusMillis((int) suspendMills);
        DateTime endTime = this.endTime;
        if (endTime != null)
            this.endTime = endTime.plusMillis((int) suspendMills);
        this.suspendTime = null;
        return true;
    }

    @Override
    public boolean suspend(DateTime time) {
        if (!isWorking() && time.plusMillis((int) this.getSpeedUpMills())
                .isAfter(this.getNextTime()))
            return false;
        this.suspendTime = time;
        return true;
    }

    @Override
    public boolean lengthen(long timeMillis) {
        if (isFinish() || timeMillis <= 0)
            return false;
        DateTime nextTime = this.nextTime;
        DateTime endTime = this.endTime;
        this.nextTime = nextTime.plusMillis((int) timeMillis);
        if (endTime != null)
            this.endTime = endTime.plusMillis((int) timeMillis);
        return false;
    }

}
