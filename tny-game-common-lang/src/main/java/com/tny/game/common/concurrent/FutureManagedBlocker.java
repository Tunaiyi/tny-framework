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
