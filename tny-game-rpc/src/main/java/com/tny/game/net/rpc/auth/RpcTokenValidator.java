package com.tny.game.net.rpc.auth;

import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;

import java.time.Instant;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 */
public class RpcTokenValidator implements AuthenticateValidator<RpcLinkerId> {

	private final RpcAuthService rpcAuthService;

	private final IdCreator idCreator;

	public RpcTokenValidator(RpcAuthService rpcAuthService) {
		this.idCreator = new HashIDCreator(16);
		this.rpcAuthService = rpcAuthService;
	}

	@Override
	public Certificate<RpcLinkerId> validate(Tunnel<RpcLinkerId> tunnel, Message message, CertificateFactory<RpcLinkerId> factory)
			throws CommandException, ValidationException {
		String token = message.bodyAs(String.class);
		try {
			DoneResult<RpcToken> result = rpcAuthService.verifyToken(token);
			if (result.isSuccess()) {
				RpcToken rpcToken = result.get();
				return factory.certificate(idCreator.createId(),
						new RpcLinkerId(rpcToken.getService(), rpcToken.getServerId(), rpcToken.getId()),
						rpcToken.getService(), Instant.now());
			} else {
				ResultCode resultCode = result.getCode();
				throw new ValidationException(format("Rpc登录认证失败. {} : {}", resultCode, result.getMessage()));
			}
		} catch (Throwable e) {
			throw new ValidationException("Rpc登录认证失败", e);
		}
	}

}
