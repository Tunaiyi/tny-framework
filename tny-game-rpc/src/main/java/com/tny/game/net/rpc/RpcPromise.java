/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.rpc;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.result.*;
import com.tny.game.net.application.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;

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

    private Session session;

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
    public Session session() {
        return session;
    }

    @Override
    public boolean isSuccess() {
        return result().isSuccess();
    }

    @Override
    public boolean isFailure() {
        return result().isFailure();
    }

    public boolean complete(Session session, Message message, RpcResult<T> value) {
        this.message = message;
        this.session = session;
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