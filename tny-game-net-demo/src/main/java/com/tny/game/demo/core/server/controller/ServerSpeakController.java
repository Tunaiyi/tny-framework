package com.tny.game.demo.core.server.controller;

import com.tny.game.common.concurrent.*;
import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.configuration.command.*;

import java.util.concurrent.ThreadLocalRandom;

import static com.tny.game.net.message.MessageMode.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 16:46
 */
@Controller(CtrlerIDs.SPEAK)
@AuthenticationRequired(Certificates.DEFAULT_USER_TYPE)
@BeforePlugin(SpringBootParamFilterPlugin.class)
@MessageFilter(modes = {REQUEST, PUSH})
public class ServerSpeakController {

	@Controller(CtrlerIDs.SPEAK$SAY)
	public SayContentDTO say(Endpoint<Long> endpoint, @MsgParam String message) {
		endpoint.send(MessageContexts
				.push(Protocols.protocol(CtrlerIDs.SPEAK$PUSH), "因为 [" + message + "] 推条信息给你! " + ThreadLocalRandom.current().nextInt(3000)));
		return new SayContentDTO(endpoint.getId(), "respond " + message);
	}

	@Controller(CtrlerIDs.SPEAK$TEST)
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
				.push(Protocols.protocol(CtrlerIDs.SPEAK$PUSH), content));
		return new SayContentDTO(endpoint.getId(), "test result: " + content);
	}

	@Controller(CtrlerIDs.SPEAK$DELAY_SAY)
	public Waiter<SayContentDTO> delaySay(Endpoint<Long> endpoint, @MsgParam String message, @MsgParam long delay) {
		long timeout = System.currentTimeMillis() + delay;
		return new Waiter<SayContentDTO>() {

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
