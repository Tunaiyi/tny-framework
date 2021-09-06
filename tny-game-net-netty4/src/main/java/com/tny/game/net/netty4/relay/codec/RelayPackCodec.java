package com.tny.game.net.netty4.relay.codec;

import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.relay.link.*;
import io.netty.channel.ChannelHandlerContext;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 6:03 下午
 */
public interface RelayPackCodec {

	default RelayLink getRelayPipe(ChannelHandlerContext ctx) {
		return ctx.channel().attr(NettyRelayAttrKeys.RELAY_LINK).get();
	}

	default RelayLink loadOrCreateRelayPipe(ChannelHandlerContext ctx, long id) {
		return ctx.channel().attr(NettyRelayAttrKeys.RELAY_LINK).get();
	}

}
