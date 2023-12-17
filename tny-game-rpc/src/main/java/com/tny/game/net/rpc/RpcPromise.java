/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.rpc;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;

import java.util.Optional;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 5:11 下午
 */
public class RpcPromise<T> extends CompleteStageFuture<RpcResult<T>> implements RpcFuture<T> {

    private Message message;

    private Endpoint endpoint;

    public RpcPromise() {
    }

    @Override
    public ResultCode getResultCode() {
        return result().resultCode();
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
    public Endpoint endpoint() {
        return endpoint;
    }

    @Override
    public boolean isSuccess() {
        return result().isSuccess();
    }

    @Override
    public boolean isFailure() {
        return result().isFailure();
    }

    public boolean complete(Endpoint endpoint, Message message, RpcResult<T> value) {
        this.message = message;
        this.endpoint = endpoint;
        return super.complete(value);
    }

    @Override
    public Optional<RpcResult<T>> getNow() {
        if (this.isDone()) {
            return Optional.of(result());
        } else {
            return Optional.empty();
        }
    }

    private RpcResult<T> result() {
        try {
            return get();
        } catch (Exception e) {
            throw new RpcInvokeException(NetResultCode.REMOTE_EXCEPTION, e);
        }
    }

}