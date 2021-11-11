package com.tny.game.net.rpc;

import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 5:11 下午
 */
public interface RpcFuture<T> extends Future<T>, CompletionStage<T> {

}