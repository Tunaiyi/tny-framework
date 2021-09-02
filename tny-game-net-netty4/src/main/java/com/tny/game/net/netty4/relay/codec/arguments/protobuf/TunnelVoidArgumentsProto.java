package com.tny.game.net.netty4.relay.codec.arguments.protobuf;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 4:53 下午
 */
public class TunnelVoidArgumentsProto extends BaseTunnelArgumentsProto<TunnelVoidArguments> {

	public TunnelVoidArgumentsProto() {
	}

	public TunnelVoidArgumentsProto(TunnelVoidArguments arguments) {
		super(arguments);
	}

	@Override
	public TunnelVoidArguments toArguments() {
		return new TunnelVoidArguments(this.getTunnelId());
	}

}
