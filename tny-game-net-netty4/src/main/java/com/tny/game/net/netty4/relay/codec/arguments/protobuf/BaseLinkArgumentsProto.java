package com.tny.game.net.netty4.relay.codec.arguments.protobuf;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 4:53 下午
 */
public abstract class BaseLinkArgumentsProto<T extends LinkPacketArguments> implements PacketArgumentsProto<T> {

	protected BaseLinkArgumentsProto() {
	}

	protected BaseLinkArgumentsProto(T arguments) {
	}

}
