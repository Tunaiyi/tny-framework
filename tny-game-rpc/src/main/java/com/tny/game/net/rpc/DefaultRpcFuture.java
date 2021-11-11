package com.tny.game.net.rpc;

import com.tny.game.common.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 5:11 下午
 */
public class DefaultRpcFuture<T> extends CompleteStageFuture<T> implements RpcFuture<T> {

	public DefaultRpcFuture() {
	}

}