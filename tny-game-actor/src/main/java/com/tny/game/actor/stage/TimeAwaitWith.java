package com.tny.game.actor.stage;

import com.tny.game.common.utils.DoneUtils;
import com.tny.game.common.utils.Done;

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
        return System.currentTimeMillis() > this.timeout ? DoneUtils.succ(object) : DoneUtils.fail();
    }
}
