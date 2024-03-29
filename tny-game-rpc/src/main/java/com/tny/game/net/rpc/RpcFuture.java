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