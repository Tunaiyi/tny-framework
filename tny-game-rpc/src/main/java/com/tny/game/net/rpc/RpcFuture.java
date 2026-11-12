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

import com.tny.game.common.result.*;
import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;

import java.util.Optional;
import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 5:11 下午
 */
public interface RpcFuture<T> extends Future<RpcResult<T>>, CompletionStage<RpcResult<T>>, RpcReturn<T> {

    ResultCode getResultCode();

    T getBody();

    Message getMessage();

    Session session();

    boolean isSuccess();

    boolean isFailure();

    Optional<RpcResult<T>> getNow();

}