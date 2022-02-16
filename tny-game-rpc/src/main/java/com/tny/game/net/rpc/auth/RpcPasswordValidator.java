package com.tny.game.net.rpc.auth;

import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;

import javax.xml.bind.ValidationException;
import java.time.Instant;
import java.util.*;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.rpc.auth.RpcAuthMessageContexts.*;

/**
 * <p>
 */
public class RpcPasswordValidator implements AuthenticateValidator<RpcLinkerId> {

	private final RpcAuthService rpcAuthService;

	private final IdCreator idCreator;

	public RpcPasswordValidator(RpcAuthService rpcAuthService) {
		this.idCreator = new HashIDCreator(16);
		this.rpcAuthService = rpcAuthService;
	}

	@Override
	public Certificate<RpcLinkerId> validate(Tunnel<RpcLinkerId> tunnel, Message message, CertificateFactory<RpcLinkerId> factory)
			throws CommandException, ValidationException {
		Optional<MessageParamList> paramListOptional = MessageParamList.of(message.bodyAs(List.class));
		if (!paramListOptional.isPresent()) {
			throw new ValidationException("Rpc登录参数错误");
		}
		MessageParamList paramList = paramListOptional.get();
		String service = getServiceParam(paramList);
		long serverId = getServerIdParam(paramList);
		long instanceId = getInstanceIdParam(paramList);
		String password = getPasswordParam(paramList);
		DoneResult<RpcLinkerId> result = rpcAuthService.authenticate(service, serverId, instanceId, password);
		if (result.isSuccess()) {
			return factory.certificate(idCreator.createId(), result.get(), service, Instant.now());
		}
		throw new ValidationException(format("Rpc登录认证失败, Code : {} ; Message : {}", result.getCode(), result.getMessage()));
	}

}
