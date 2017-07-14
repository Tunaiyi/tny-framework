package com.tny.game.common.scheduler;

import com.tny.game.common.scheduler.cycle.TimeCycle;
import org.joda.time.DateTime;

public interface TimeTrigger<C extends TimeCycle> extends TimeMeter<C> {

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

    boolean triggerForce(long timeMillis);

    default boolean trigger() {
        return trigger(System.currentTimeMillis());
    }

    boolean trigger(long timeMillis);

    boolean speedUp(long timeMills);

    boolean resume(DateTime time);

    boolean suspend(DateTime time);

    default boolean resume() {
        return resume(DateTime.now());
    }

    default boolean suspend() {
        return suspend(DateTime.now());
    }

    boolean lengthen(long timeMillis);

}
