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
public class RpcPasswordValidator implements AuthenticationValidator<RpcAccessIdentify, ContactCertificateFactory<RpcAccessIdentify>> {

    private final RpcAuthService rpcAuthService;

    private final IdCreator idCreator;

    public RpcPasswordValidator(RpcAuthService rpcAuthService) {
        this.idCreator = new HashIDCreator(16);
        this.rpcAuthService = rpcAuthService;
    }

    @Override
    public Certificate<RpcAccessIdentify> validate(Tunnel<RpcAccessIdentify> communicator, Message message,
            ContactCertificateFactory<RpcAccessIdentify> factory)
            throws RpcInvokeException, AuthFailedException {
        Optional<MessageParamList> paramListOptional = MessageParamList.of(message.bodyAs(List.class));
        if (!paramListOptional.isPresent()) {
            throw new AuthFailedException("Rpc登录参数错误");
        }
        MessageParamList paramList = paramListOptional.get();
        long id = getIdParam(paramList);
        String password = getPasswordParam(paramList);
        DoneResult<RpcAccessIdentify> result = rpcAuthService.authenticate(id, password);
        if (result.isSuccess()) {
            RpcAccessIdentify identify = result.get();
            return factory.certificate(idCreator.createId(), identify, id, identify.getServiceType(), Instant.now());
        }
        throw new AuthFailedException(format("Rpc登录认证失败, Code : {} ; Message : {}", result.getCode(), result.getMessage()));
    }

}
