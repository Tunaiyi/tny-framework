package com.tny.game.net.command.dispatcher;

import com.google.common.collect.ImmutableSet;
import com.tny.game.net.annotation.*;
import com.tny.game.net.message.*;
import org.slf4j.*;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 8:54 下午
 */
public class RpcProfile {

	public static final Logger LOGGER = LoggerFactory.getLogger(RpcProfile.class);

	private final int protocol;

	private final int[] line;

	private final Set<MessageMode> modeSet;

	private RpcProfile(int protocol, int[] line, MessageMode... mode) {
		this(protocol, line, ImmutableSet.copyOf(mode));
	}

	private RpcProfile(int protocol, int[] line, Set<MessageMode> modeSet) {
		this.protocol = protocol;
		if (line == null) {
			line = new int[]{};
		}
		this.line = line;
		this.modeSet = ImmutableSet.copyOf(modeSet);
	}

	public Class<?> getRouter() {
		return null;
	}

	public static RpcProfile of(Method method) {
		RpcRequest rpcRequest = method.getAnnotation(RpcRequest.class);
		if (rpcRequest != null) {
			return new RpcProfile(rpcRequest.value(), rpcRequest.line(), MessageMode.REQUEST);
		}
		RpcPush rpcPush = method.getAnnotation(RpcPush.class);
		if (rpcPush != null) {
			return new RpcProfile(rpcPush.value(), rpcPush.line(), MessageMode.PUSH);
		}
		RpcResponse rpcResponse = method.getAnnotation(RpcResponse.class);
		if (rpcResponse != null) {
			return new RpcProfile(rpcResponse.value(), rpcResponse.line(), MessageMode.RESPONSE);
		}
		Rpc rpc = method.getAnnotation(Rpc.class);
		if (rpc != null) {
			return new RpcProfile(rpc.value(), rpc.line(), rpc.modes());
		}
		LOGGER.warn("{} 没有存在注解 {}, {}, {}, {} 中的一个", method, RpcRequest.class, RpcPush.class, RpcResponse.class, Rpc.class);
		return null;
	}

	public int getProtocol() {
		return protocol;
	}

	public int[] getLine() {
		return line;
	}

	public Set<MessageMode> getModeSet() {
		return modeSet;
	}

}
