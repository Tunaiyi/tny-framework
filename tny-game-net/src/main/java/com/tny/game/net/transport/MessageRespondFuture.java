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