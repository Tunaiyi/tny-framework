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

import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
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
        LOGGER.info("Rpc响应 >> [{}] 认证完成", id);
    }

}
