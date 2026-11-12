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

package com.tny.game.demo.core.client.service;

import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.application.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.rpc.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/11 12:44 上午
 */
@RpcRemoteService("game-service")
public interface SpeakRemoteService {

    @RpcRequest(value = CtrlerIds.SPEAK$SAY)
    RpcResult<SayContentDTO> say(String message);

    @RpcBody
    @RpcRequest(value = CtrlerIds.SPEAK$SAY_FOR_RPC)
    SayContentDTO sayForBody(String message);

    @RpcRequest(value = CtrlerIds.SPEAK$SAY_FOR_RPC)
    RpcFuture<SayContentDTO> asyncSay(String message);

    @RpcRequest(value = CtrlerIds.SPEAK$SAY_FOR_CONTENT)
    SayContentDTO sayForContent(SayContentDTO content);

}
