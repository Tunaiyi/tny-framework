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

import com.tny.game.net.transport.*;
import io.netty.channel.*;
import org.slf4j.*;

/**
 * <p>
 */
public class NettyWriteMessageHandler implements ChannelFutureListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyWriteMessageHandler.class);

    private final MessageWriteFuture awaiter;

    public NettyWriteMessageHandler(MessageWriteFuture awaiter) {
        this.awaiter = awaiter;
    }

    @Override
    public void operationComplete(ChannelFuture future) {
        if (future.isSuccess()) {
            this.awaiter.complete(null);
        } else if (future.isCancelled()) {
            this.awaiter.cancel(true);
        } else if (future.cause() != null) {
            this.awaiter.completeExceptionally(future.cause());
        }
    }

}
