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
