package com.tny.game.net.rpc;

import com.tny.game.common.result.*;
import com.tny.game.net.command.*;

import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 5:11 下午
 */
public interface RpcFuture<T> extends Future<RpcResult<T>>, CompletionStage<RpcResult<T>> {

	ResultCode getResultCode();

	T getBody();

	boolean isSuccess();

	boolean isFailure();

}