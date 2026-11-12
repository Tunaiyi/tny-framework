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
package com.tny.game.net.netty4.relay;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.exception.*;
import com.tny.game.net.relay.message.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;

import javax.annotation.Nonnull;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 处理由客户端发送的消息处理
 * 客户端 = > [NettyRelayMessageHandler] 转发服务器 [NettyRelayPacketHandler] => [NettyRelayPacketHandler] 业务服务器
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/16 5:35 下午
 */
@Sharable
@Unit("relayMessageHandler")
public class NettyRelayMessageHandler extends NettyMessageHandler {

    public NettyRelayMessageHandler(NetworkContext context) {
        super(context);
    }

    @Override
    public void channelRead(ChannelHandlerContext context, @Nonnull Object object) {
        if (object instanceof NetMessage message) {
            if (message.isRelay()) {
                relayMessage(context, new RelayMessage(message));
                return;
            }
        }
        super.channelRead(context, object);
    }

    private void relayMessage(ChannelHandlerContext context, RelayMessage message) {
        Channel channel = context.channel();
        NetTunnel tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).get();
        var rpcContext = RpcTransactionContext.createRelay(tunnel, message, rpcMonitor, false);
        rpcMonitor.onReceive(rpcContext);
        if (tunnel instanceof NetRelayTunnel) {
            RelayTunnel relayTunnel = as(tunnel);
            relayTunnel.relay(rpcContext, false);
        } else {
            message.release();
            var cause = new RelayException(NetResultCode.SERVER_ERROR, "tunnel cannot relay");
            rpcContext.complete(cause);
            throw cause;
        }
    }

    @Override
    public void write(ChannelHandlerContext context, Object object, ChannelPromise promise) {
        super.write(context, object, promise);
    }

}
