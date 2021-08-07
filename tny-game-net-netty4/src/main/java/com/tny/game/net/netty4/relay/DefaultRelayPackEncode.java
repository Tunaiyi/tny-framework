package com.tny.game.net.netty4.relay;

import com.tny.game.net.relay.packet.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/6 8:46 下午
 */
public class DefaultRelayPackEncode implements RelayPacketEncoder {

	@Override
	public void encodeObject(ChannelHandlerContext ctx, RelayPacket relay, ByteBuf out) {
	}

}
