/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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

    private byte state = EXECUTE;

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
