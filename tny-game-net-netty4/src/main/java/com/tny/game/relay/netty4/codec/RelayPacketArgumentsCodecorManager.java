package com.tny.game.relay.netty4.codec;

import com.tny.game.common.utils.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.codec.*;
import com.tny.game.relay.packet.arguments.*;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 4:22 下午
 */
public class RelayPacketArgumentsCodecorManager {

	private final Map<Class<?>, RelayPacketArgumentsCodecor<?>> argumentsCodecorMap = new HashMap<>();

	public RelayPacketArgumentsCodecorManager(NettyMessageCodec messageCodec, MessageFactory factory) {
		addCodecor(new VoidPacketArgumentsCodecor());
		addCodecor(new TunnelRelayArgumentsCodecor(messageCodec, factory));
		addCodecor(new TunnelConnectArgumentsCodecor());
	}

	private void addCodecor(RelayPacketArgumentsCodecor<?> codecor) {
		this.argumentsCodecorMap.put(codecor.getArgumentsClass(), codecor);
	}

	public <A extends RelayPacketArguments> RelayPacketArgumentsCodecor<A> codecor(Class<?> clazz) {
		RelayPacketArgumentsCodecor<A> codecor = as(this.argumentsCodecorMap.get(clazz));
		Asserts.checkNotNull(codecor, "不支持 {} RelayPacketArguments codecor");
		return as(codecor);
	}

}
