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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tny.game.codec.jackson.mapper.*;
import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;

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

    public enum TestRpcServiceType implements RpcServiceType {

        TEST_SERVICE(100, "test_rpc_service"),
        TEST_2_SERVICE(200, "test_2_rpc_service"),

        //
        ;

        private final int id;

        private final String service;

        TestRpcServiceType(int id, String service) {
            this.id = id;
            this.service = service;
            this.register();
        }

        @Override
        public int id() {
            return id;
        }

        @Override
        public String getService() {
            return service;
        }
    }

}
