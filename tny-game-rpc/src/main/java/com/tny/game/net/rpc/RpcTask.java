package com.tny.game.net.rpc;

import com.tny.game.common.concurrent.*;
import com.tny.game.net.base.*;

import java.util.concurrent.CompletionStage;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 5:11 下午
 */
public class RpcTask<T> extends CompleteStageFuture<RpcResult<T>> implements RpcReturn<T> {

    public static <T> RpcTask<T> toTask(CompletionStage<RpcResult<T>> stage) {
        RpcTask<T> future = new RpcTask<T>();
        stage.whenComplete((result, cause) -> {
            if (cause != null) {
                future.completeExceptionally(cause);
            } else {
                future.complete(result);
            }
        });
        return future;
    }

}