package com.tny.game.net.rpc.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.rpc.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/5 1:54 上午
 */
public class DefaultRpcAuthService implements RpcAuthService {

	private final RpcUserPasswordManager rpcUserPasswordManager;

	private final NetAppContext netAppContext;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public DefaultRpcAuthService(NetAppContext netAppContext, RpcUserPasswordManager rpcUserPasswordManager) {
		this.rpcUserPasswordManager = rpcUserPasswordManager;
		this.netAppContext = netAppContext;
	}

	@Override
	public DoneResult<RpcLinkerId> authenticate(String service, long serverId, long instance, String password) {
		if (rpcUserPasswordManager.auth(service, serverId, instance, password)) {
			return DoneResults.success(new RpcLinkerId(service, serverId, instance));
		}
		return DoneResults.failure(NetResultCode.VALIDATOR_FAIL_ERROR);
	}

	@Override
	public String createToken(String serviceName, RpcLinkerId id) {
		RpcToken token = new RpcToken(serviceName, netAppContext.getServerId(), id.getId(), id);
		try {
			return objectMapper.writeValueAsString(token);
		} catch (JsonProcessingException e) {
			throw new CommonRuntimeException(e);
		}
	}

	@Override
	public DoneResult<RpcToken> verifyToken(String token) {
		try {
			RpcToken rpcToken = objectMapper.readValue(token, RpcToken.class);
			return DoneResults.success(rpcToken);
		} catch (JsonProcessingException e) {
			throw new CommonRuntimeException(e);
		}
	}

}
