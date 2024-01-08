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
