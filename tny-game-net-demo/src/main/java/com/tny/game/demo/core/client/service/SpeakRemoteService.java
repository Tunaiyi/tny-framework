package com.tny.game.demo.core.client.service;

import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.command.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.rpc.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/11 12:44 上午
 */
@RpcService("game-service")
public interface SpeakRemoteService {

	@RpcCaller(value = CtrlerIDs.SPEAK$SAY_FOR_RPC)
	RpcResult<SayContentDTO> say(String message);

	@RpcBody
	@RpcCaller(value = CtrlerIDs.SPEAK$SAY_FOR_RPC)
	SayContentDTO sayForBody(String message);

	@RpcCaller(value = CtrlerIDs.SPEAK$SAY_FOR_RPC)
	RpcFuture<RpcResult<SayContentDTO>> asyncSay(String message);

	@RpcBody
	@RpcCaller(value = CtrlerIDs.SPEAK$SAY_FOR_RPC)
	RpcFuture<SayContentDTO> asyncSayForBody(String message);

}
