package com.tny.game.scheduler;

import com.tny.game.scheduler.cycle.TimeCycle;
import org.joda.time.DateTime;

public interface TimeTrigger<C extends TimeCycle> {

    DateTime getStartTime();

    DateTime getNextTime();

    DateTime getPreviousTime();

    DateTime getEndTime();

    long getSpeedMills();

    C getTimeCycle();

    boolean stop();

    default boolean start() {
        return this.start(null, null, null);
    }

    default boolean start(long startTimeMillis) {
        return this.start(null, startTimeMillis);
    }

    default boolean start(long startTimeMillis, long endTimeMillis) {
        return this.start(null, startTimeMillis, endTimeMillis);
    }

    default boolean start(C timeCycle) {
        return this.start(timeCycle, null, null);
    }

    default boolean start(C timeCycle, long startTimeMillis) {
        return this.start(timeCycle, new DateTime(startTimeMillis), null);
    }

    default boolean start(C timeCycle, long startTimeMillis, long endTimeMillis) {
        return this.start(timeCycle, new DateTime(startTimeMillis), new DateTime(endTimeMillis));
    }

    default boolean start(DateTime startTime) {
        return this.start(null, startTime, null);
    }

    default boolean start(DateTime startTime, DateTime endTime) {
        return this.start(null, startTime, endTime);
    }

    default boolean start(C timeCycle, DateTime startTime) {
        return this.start(timeCycle, startTime, null);
    }

    boolean start(C timeCycle, DateTime startTime, DateTime endTime);

    default void restartNow() {
        this.restart(System.currentTimeMillis());
    }

    default void restart(long startTimeMillis) {
        this.restart(null, startTimeMillis);
    }

    default void restart(long startTimeMillis, long endTimeMillis) {
        this.restart(null, startTimeMillis, endTimeMillis);
    }

    default void restart(C timeCycle, long startTimeMillis) {
        this.restart(timeCycle, new DateTime(startTimeMillis), null);
    }

    default void restart(C timeCycle, long startTimeMillis, long endTimeMillis) {
        this.restart(timeCycle, new DateTime(startTimeMillis), new DateTime(endTimeMillis));
    }

    default void restart(DateTime startTime) {
        this.restart(null, startTime, null);
    }

    default void restart(DateTime startTime, DateTime endTime) {
        this.restart(null, startTime, endTime);
    }

    default void restart(C timeCycle, DateTime startTime) {
        this.restart(timeCycle, startTime, null);
    }

    void restart(C timeCycle, DateTime startTime, DateTime endTime);

    boolean triggerForce();

    default boolean trigger() {
        return trigger(System.currentTimeMillis());
    }

    boolean trigger(long timeMillis);

    default boolean isFinish() {
        return this.getNextTime() == null;
    }

    default long countRemainMills() {
        return countRemainMills(System.currentTimeMillis());
    }

    void speedUp(long timeMills);

    default long getAllDuration() {
        DateTime dateTime = this.getStartTime();
        DateTime endTime = this.getEndTime();
        if (endTime == null)
            return -1L;
        return Math.max(endTime.getMillis() - dateTime.getMillis(), 0L);
    }

    default long countRemainMills(long timeMillis) {
        DateTime next = this.getNextTime();
        if (next == null)
            return -1;
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
        return Math.max(end.getMillis() - (timeMillis + this.getSpeedMills()), 0L);
    }

}
