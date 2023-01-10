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
package com.tny.game.demo.core.server.controller;

import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.netty4.configuration.command.*;
import com.tny.game.net.transport.*;

import java.time.ZonedDateTime;
import java.util.concurrent.*;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.base.MessagerType.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 16:46
 */
@RpcController
@AuthenticationRequired(DEFAULT_USER_TYPE)
@BeforePlugin(SpringBootParamFilterPlugin.class)
public class ServerLoginController {

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);

    @Rpc(CtrlerIds.LOGIN$LOGIN)
    @BeforePlugin(SpringBootParamFilterPlugin.class)
    @AuthenticationRequired(value = DEFAULT_USER_TYPE, validator = DemoAuthenticationValidator.class)
    public LoginDTO login(Tunnel<Long> tunnel, Endpoint<Long> endpoint, @RpcParam long sessionId, @RpcParam long userId) {
        Certificate<Long> certificate = endpoint.getCertificate();
        //        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> endpoint.send(MessageContexts
        //                .push(ProtocolAide.protocol(CtrlerIDs.LOGIN$PING), "ping tunnel id " + tunnel.getId())), 0, 3, TimeUnit.SECONDS);
        return new LoginDTO(certificate.getId(), userId, format("{} - {} 登录成功 at {}", userId, sessionId, ZonedDateTime.now()));
    }

}
