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
package com.tny.game.net.netty4.relay.codec.arguments.codecor;

import com.tny.game.net.netty4.relay.codec.arguments.*;
import com.tny.game.net.relay.packet.arguments.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:01 下午
 */
public class LinkVoidArgumentsCodec implements RelayPacketArgumentsCodec<LinkVoidArguments> {

    @Override
    public Class<LinkVoidArguments> getClassOfArguments() {
        return LinkVoidArguments.class;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, LinkVoidArguments arguments, ByteBuf out) {
    }

    @Override
    public LinkVoidArguments decode(ChannelHandlerContext ctx, ByteBuf out) {
        return LinkVoidArguments.of();
    }

}
