/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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
public class RpcTokenValidator implements AuthenticationValidator<RpcAccessIdentify, MessagerCertificateFactory<RpcAccessIdentify>> {

    private final RpcAuthService rpcAuthService;

    private final IdCreator idCreator;

    public RpcTokenValidator(RpcAuthService rpcAuthService) {
        this.idCreator = new HashIDCreator(16);
        this.rpcAuthService = rpcAuthService;
    }

    @Override
    public Certificate<RpcAccessIdentify> validate(Tunnel<RpcAccessIdentify> communicator, Message message,
            MessagerCertificateFactory<RpcAccessIdentify> factory)
            throws RpcInvokeException, AuthFailedException {
        String token = message.bodyAs(String.class);
        try {
            DoneResult<RpcAccessToken> result = rpcAuthService.verifyToken(token);
            if (result.isSuccess()) {
                RpcAccessToken rpcToken = result.get();
                return factory.certificate(idCreator.createId(),
                        RpcAccessIdentify.parse(rpcToken.getMessagerId()),
                        rpcToken.getMessagerId(), rpcToken.getServiceType(), Instant.now());
            } else {
                ResultCode resultCode = result.getCode();
                throw new AuthFailedException(format("Rpc登录认证失败. {} : {}", resultCode, result.getMessage()));
            }
        } catch (Throwable e) {
            throw new AuthFailedException("Rpc登录认证失败", e);
        }
    }

}
