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

package com.tny.game.common.concurrent;

import java.util.concurrent.Future;

/**
 * Created by Kun Yang on 2017/6/2.
 */
class FutureWait<R> implements Wait<R> {

    public static final int EXECUTE = 1;

    public static final int FAILED = 2;

    public static final int SUCCESS = 3;

    private final Future<R> future;

    // 终态判定跨线程立即可见：state 必须 volatile——发布序"先写 value/cause、后写 state"，
    // 读侧判 SUCCESS/FAILED（volatile 读 state）后读到的值必为完成方发布的最终值
    private volatile byte state = EXECUTE;

    private volatile Throwable cause;

    private volatile R value;

    FutureWait(Future<R> future) {
        this.future = future;
    }

    @Override
    public boolean isDone() {
        if (this.state != EXECUTE) {
            return true;
        }
        this.check();
        return this.state != EXECUTE;
    }

    @Override
    public boolean isFailed() {
        if (this.state == FAILED) {
            return true;
        }
        this.check();
        return this.state == FAILED;
    }

    @Override
    public boolean isSuccess() {
        if (this.state == SUCCESS) {
            return true;
        }
        this.check();
        return this.state == SUCCESS;
    }

    @Override
    public Throwable getCause() {
        if (this.isFailed()) {
            return this.cause;
        }
        this.check();
        return this.cause;
    }

    @Override
    public R getResult() {
        if (this.isSuccess()) {
            return this.value;
        }
        this.check();
        return this.value;
    }

    private void check() {
        if (this.future.isDone()) {
            try {
                this.value = this.future.get();
                this.state = SUCCESS;
            } catch (Throwable e) {
                this.cause = e;
                this.state = FAILED;
            }
        }
    }

}
