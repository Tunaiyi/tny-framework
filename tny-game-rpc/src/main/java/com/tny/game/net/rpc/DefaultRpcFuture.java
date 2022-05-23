package com.tny.game.net.rpc;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.exception.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 5:11 下午
 */
public class DefaultRpcFuture<T> extends CompleteStageFuture<RpcResult<T>> implements RpcFuture<T> {

    private Message message;

    public DefaultRpcFuture() {
    }

    @Override
    public ResultCode getResultCode() {
        return result().getResultCode();
    }

    @Override
    public T getBody() {
        return as(result().getBody());
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public boolean isSuccess() {
        return result().isSuccess();
    }

    @Override
    public boolean isFailure() {
        return result().isFailure();
    }

    public boolean complete(Message message, RpcResult<T> value) {
        this.message = message;
        return super.complete(value);
    }

    private RpcResult<T> result() {
        try {
            return get();
        } catch (Exception e) {
            throw new RpcInvokeException(NetResultCode.REMOTE_EXCEPTION, e);
        }
    }

}