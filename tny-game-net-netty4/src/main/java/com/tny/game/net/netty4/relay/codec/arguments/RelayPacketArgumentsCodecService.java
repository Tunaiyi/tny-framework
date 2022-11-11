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

import com.tny.game.common.utils.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.netty4.relay.codec.arguments.codecor.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 4:22 下午
 */
public class RelayPacketArgumentsCodecService {

    private final Map<Class<?>, RelayPacketArgumentsCodec<?>> argumentsCodecMap = new HashMap<>();

    public RelayPacketArgumentsCodecService() {
        this.addCodec(new LinkOpenArgumentsCodec());
        this.addCodec(new LinkOpenedArgumentsCodec());
        this.addCodec(new LinkVoidArgumentsCodec());
        this.addCodec(new TunnelConnectArgumentsCodec());
        this.addCodec(new TunnelConnectedArgumentsCodec());
        this.addCodec(new TunnelVoidArgumentsCodec());
    }

    private void addCodec(RelayPacketArgumentsCodec<?> codec) {
        RelayPacketArgumentsCodec<?> old = this.argumentsCodecMap.putIfAbsent(codec.getClassOfArguments(), codec);
        if (old != null) {
            throw new IllegalArgumentException(format("Add {} for {} CodecorClass, {} is exist", codec, codec.getClassOfArguments(), old));
        }
    }

    public void setMessageCodec(NettyMessageCodec messageCodec) {
        addCodec(new TunnelRelayArgumentsCodec(messageCodec));
    }

    private <A extends RelayPacketArguments> RelayPacketArgumentsCodec<A> codec(Class<?> clazz) {
        RelayPacketArgumentsCodec<A> codec = as(this.argumentsCodecMap.get(clazz));
        Asserts.checkNotNull(codec, "不支持 {} RelayPacketArguments codecor");
        return as(codec);
    }

    public void encode(ChannelHandlerContext ctx, RelayPacketArguments arguments, ByteBuf out) throws Exception {
        if (arguments != null) {
            RelayPacketArgumentsCodec<RelayPacketArguments> codec = this.codec(arguments.getClass());
            if (codec != null) {
                codec.encode(ctx, arguments, out);
            }
        }
    }

    public RelayPacketArguments decode(ChannelHandlerContext ctx, ByteBuf in, RelayPacketType relayType) throws Exception {
        RelayPacketArgumentsCodec<RelayPacketArguments> codec = this.codec(relayType.getClassOfArguments());
        return codec.decode(ctx, in);
    }

}
