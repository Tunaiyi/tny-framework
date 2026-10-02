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

package com.tny.game.net.netty4.network;

import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/8 6:54 下午
 */
public class ScheduledTunnelConnectExecutor implements TunnelConnectExecutor {

    private final ScheduledExecutorService executorService;

    public ScheduledTunnelConnectExecutor(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public ScheduledFuture<Void> schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
        return as(executorService.schedule(runnable, delay, timeUnit));
    }

    @Override
    public void execute(Runnable runnable) {
        executorService.execute(runnable);
    }

}
