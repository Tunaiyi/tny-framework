package com.tny.game.common.scheduler.cycle;

import java.time.*;

/**
 * Created by Kun Yang on 16/2/20.
 */
public class DurationTimeCycle implements TimeCycle {

    private Duration duration;

    private DurationTimeCycle(Duration duration) {
        this.duration = duration;
    }

    public static final DurationTimeCycle of(long millis) {
        return new DurationTimeCycle(Duration.ofMillis(millis));
    }

    public static final DurationTimeCycle of(Instant start, Instant end) {
        return new DurationTimeCycle(Duration.between(start, end));
    }

    public static final DurationTimeCycle of(Duration duration) {
        return new DurationTimeCycle(duration);
    }

    public Duration getDuration() {
        return this.duration;
    }

    @Override
    public Instant getTimeAfter(Instant dateTime) {
        return dateTime.plusMillis(this.duration.toMillis());
    }

}
