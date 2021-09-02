package com.tny.game.demo.core.client.controller;

import com.tny.game.common.result.*;
import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.command.*;
import com.tny.game.net.netty4.configuration.command.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

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
@MessageFilter(modes = {RESPONSE, PUSH})
public class ClientLoginController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientLoginController.class);

	@Controller(CtrlerIDs.LOGIN$LOGIN)
	@BeforePlugin(SpringBootParamFilterPlugin.class)
	@AuthenticationRequired(value = Certificates.DEFAULT_USER_TYPE, validator = DemoAuthenticateValidator.class)
	public void login(@MsgCode int code, @MsgBody LoginDTO dto) {
		if (!ResultCodes.isSuccess(code)) {
			LOGGER.info("Login failed : {}", code);
		} else {
			LOGGER.info("{} Login finish : {}", dto.getUserId(), dto.getMessage());
		}
	}

	@Controller(CtrlerIDs.SPEAK$PUSH)
	@BeforePlugin(SpringBootParamFilterPlugin.class)
	public void pushMessage(Tunnel<Long> tunnel, @MsgBody String message) {
		//        LOGGER.info("User {} [accessId {}]receive push message {}", tunnel.getUserId(), tunnel.getAccessId(), message);
	}

	@Controller(CtrlerIDs.SPEAK$PING)
	@BeforePlugin(SpringBootParamFilterPlugin.class)
	public void pingMessage(Tunnel<Long> tunnel, @MsgBody String message) {
		LOGGER.info("User {} [accessId {}] receive : {}", tunnel.getUserId(), tunnel.getAccessId(), message);
	}

}
