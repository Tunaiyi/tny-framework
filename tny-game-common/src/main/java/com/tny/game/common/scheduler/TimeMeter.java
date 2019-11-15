package com.tny.game.common.scheduler;

import com.tny.game.common.scheduler.cycle.*;
import org.joda.time.DateTime;

/**
 * 计时器
 * Created by Kun Yang on 16/4/13.
 */
public interface TimeMeter<C extends TimeCycle> {

    DateTime getStartTime();

    DateTime getNextTime();

    DateTime getPreviousTime();

    DateTime getEndTime();

    DateTime getSuspendTime();

    long getSpeedMills();

    C getTimeCycle();

    default long countRemainMills() {
        return countRemainMills(System.currentTimeMillis());
    }

    default boolean isSuspend() {
        return this.getNextTime() != null && this.getSuspendTime() != null;
    }

    default boolean isWorking() {
        return this.getNextTime() != null && this.getSuspendTime() == null;
    }

    default boolean isFinish() {
        return this.getNextTime() == null;
    }

    default double getProgress() {
        return getProgress(System.currentTimeMillis());
    }

    default double getTotalProgress() {
        return getTotalProgress(System.currentTimeMillis());
    }

    default double getProgress(long timeMillis) {
        long duration = this.getDuration();
        long progressTime = duration - this.countRemainMills(timeMillis);
        return (double) progressTime / duration;
    }

    default double getTotalProgress(long timeMillis) {
        long duration = this.getTotalDuration();
        long progressTime = duration - this.countEndRemainMills(timeMillis);
        return (double) progressTime / duration;
    }

    default long getTotalDuration() {
        DateTime dateTime = this.getStartTime();
        DateTime endTime = this.getEndTime();
        if (endTime == null)
            return -1L;
        return Math.max(endTime.getMillis() - dateTime.getMillis(), 0L);
    }

    default long getDuration() {
        DateTime previous = this.getPreviousTime();
        DateTime next = this.getNextTime();
        if (previous == null || next == null)
            return -1;
        return next.getMillis() - previous.getMillis();
    }

    default long countRemainMills(long timeMillis) {
        DateTime next = this.getNextTime();
        if (next == null)
            return -1;
        DateTime suspendTime = this.getSuspendTime();
        if (suspendTime != null && suspendTime.getMillis() < timeMillis + this.getSpeedMills())
            timeMillis = suspendTime.getMillis();
        return Math.max(next.getMillis() - (timeMillis + this.getSpeedMills()), 0L);
    }

    default long countEndRemainMills() {
        return countEndRemainMills(System.currentTimeMillis());
    }

    default long countEndRemainMills(long timeMillis) {
        DateTime next = this.getNextTime();
        if (next == null)
            return -1;
        DateTime end = this.getEndTime();
        if (end == null)
            return -1;
        DateTime suspendTime = this.getSuspendTime();
        if (suspendTime != null && suspendTime.getMillis() <= timeMillis)
            timeMillis = suspendTime.getMillis();
        return Math.max(end.getMillis() - (timeMillis + this.getSpeedMills()), 0L);
    }

}
