package com.tny.game.net.rpc.auth;

import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.time.Instant;
import java.util.*;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.rpc.auth.RpcAuthMessageContexts.*;

/**
 * <p>
 */
public class RpcPasswordValidator implements AuthenticateValidator<RpcAccessIdentify, MessagerCertificateFactory<RpcAccessIdentify>> {

    private final RpcAuthService rpcAuthService;

    private final IdCreator idCreator;

    public RpcPasswordValidator(RpcAuthService rpcAuthService) {
        this.idCreator = new HashIDCreator(16);
        this.rpcAuthService = rpcAuthService;
    }

    @Override
    public Certificate<RpcAccessIdentify> validate(Tunnel<RpcAccessIdentify> tunnel, Message message,
            MessagerCertificateFactory<RpcAccessIdentify> factory)
            throws CommandException, ValidationException {
        Optional<MessageParamList> paramListOptional = MessageParamList.of(message.bodyAs(List.class));
        if (!paramListOptional.isPresent()) {
            throw new ValidationException("Rpc登录参数错误");
        }
        MessageParamList paramList = paramListOptional.get();
        long value = getIdParam(paramList);
        String password = getPasswordParam(paramList);
        DoneResult<RpcAccessIdentify> result = rpcAuthService.authenticate(value, password);
        if (result.isSuccess()) {
            RpcAccessIdentify identify = result.get();
            return factory.certificate(idCreator.createId(), identify, value, identify.getServiceType(), Instant.now());
        }
        throw new ValidationException(format("Rpc登录认证失败, Code : {} ; Message : {}", result.getCode(), result.getMessage()));
    }

}
