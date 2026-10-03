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

import com.tny.game.net.annotation.*;
import com.tny.game.net.application.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tny.game.net.rpc.auth.RpcProtocol.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-31 16:46
 */
@RpcController
public class RpcAuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcAuthController.class);

    @Autowired
    private RpcAuthService rpcAuthService;

    @RpcRequest(RPC_AUTH_$_AUTHENTICATE)
    @AuthenticationRequired(validator = RpcPasswordValidator.class)
    public RpcResult<String> authenticate(ServerBootstrapSetting setting, @IdentifyToken RpcAccessIdentify id) {
        RpcServiceType serviceType = RpcServiceTypes.checkService(setting.serviceName());
        String token = rpcAuthService.createToken(serviceType, id);
        LOGGER.info("Rpc执行 << [{}] 认证成功", id);
        return RpcResults.success(token);
    }

    @RpcResponse(RPC_AUTH_$_AUTHENTICATE)
    @AuthenticationRequired(validator = RpcTokenValidator.class)
    public void authenticated(@IdentifyToken RpcAccessIdentify id) {
        LOGGER.info("Rpc响应 << [{}] 认证完成", id);
    }

}
