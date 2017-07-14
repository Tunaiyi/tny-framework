package com.tny.game.common.scheduler.cycle;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.ReadableInstant;

/**
 * Created by Kun Yang on 16/2/20.
 */
public class DurationTimeCycle implements TimeCycle {

    private Duration duration;

    private DurationTimeCycle(Duration duration) {
        this.duration = duration;
    }

    public static final DurationTimeCycle of(long millis) {
        return new DurationTimeCycle(Duration.millis(millis));
    }

    public static final DurationTimeCycle of(ReadableInstant start, ReadableInstant end) {
        return new DurationTimeCycle(new Duration(start, end));
    }

    public static final DurationTimeCycle of(Duration duration) {
        return new DurationTimeCycle(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public DateTime getTimeAfter(DateTime dateTime) {
        return dateTime.plus(duration.getMillis());
    }

}
