package com.tny.game.actor.task;

import com.tny.game.base.item.Do;
import com.tny.game.base.item.Done;

import java.time.Duration;

/**
 * Created by Kun Yang on 16/1/23.
 */
public class TimeAwaitWith<T> {

    private Duration duration;
    private T object;
    private long timeout = -1;

    public TimeAwaitWith(T object, Duration duration) {
        this.object = object;
        this.duration = duration;
    }

    public Done<T> get() {
        if (this.timeout < 0)
            this.timeout = System.currentTimeMillis() + duration.toMillis();
        return System.currentTimeMillis() > this.timeout ? Do.succ(object) : Do.fail();
    }
}
