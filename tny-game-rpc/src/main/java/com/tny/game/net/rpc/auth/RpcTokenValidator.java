package com.tny.game.net.rpc.auth;

import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.time.Instant;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 */
public class RpcTokenValidator implements AuthenticateValidator<RpcAccessIdentify, MessagerCertificateFactory<RpcAccessIdentify>> {

    private final RpcAuthService rpcAuthService;

    private final IdCreator idCreator;

    public RpcTokenValidator(RpcAuthService rpcAuthService) {
        this.idCreator = new HashIDCreator(16);
        this.rpcAuthService = rpcAuthService;
    }

    @Override
    public Certificate<RpcAccessIdentify> validate(Tunnel<RpcAccessIdentify> tunnel, Message message,
            MessagerCertificateFactory<RpcAccessIdentify> factory)
            throws CommandException, ValidationException {
        String token = message.bodyAs(String.class);
        try {
            DoneResult<RpcAccessToken> result = rpcAuthService.verifyToken(token);
            if (result.isSuccess()) {
                RpcAccessToken rpcToken = result.get();
                return factory.certificate(idCreator.createId(),
                        RpcAccessIdentify.parse(rpcToken.getId()),
                        rpcToken.getId(), rpcToken.getServiceType(), Instant.now());
            } else {
                ResultCode resultCode = result.getCode();
                throw new ValidationException(format("Rpc登录认证失败. {} : {}", resultCode, result.getMessage()));
            }
        } catch (Throwable e) {
            throw new ValidationException("Rpc登录认证失败", e);
        }
    }

}
