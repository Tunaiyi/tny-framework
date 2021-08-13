package com.tny.game.net.netty4.relay;

import com.google.common.collect.ImmutableMap;
import com.tny.game.net.netty4.relay.codec.*;
import com.tny.game.net.relay.packet.arguments.*;

import java.util.Map;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 4:22 下午
 */
public class RelayPacketArgumentsCodecorManager {

	private final Map<Class<?>, RelayPacketArgumentsCodecor<?>> argumentsCodecorMap;

	public RelayPacketArgumentsCodecorManager(
			Map<Class<?>, RelayPacketArgumentsCodecor<?>> argumentsCodecorMap) {
		this.argumentsCodecorMap = ImmutableMap.copyOf(argumentsCodecorMap);
	}

	public <A extends RelayPacketArguments> A codecor(Class<?> clazz) {
		RelayPacketArgumentsCodecor<?> codecor = argumentsCodecorMap.get(clazz);
		return as(codecor);
	}

}
