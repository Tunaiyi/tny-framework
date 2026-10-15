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
