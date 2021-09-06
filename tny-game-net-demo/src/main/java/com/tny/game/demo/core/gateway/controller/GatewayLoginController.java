package com.tny.game.demo.core.gateway.controller;

import com.tny.game.demo.core.common.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.netty4.configuration.command.*;
import org.slf4j.*;

import java.time.ZonedDateTime;

import static com.tny.game.net.message.MessageMode.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 16:46
 */
@Controller(CtrlerIDs.LOGIN)
@AuthenticationRequired(Certificates.DEFAULT_USER_TYPE)
@BeforePlugin(SpringBootParamFilterPlugin.class)
@MessageFilter(modes = {REQUEST, PUSH})
public class GatewayLoginController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GatewayLoginController.class);

	@RelayTo
	@Controller(CtrlerIDs.LOGIN$LOGIN)
	@BeforePlugin(SpringBootParamFilterPlugin.class)
	@AuthenticationRequired(value = Certificates.DEFAULT_USER_TYPE, validator = DemoAuthenticateValidator.class)
	public void login(Endpoint<Long> endpoint, @MsgParam long sessionId, @MsgParam long userId) {
		LOGGER.info("{} - {} 登录成功 at {}", userId, sessionId, ZonedDateTime.now());
	}

}
