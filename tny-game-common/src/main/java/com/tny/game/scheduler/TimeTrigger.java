package com.tny.game.scheduler;

public interface TimeTrigger {

    boolean trigger();

    long getTriggerTime();

    boolean trigger(long time);

    long getLastFireTime();

}
