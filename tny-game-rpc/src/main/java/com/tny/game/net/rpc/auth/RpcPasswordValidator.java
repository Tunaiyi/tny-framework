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

import com.tny.game.common.utils.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;

import java.util.*;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.rpc.auth.RpcAuthMessageContexts.*;

/**
 * <p>
 */
public class RpcPasswordValidator implements AuthenticationValidator {

    private final RpcAuthService rpcAuthService;

    private final IdCreator idCreator;

    public RpcPasswordValidator(RpcAuthService rpcAuthService) {
        this.idCreator = new HashIDCreator(16);
        this.rpcAuthService = rpcAuthService;
    }

    @Override
    public Certificate validate(Tunnel tunnel, Message message)
            throws RpcInvokeException, AuthFailedException {
        Optional<MessageParamList> paramListOptional = MessageParamList.of(message.bodyAs(List.class));
        if (!paramListOptional.isPresent()) {
            throw new AuthFailedException("Rpc登录参数错误");
        }
        MessageParamList paramList = paramListOptional.get();
        long id = getIdParam(paramList);
        String password = getPasswordParam(paramList);
        var result = rpcAuthService.authenticate(id, password);
        if (result.isSuccess()) {
            RpcAccessIdentify identify = result.get();
            return Certificates.createAuthenticated(idCreator.createId(), identify.getId(), identify.getContactId(), identify.getContactType(),
                    identify);
        }
        throw new AuthFailedException(format("Rpc登录认证失败, Code : {} ; Message : {}", result.getCode(), result.getMessage()));
    }

}
