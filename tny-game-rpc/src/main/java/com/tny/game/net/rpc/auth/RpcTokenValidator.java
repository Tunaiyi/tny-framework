/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
