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

package com.tny.game.common.scheduler;

import com.tny.game.common.scheduler.cycle.*;

import java.time.Instant;

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
        return this.start(timeCycle, Instant.ofEpochMilli(startTimeMillis), null);
    }

    default boolean start(C timeCycle, long startTimeMillis, long endTimeMillis) {
        return this.start(timeCycle, Instant.ofEpochMilli(startTimeMillis), Instant.ofEpochMilli(endTimeMillis));
    }

    default boolean start(Instant startTime) {
        return this.start(null, startTime, null);
    }

    default boolean start(Instant startTime, Instant endTime) {
        return this.start(null, startTime, endTime);
    }

    default boolean start(C timeCycle, Instant startTime) {
        return this.start(timeCycle, startTime, null);
    }

    boolean start(C timeCycle, Instant startTime, Instant endTime);

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
        this.restart(timeCycle, Instant.ofEpochMilli(startTimeMillis), null);
    }

    default void restart(C timeCycle, long startTimeMillis, long endTimeMillis) {
        this.restart(timeCycle, Instant.ofEpochMilli(startTimeMillis), Instant.ofEpochMilli(endTimeMillis));
    }

    default void restart(Instant startTime) {
        this.restart(null, startTime, null);
    }

    default void restart(Instant startTime, Instant endTime) {
        this.restart(null, startTime, endTime);
    }

    default void restart(C timeCycle, Instant startTime) {
        this.restart(timeCycle, startTime, null);
    }

    void restart(C timeCycle, Instant startTime, Instant endTime);

    boolean triggerForce(long timeMillis);

    default boolean trigger() {
        return trigger(System.currentTimeMillis());
    }

    boolean trigger(long timeMillis);

    boolean speedUp(long timeMills);

    boolean resume(Instant time);

    boolean suspend(Instant time);

    default boolean resume() {
        return resume(Instant.now());
    }

    default boolean suspend() {
        return suspend(Instant.now());
    }

    boolean lengthen(long timeMillis);

}
