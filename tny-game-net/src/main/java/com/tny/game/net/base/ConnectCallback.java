package com.tny.game.net.base;

import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/8 3:08 下午
 */
@FunctionalInterface
public interface ConnectCallback {

	ConnectCallback NOOP = (s, t, c) -> {
	};

	void onConnect(ConnectCallbackStatus status, NetTunnel<?> tunnel, Throwable e);

}
