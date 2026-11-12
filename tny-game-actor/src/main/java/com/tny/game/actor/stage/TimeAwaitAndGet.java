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

package com.tny.game.actor.stage;

import com.tny.game.common.result.*;

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
        if (this.timeout < 0) {
            this.timeout = System.currentTimeMillis() + this.duration.toMillis();
        }
        if (System.currentTimeMillis() > this.timeout) {
            return DoneResults.success(this.object);
        }
        return DoneResults.failure();
    }

}
