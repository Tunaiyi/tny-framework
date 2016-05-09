package com.tny.game.actor.stage;

import java.time.Duration;

/**
 * Created by Kun Yang on 16/1/23.
 */
public class TimeAwait {

    private Duration duration;
    private long timeout = -1;

    TimeAwait(Duration duration) {
        this.duration = duration;
    }

    public boolean get() {
        if (this.timeout < 0)
            this.timeout = System.currentTimeMillis() + duration.toMillis();
        return System.currentTimeMillis() > this.timeout;
    }

}
