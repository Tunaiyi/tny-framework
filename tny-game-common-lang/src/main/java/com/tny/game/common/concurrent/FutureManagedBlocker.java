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

import org.slf4j.*;

import java.util.concurrent.*;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;

/**
 * Created by Kun Yang on 2017/11/17.
 */
public class FutureManagedBlocker implements ManagedBlocker {

    public static final Logger LOGGER = LoggerFactory.getLogger(FutureManagedBlocker.class);

    private Future<?> future;

    private long timeout;

    public FutureManagedBlocker(Future<?> future, long timeout) {
        this.future = future;
        this.timeout = timeout;
    }

    @Override
    public boolean block() throws InterruptedException {
        try {
            future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | TimeoutException e) {
            LOGGER.error("", e);
        }
        return isReleasable();
    }

    @Override
    public boolean isReleasable() {
        return future.isDone();
    }

}
