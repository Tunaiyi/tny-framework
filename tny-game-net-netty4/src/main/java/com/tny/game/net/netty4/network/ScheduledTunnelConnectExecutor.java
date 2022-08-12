/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
