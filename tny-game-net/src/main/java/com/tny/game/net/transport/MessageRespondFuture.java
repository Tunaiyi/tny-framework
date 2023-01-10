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
package com.tny.game.net.transport;

import com.tny.game.common.concurrent.*;
import com.tny.game.net.message.*;

import java.util.concurrent.CompletableFuture;

public class MessageRespondFuture extends CompletableFuture<Message> implements CompletionStageFuture<Message> {

    public static final long DEFAULT_FUTURE_TIMEOUT = 10000L;

    private final long timeout;

    public MessageRespondFuture() {
        this(-1);
    }

    public MessageRespondFuture(long timeout) {
        if (timeout <= 0) {
            timeout = DEFAULT_FUTURE_TIMEOUT;
        }
        this.timeout = System.currentTimeMillis() + timeout;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return super.cancel(mayInterruptIfRunning);
    }

    public boolean isTimeout() {
        return System.currentTimeMillis() >= this.timeout;
    }

}