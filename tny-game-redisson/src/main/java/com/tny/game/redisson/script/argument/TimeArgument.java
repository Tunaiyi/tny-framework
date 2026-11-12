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

package com.tny.game.redisson.script.argument;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

/**
 * 时间参数
 * <p>
 *
 * @author : kgtny
 * @date : 2020/7/27 6:36 下午
 */
public class TimeArgument<B> extends ScriptArgument<B> {

    private Long time;

    public TimeArgument(B builder) {
        super(builder);
    }

    public TimeArgument(B builder, Long time) {
        super(builder);
        this.time = time;
    }

    public B withTime(long time) {
        this.time = time;
        return and();
    }

    public B withTime(Instant time) {
        this.time = time.toEpochMilli();
        return and();
    }

    public B withTime(long time, TimeUnit unit) {
        this.time = unit.toMillis(time);
        return and();
    }

    public B withInterval(long interval) {
        this.time = System.currentTimeMillis() + interval;
        return and();
    }

    public B withInterval(long interval, TimeUnit unit) {
        this.time = System.currentTimeMillis() + unit.toMillis(interval);
        return and();
    }

    public long getTime() {
        return this.time;
    }

    public long getTime(long defaultTime) {
        return this.time == null ? defaultTime : this.time;
    }

    public long getTime(LongSupplier supplier) {
        return this.time == null ? supplier.getAsLong() : this.time;
    }

    public boolean hasValue() {
        return this.time != null;
    }

}
