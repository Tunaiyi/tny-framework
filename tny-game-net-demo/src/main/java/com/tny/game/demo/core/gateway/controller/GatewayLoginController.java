package com.tny.game.demo.core.gateway.controller;

import com.tny.game.demo.core.common.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.netty4.configuration.command.*;
import org.slf4j.*;

import java.time.ZonedDateTime;

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
public class GatewayLoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayLoginController.class);

    @RelayTo
    @Rpc(CtrlerIds.LOGIN$LOGIN)
    @BeforePlugin(SpringBootParamFilterPlugin.class)
    @AuthenticationRequired(value = DEFAULT_USER_TYPE, validator = DemoAuthenticateValidator.class)
    public void login(Endpoint<Long> endpoint, @RpcParam long sessionId, @RpcParam long userId) {
        LOGGER.info("{} - {} 登录成功 at {}", userId, sessionId, ZonedDateTime.now());
    }

}
