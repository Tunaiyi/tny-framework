/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
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

    private final Map<Class<?>, RelayPacketArgumentsCodecor<?>> argumentsCodecorMap = new HashMap<>();

    public RelayPacketArgumentsCodecService() {
        this.addCodecor(new LinkOpenArgumentsCodecor());
        this.addCodecor(new LinkOpenedArgumentsCodecor());
        this.addCodecor(new LinkVoidArgumentsCodecor());
        this.addCodecor(new TunnelConnectArgumentsCodecor());
        this.addCodecor(new TunnelConnectedArgumentsCodecor());
        this.addCodecor(new TunnelVoidArgumentsCodecor());
    }

    private void addCodecor(RelayPacketArgumentsCodecor<?> codecor) {
        RelayPacketArgumentsCodecor<?> old = this.argumentsCodecorMap.putIfAbsent(codecor.getArgumentsClass(), codecor);
        if (old != null) {
            throw new IllegalArgumentException(format("Add {} for {} CodecorClass, {} is exist", codecor, codecor.getArgumentsClass(), old));
        }
    }

    public void setMessageCodec(NettyMessageCodec messageCodec) {
        addCodecor(new TunnelRelayArgumentsCodecor(messageCodec));
    }

    private <A extends RelayPacketArguments> RelayPacketArgumentsCodecor<A> codecor(Class<?> clazz) {
        RelayPacketArgumentsCodecor<A> codecor = as(this.argumentsCodecorMap.get(clazz));
        Asserts.checkNotNull(codecor, "不支持 {} RelayPacketArguments codecor");
        return as(codecor);
    }

    public void encode(ChannelHandlerContext ctx, RelayPacketArguments arguments, ByteBuf out) throws Exception {
        if (arguments != null) {
            RelayPacketArgumentsCodecor<RelayPacketArguments> codecor = this.codecor(arguments.getClass());
            if (codecor != null) {
                codecor.encode(ctx, arguments, out);
            }
        }
    }

    public RelayPacketArguments decode(ChannelHandlerContext ctx, ByteBuf in, RelayPacketType relayType) throws Exception {
        RelayPacketArgumentsCodecor<RelayPacketArguments> codecor = this.codecor(relayType.getArgumentsClass());
        return codecor.decode(ctx, in);
    }

}
