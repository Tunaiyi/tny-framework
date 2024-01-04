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
        RelayTransporter transporter = ctx.channel().attr(NettyRelayAttrKeys.RELAY_TRANSPORTER).get();
        NetworkContext context = transporter.getContext();
        long instanceId = NettyVarIntCoder.readFixed64(out);
        long tunnelId = NettyVarIntCoder.readFixed64(out);
        NetMessage message = messageCodec.decode(out, context.getMessageFactory());
        return new TunnelRelayArguments(instanceId, tunnelId, message);
    }

}
