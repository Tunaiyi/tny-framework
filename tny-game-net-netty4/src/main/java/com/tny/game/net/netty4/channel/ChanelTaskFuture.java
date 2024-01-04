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

package com.tny.game.net.netty4.channel;

import com.tny.game.common.concurrent.*;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/12/16 3:41 PM
 */
public class ChanelTaskFuture implements TaskFuture<Void> {

    private ChannelPromise promise;

    public ChanelTaskFuture(ChannelPromise promise) {
        this.promise = promise;
    }

    @Override
    public void success(Void value) {
        this.promise.setSuccess();
    }

    @Override
    public void failure(Throwable throwable) {
        promise.setFailure(throwable);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return promise.cancel(true);
    }

    @Override
    public boolean isCancelled() {
        return promise.isCancelled();
    }

    @Override
    public boolean isDone() {
        return promise.isDone();
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {
        return promise.get();
    }

    @Override
    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return promise.get(timeout, unit);
    }

}
