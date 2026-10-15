/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
