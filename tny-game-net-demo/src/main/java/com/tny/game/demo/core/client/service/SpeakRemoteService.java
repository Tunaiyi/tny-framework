/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.demo.core.client.service;

import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
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

    @RpcRequest(value = CtrlerIds.SPEAK$SAY_FOR_RPC)
    RpcResult<SayContentDTO> say(String message);

    @RpcBody
    @RpcRequest(value = CtrlerIds.SPEAK$SAY_FOR_RPC)
    SayContentDTO sayForBody(String message);

    @RpcRequest(value = CtrlerIds.SPEAK$SAY_FOR_RPC)
    RpcFuture<SayContentDTO> asyncSay(String message);

}
