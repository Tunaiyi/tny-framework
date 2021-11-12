package com.tny.game.net.rpc.auth;

import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.rpc.*;

import javax.annotation.Resource;

import static com.tny.game.net.rpc.auth.RpcProtocol.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 16:46
 */
@RpcController
public class RpcAuthController {

	@Resource
	private RpcAuthService rpcAuthService;

	@RpcRequest(RPC_AUTH_$_AUTHENTICATE)
	@AuthenticationRequired(validator = RpcPasswordValidator.class)
	public RpcResult<String> authenticate(ServerBootstrapSetting setting, @UserID RpcLinkerId id) {
		String token = rpcAuthService.createToken(setting.serviceName(), id);
		return RpcResults.success(token);
	}

	@RpcResponse(RPC_AUTH_$_AUTHENTICATE)
	@AuthenticationRequired(validator = RpcTokenValidator.class)
	public void authenticated(@UserID RpcLinkerId id) {
		System.out.println(1111);
	}

}
