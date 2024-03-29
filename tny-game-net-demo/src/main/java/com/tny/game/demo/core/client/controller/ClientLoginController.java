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
package com.tny.game.demo.core.client.controller;

import com.tny.game.common.result.*;
import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.netty4.configuration.command.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import static com.tny.game.net.application.ContactType.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-31 16:46
 */
@RpcController
@AuthenticationRequired({DEFAULT_USER_TYPE, "game-service"})
@BeforePlugin(SpringBootParamFilterPlugin.class)
public class ClientLoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientLoginController.class);

    public ClientLoginController() {
        System.out.println();
    }

    @RpcResponse(CtrlerIds.LOGIN$LOGIN)
    @BeforePlugin(SpringBootParamFilterPlugin.class)
    @AuthenticationRequired(value = DEFAULT_USER_TYPE, validator = DemoAuthenticationValidator.class)
    public void login(@RpcCode int code, @RpcBody LoginDTO dto) {
        if (!ResultCodes.isSuccess(code)) {
            LOGGER.info("Login failed : {}", code);
        } else {
            LOGGER.info("{} Login finish : {}", dto.getUserId(), dto.getMessage());
        }
    }

    @RpcPush(CtrlerIds.SPEAK$PUSH)
    @BeforePlugin(SpringBootParamFilterPlugin.class)
    public void pushMessage(Tunnel tunnel, @RpcBody String message) {
        LOGGER.info("User {} [accessId {}]receive push message {}", tunnel.getIdentify(), tunnel.getAccessId(), message);
    }

    @RpcPush(CtrlerIds.SPEAK$PING)
    @BeforePlugin(SpringBootParamFilterPlugin.class)
    public void pingMessage(Tunnel tunnel, @RpcBody String message) {
        LOGGER.info("User {} [accessId {}] receive : {}", tunnel.getIdentify(), tunnel.getAccessId(), message);
    }

}
