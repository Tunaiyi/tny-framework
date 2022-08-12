/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
public class LinkVoidArgumentsCodecor implements RelayPacketArgumentsCodecor<LinkVoidArguments> {

    @Override
    public Class<LinkVoidArguments> getArgumentsClass() {
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
