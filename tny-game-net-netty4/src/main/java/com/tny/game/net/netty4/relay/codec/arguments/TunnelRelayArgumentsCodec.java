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
package com.tny.game.net.netty4.relay.codec.arguments;

import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.arguments.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:01 下午
 */
public class TunnelRelayArgumentsCodec implements RelayPacketArgumentsCodec<TunnelRelayArguments> {

    private final NettyMessageCodec messageCodec;

    public TunnelRelayArgumentsCodec(NettyMessageCodec messageCodec) {
        this.messageCodec = messageCodec;
    }

    @Override
    public Class<TunnelRelayArguments> getClassOfArguments() {
        return TunnelRelayArguments.class;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, TunnelRelayArguments arguments, ByteBuf out) throws Exception {
        NettyVarIntCoder.writeFixed64(arguments.getInstanceId(), out);
        NettyVarIntCoder.writeFixed64(arguments.getTunnelId(), out);
        Message message = arguments.getMessage();
        messageCodec.encode(as(message), out);
    }

    @Override
    public TunnelRelayArguments decode(ChannelHandlerContext ctx, ByteBuf out) throws Exception {
        RelayTransport transporter = ctx.channel().attr(NettyRelayAttrKeys.RELAY_TRANSPORTER).get();
        NetworkContext context = transporter.getContext();
        long instanceId = NettyVarIntCoder.readFixed64(out);
        long tunnelId = NettyVarIntCoder.readFixed64(out);
        NetMessage message = messageCodec.decode(out, context.getMessageFactory());
        return new TunnelRelayArguments(instanceId, tunnelId, message);
    }

}
