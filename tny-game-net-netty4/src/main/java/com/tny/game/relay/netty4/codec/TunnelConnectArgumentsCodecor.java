package com.tny.game.relay.netty4.codec;

import com.tny.game.net.exception.*;
import com.tny.game.net.netty4.codec.*;
import com.tny.game.relay.packet.arguments.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:01 下午
 */
public class TunnelConnectArgumentsCodecor implements RelayPacketArgumentsCodecor<TunnelConnectArguments> {

	@Override
	public Class<TunnelConnectArguments> getArgumentsClass() {
		return TunnelConnectArguments.class;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, TunnelConnectArguments arguments, ByteBuf out) {
		int[] ipValue = arguments.getIpValue();
		if (ipValue.length < 4) {
			throw CodecException.causeEncode("error ip lengthF");
		}
		for (int index = 0; index < 4; index++) {
			out.writeByte(ipValue[index]);
		}
		NettyVarIntCoder.writeFixed32(arguments.getPort(), out);
	}

	@Override
	public TunnelConnectArguments decode(ChannelHandlerContext ctx, ByteBuf out) {
		int[] ipValue = new int[4];
		for (int index = 0; index < 4; index++) {
			ipValue[index] = out.readByte();
		}
		int port = NettyVarIntCoder.readFixed32(out);
		return new TunnelConnectArguments(ipValue, port);
	}

}
