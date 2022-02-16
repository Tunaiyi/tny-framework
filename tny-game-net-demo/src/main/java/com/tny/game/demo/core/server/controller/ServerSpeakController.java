package com.tny.game.demo.core.server.controller;

import com.tny.game.common.concurrent.*;
import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.configuration.command.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 16:46
 */
@RpcController
@AuthenticationRequired({Certificates.DEFAULT_USER_TYPE, "game-client"})
@BeforePlugin(SpringBootParamFilterPlugin.class)
public class ServerSpeakController {

	@Rpc(CtrlerIds.SPEAK$SAY)
	public SayContentDTO say(Endpoint<Long> endpoint, @MsgParam String message) {
		endpoint.send(MessageContexts
				.push(Protocols.protocol(CtrlerIds.SPEAK$PUSH), "因为 [" + message + "] 推条信息给你! " + ThreadLocalRandom.current().nextInt(3000)));
		return new SayContentDTO(endpoint.getId(), "respond " + message);
	}

	@Rpc(CtrlerIds.SPEAK$SAY_FOR_RPC)
	public SayContentDTO say(@UserId RpcLinkerId id, @MsgParam String message) {
		return new SayContentDTO(id.getId(), "respond " + message);
	}

	@Rpc(CtrlerIds.SPEAK$TEST)
	public SayContentDTO test(Endpoint<Long> endpoint,
			@MsgParam byte byteValue,
			@MsgParam short shortValue,
			@MsgParam int intValue,
			@MsgParam long longValue,
			@MsgParam float floatValue,
			@MsgParam double doubleValue,
			@MsgParam boolean booleanValue,
			@MsgParam String message) {
		String content = "\nbyteValue:" + byteValue +
				"\nshortValue:" + shortValue +
				"\nintValue:" + intValue +
				"\nlongValue:" + longValue +
				"\nfloatValue:" + floatValue +
				"\ndoubleValue:" + doubleValue +
				"\nbooleanValue:" + booleanValue +
				"\nmessage:" + message;
		endpoint.send(MessageContexts
				.push(Protocols.protocol(CtrlerIds.SPEAK$PUSH), content));
		return new SayContentDTO(endpoint.getId(), "test result: " + content);
	}

	@Rpc(CtrlerIds.SPEAK$DELAY_SAY)
	public Wait<SayContentDTO> delaySay(Endpoint<Long> endpoint, @MsgParam String message, @MsgParam long delay) {
		long timeout = System.currentTimeMillis() + delay;
		return new Wait<SayContentDTO>() {

			@Override
			public boolean isDone() {
				return System.currentTimeMillis() - timeout > 0;
			}

			@Override
			public boolean isFailed() {
				return false;
			}

			@Override
			public boolean isSuccess() {
				return isDone();
			}

			@Override
			public Throwable getCause() {
				return null;
			}

			@Override
			public SayContentDTO getResult() {
				System.out.println(message);
				return new SayContentDTO(endpoint.getId(), "delay message : " + message);
			}
		};
	}

}
