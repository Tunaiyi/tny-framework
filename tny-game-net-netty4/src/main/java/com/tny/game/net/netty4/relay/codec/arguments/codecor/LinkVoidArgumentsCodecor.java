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
