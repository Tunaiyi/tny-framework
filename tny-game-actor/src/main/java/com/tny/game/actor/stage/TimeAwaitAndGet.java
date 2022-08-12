/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
