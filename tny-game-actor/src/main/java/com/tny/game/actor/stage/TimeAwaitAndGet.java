package com.tny.game.actor.stage;

import com.tny.game.common.utils.Do;
import com.tny.game.common.utils.Done;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/1/23.
 */
public class TimeAwaitAndGet<T> implements Supplier<Done<T>> {

    private Duration duration;
    private T object;
    private long timeout = -1;

    TimeAwaitAndGet(T object, Duration duration) {
        this.duration = duration;
    }


    @Override
    public Done<T> get() {
        if (this.timeout < 0)
            this.timeout = System.currentTimeMillis() + duration.toMillis();
        if (System.currentTimeMillis() > this.timeout)
            return Do.succ(object);
        return Do.fail();
    }

}
