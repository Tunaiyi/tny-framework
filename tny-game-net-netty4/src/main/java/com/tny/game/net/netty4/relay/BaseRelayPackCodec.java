package com.tny.game.net.netty4.relay;

import com.tny.game.net.relay.*;
import io.netty.channel.ChannelHandlerContext;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 6:03 下午
 */
public class BaseRelayPackCodec {

	public RelayPipe<?> getRelayPipe(ChannelHandlerContext ctx) {
		return ctx.channel().attr(NettyRelayAttributeKeys.RELAY_PIPE).get();
	}

	public RelayPipe<?> loadOrCreateRelayPipe(ChannelHandlerContext ctx, long id) {
		return ctx.channel().attr(NettyRelayAttributeKeys.RELAY_PIPE).get();
	}

}
