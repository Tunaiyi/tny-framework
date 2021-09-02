package com.tny.game.net.netty4.relay.codec.arguments.codecor;

import com.tny.game.net.netty4.relay.codec.arguments.*;
import com.tny.game.net.netty4.relay.codec.arguments.protobuf.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:01 下午
 */
public class TunnelConnectArgumentsCodecor extends BaseProtobufPacketArgumentsCodecor<TunnelConnectArguments, TunnelConnectArgumentsProto> {

	public TunnelConnectArgumentsCodecor() {
		super(TunnelConnectArguments.class, TunnelConnectArgumentsProto.class, TunnelConnectArgumentsProto::new);
	}

}
