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
import com.tny.game.net.application.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 */
public class RpcTokenValidator implements AuthenticationValidator {

    private final RpcAuthService rpcAuthService;

    private final IdCreator idCreator;

    public RpcTokenValidator(RpcAuthService rpcAuthService) {
        this.idCreator = new HashIDCreator(16);
        this.rpcAuthService = rpcAuthService;
    }

    @Override
    public Certificate validate(Tunnel tunnel, Message message) throws RpcInvokeException, AuthFailedException {
        String token = message.bodyAs(String.class);
        try {
            DoneResult<RpcAccessToken> result = rpcAuthService.verifyToken(token);
            if (result.isSuccess()) {
                RpcAccessToken rpcToken = result.get();
                var identify = RpcAccessIdentify.parse(rpcToken.getId());
                return Certificates.createAuthenticated(idCreator.createId(),
                        identify.getId(), identify.getContactId(), identify.getServiceType(), identify);
            } else {
                ResultCode resultCode = result.getCode();
                throw new AuthFailedException(format("Rpc登录认证失败. {} : {}", resultCode, result.getMessage()));
            }
        } catch (Throwable e) {
            throw new AuthFailedException("Rpc登录认证失败", e);
        }
    }

}
