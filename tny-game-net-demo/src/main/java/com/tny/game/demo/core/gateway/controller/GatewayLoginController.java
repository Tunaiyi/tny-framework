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
package com.tny.game.demo.core.gateway.controller;

import com.tny.game.demo.core.common.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.netty4.configuration.command.*;
import com.tny.game.net.session.*;
import org.slf4j.*;

import java.time.ZonedDateTime;

import static com.tny.game.net.application.ContactType.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-31 16:46
 */
@RpcController
@AuthenticationRequired(DEFAULT_USER_TYPE)
@BeforePlugin(SpringBootParamFilterPlugin.class)
public class GatewayLoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayLoginController.class);

    @RelayTo
    @RpcRequest(CtrlerIds.LOGIN$LOGIN)
    @BeforePlugin(SpringBootParamFilterPlugin.class)
    @AuthenticationRequired(value = DEFAULT_USER_TYPE, validator = DemoAuthenticationValidator.class)
    public void login(Session session, @RpcParam long sessionId, @RpcParam long userId) {
        LOGGER.info("{} - {} 登录成功 at {}", userId, sessionId, ZonedDateTime.now());
    }

}
