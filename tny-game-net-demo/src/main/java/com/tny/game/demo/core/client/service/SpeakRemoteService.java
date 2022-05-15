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
