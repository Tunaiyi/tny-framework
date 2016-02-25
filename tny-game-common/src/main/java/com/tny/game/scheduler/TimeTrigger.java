package com.tny.game.scheduler;

import com.tny.game.scheduler.cycle.TimeCycle;
import org.joda.time.DateTime;

public interface TimeTrigger<C extends TimeCycle> {

    DateTime getNextTime();

    DateTime getPreviousTime();

    DateTime getEndTime();

    long getSpeedMills();

    C getTimeCycle();

    boolean stop();

    default void restartAt() {
        this.restartAt(System.currentTimeMillis());
    }

    default void restartAt(C timeCycle, long timeMillis) {
        this.restartAt(timeCycle, new DateTime(timeMillis), null);
    }

    default void restartAt(C timeCycle, long timeMillis, DateTime end) {
        this.restartAt(timeCycle, new DateTime(timeMillis), end);
    }

    default void restartAt(long timeMillis) {
        this.restartAt(null, timeMillis);
    }

    default void restartAt(long timeMillis, DateTime end) {
        this.restartAt(null, timeMillis, end);
    }

    default void restartAt(C timeCycle, DateTime time) {
        this.restartAt(timeCycle, time, null);
    }

    void restartAt(C timeCycle, DateTime time, DateTime end);

    boolean triggerForce();

    default boolean trigger() {
        return trigger(System.currentTimeMillis());
    }

    boolean trigger(long timeMillis);

    default boolean isStop() {
        return this.getNextTime() == null;
    }

    default long countRemainMills() {
        return countRemainMills(System.currentTimeMillis());
    }

    void speedUp(long timeMills);

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
