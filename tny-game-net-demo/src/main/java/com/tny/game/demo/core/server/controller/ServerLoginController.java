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
package com.tny.game.demo.core.server.controller;

import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.netty4.configuration.command.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;

import java.time.ZonedDateTime;
import java.util.concurrent.*;

import static com.tny.game.common.utils.StringAide.*;
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
public class ServerLoginController {

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);

    @RpcRequest(CtrlerIds.LOGIN$LOGIN)
    @BeforePlugin(SpringBootParamFilterPlugin.class)
    @AuthenticationRequired(value = DEFAULT_USER_TYPE, validator = DemoAuthenticationValidator.class)
    public LoginDTO login(Tunnel tunnel, Session session, @RpcParam long sessionId, @RpcParam long userId) {
        Certificate certificate = session.getCertificate();
        return new LoginDTO(certificate.getId(), userId, format("{} - {} 登录成功 at {}", userId, sessionId, ZonedDateTime.now()));
    }

}
