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

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 16:46
 */
@RpcController
@AuthenticationRequired(Certificates.DEFAULT_USER_TYPE)
@BeforePlugin(SpringBootParamFilterPlugin.class)
public class ServerLoginController {

	private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);

	@Rpc(CtrlerIds.LOGIN$LOGIN)
	@BeforePlugin(SpringBootParamFilterPlugin.class)
	@AuthenticationRequired(value = Certificates.DEFAULT_USER_TYPE, validator = DemoAuthenticateValidator.class)
	public LoginDTO login(Tunnel<Long> tunnel, Endpoint<Long> endpoint, @MsgParam long sessionId, @MsgParam long userId) {
		Certificate<Long> certificate = endpoint.getCertificate();
		//        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> endpoint.send(MessageContexts
		//                .push(ProtocolAide.protocol(CtrlerIDs.LOGIN$PING), "ping tunnel id " + tunnel.getId())), 0, 3, TimeUnit.SECONDS);
		return new LoginDTO(certificate.getId(), userId, format("{} - {} 登录成功 at {}", userId, sessionId, ZonedDateTime.now()));
	}

}
