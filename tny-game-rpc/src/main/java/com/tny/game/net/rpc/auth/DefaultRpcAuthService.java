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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tny.game.codec.jackson.mapper.*;
import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;
import com.tny.game.net.application.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/5 1:54 上午
 */
public class DefaultRpcAuthService implements RpcAuthService {

    private final RpcUserPasswordManager rpcUserPasswordManager;

    private final NetAppContext netAppContext;

    private static final ObjectMapper objectMapper = ObjectMapperFactory.createMapper();

    static {
        SimpleModule module = new SimpleModule();
        module.addSerializer(RpcServiceType.class, new RpcServiceTypeJsonSerializer());
        module.addDeserializer(RpcServiceType.class, new RpcServiceTypeJsonDeserializer());
        objectMapper.registerModule(module);
    }

    public DefaultRpcAuthService(NetAppContext netAppContext, RpcUserPasswordManager rpcUserPasswordManager) {
        this.rpcUserPasswordManager = rpcUserPasswordManager;
        this.netAppContext = netAppContext;
    }

    @Override
    public DoneResult<RpcAccessIdentify> authenticate(long id, String password) {
        RpcAccessIdentify identify = RpcAccessIdentify.parse(id);
        if (rpcUserPasswordManager.auth(identify, password)) {
            return DoneResults.success(identify);
        }
        return DoneResults.failure(NetResultCode.AUTH_FAIL_ERROR);
    }

    @Override
    public String createToken(RpcServiceType serviceType, RpcAccessIdentify user) {
        RpcAccessToken token = new RpcAccessToken(serviceType, netAppContext.getServerId(), user);
        try {
            return objectMapper.writeValueAsString(token);
        } catch (JsonProcessingException e) {
            throw new CommonRuntimeException(e);
        }
    }

    @Override
    public DoneResult<RpcAccessToken> verifyToken(String token) {
        try {
            RpcAccessToken rpcToken = objectMapper.readValue(token, RpcAccessToken.class);
            return DoneResults.success(rpcToken);
        } catch (JsonProcessingException e) {
            throw new CommonRuntimeException(e);
        }
    }

}
