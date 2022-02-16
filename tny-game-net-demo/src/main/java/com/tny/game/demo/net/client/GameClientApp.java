package com.tny.game.demo.net.client;

import com.tny.game.boot.launcher.*;
import com.tny.game.common.number.*;
import com.tny.game.common.url.*;
import com.tny.game.demo.core.client.service.*;
import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.configuration.application.*;
import com.tny.game.net.netty4.network.annotation.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-30 16:49
 */
@EnableNetApplication
@SpringBootApplication(scanBasePackages = {
		"com.tny.game.demo.net.client",
		"com.tny.game.demo.core.common",
		"com.tny.game.demo.core.client"})
public class GameClientApp {

	private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(1);

	private static final Logger LOGGER = LoggerFactory.getLogger(GameClientApp.class);

	private static final String IDS = ThreadLocalRandom.current().nextInt(1, 900) + "";

	private static SpeakRemoteService speakService;

	public static void main(String[] args) {
		try {
			ApplicationLauncherContext.register(GameClientApp.class);
			ApplicationContext applicationContext = SpringApplication.run(GameClientApp.class, args);
			NetApplication application = applicationContext.getBean(NetApplication.class);
			ClientGuide clientGuide = applicationContext.getBean(ClientGuide.class);
			long userId = 1000;
			AtomicInteger times = new AtomicInteger();
			Client<Long> client = clientGuide.client(URL.valueOf("protoex://127.0.0.1:18800"),
					tunnel -> {
						tunnel.setAccessId(4000);
						String message = "[" + IDS + "] 请求登录 " + times.incrementAndGet() + " 次";
						System.out.println("!!@   [发送] 请求 = " + message);
						SendReceipt context = tunnel
								.send(MessageContexts.requestParams(Protocols.protocol(CtrlerIds.LOGIN$LOGIN), 888888L, userId)
										.willRespondAwaiter(3000000L));
						try {
							Message response = context.respond().get(300000L, TimeUnit.MILLISECONDS);
							System.out.println("!!@   [响应] 请求 = " + response.bodyAs(Object.class));
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
						return true;
					});
			client.open();
			speakService = applicationContext.getBean(SpeakRemoteService.class);
			application.waitForConsole("q", (cmd, cmds) -> {
				if (cmd.startsWith("@t ")) {
					if (cmds.length > 4) {
						int totalTimes = Integer.parseInt(cmds[1]);
						int stepTimes = Integer.parseInt(cmds[2]);
						long delay = Long.parseLong(cmds[3]);
						String message = cmds[4];
						System.out.println(format("每 {} ms 发送 \"{}\" {} 次, 总共 {} 次.", delay, message, stepTimes, totalTimes));
						SERVICE.schedule(new Runnable() {

							private final IntLocalNum num = new IntLocalNum(totalTimes);

							@Override
							public void run() {
								int index = 0;
								while (this.num.sub(1) > 0) {
									send(client, message, false);
									if (++index > stepTimes) {
										break;
									}
								}
								if (this.num.intValue() > 0) {
									SERVICE.schedule(this, delay, TimeUnit.MILLISECONDS);
								}
							}
						}, delay, TimeUnit.MILLISECONDS);
					}
				} else if (cmd.startsWith("@delay ")) {
					String message = cmds[1];
					long delay = Long.parseLong(cmds[2]);
					send(client, Protocols.protocol(CtrlerIds.SPEAK$DELAY_SAY), SayContentDTO.class, delay + 3000, message, delay);
				} else if (cmd.startsWith("@test ")) {
					String message = cmds[1];
					test(client, message, true);
				} else if (cmd.startsWith("@player ")) {
					String op = cmds[1];
					long playerId = Long.parseLong(cmds[2]);
					switch (op) {
						case "g":
							send(client, Protocols.protocol(CtrlerIds.PLAYER$GET), PlayerDTO.class, 3000, playerId);
							break;
						case "d":
							send(client, Protocols.protocol(CtrlerIds.PLAYER$DELETE), PlayerDTO.class, 3000, playerId);
							break;
						case "a":
							send(client, Protocols.protocol(CtrlerIds.PLAYER$ADD), PlayerDTO.class, 3000, playerId, cmds[3],
									Integer.parseInt(cmds[4]));
							break;
						case "u":
							send(client, Protocols.protocol(CtrlerIds.PLAYER$UPDATE), PlayerDTO.class, 3000, playerId, cmds[3],
									Integer.parseInt(cmds[4]));
							break;
						case "s":
							send(client, Protocols.protocol(CtrlerIds.PLAYER$SAVE), PlayerDTO.class, 3000, playerId, cmds[3],
									Integer.parseInt(cmds[4]));
							break;
					}
				} else if (cmd.startsWith("@rpc ")) {
					String op = cmds[1];
					String message = cmds[2];
					try {
						switch (op) {
							case "s": {
								RpcResult<SayContentDTO> result = speakService.say(message);
								LOGGER.info("Sync Call : RpcResult [ {}, {} ]", result.getResultCode(), result.get());
								break;
							}
							case "sb": {
								SayContentDTO body = speakService.sayForBody(message);
								LOGGER.info("Sync Call : Body [ {} ]", body);
								break;
							}
							case "a": {
								RpcFuture<RpcResult<SayContentDTO>> future = speakService.asyncSay(message);
								RpcResult<SayContentDTO> result = future.get();
								LOGGER.info("Async Call : RpcResult [ {}, {} ]", result.getResultCode(), result.get());
								break;
							}
							case "ab": {
								RpcFuture<SayContentDTO> future = speakService.asyncSayForBody(message);
								SayContentDTO body = future.get();
								LOGGER.info("Sync Call : Body [ {} ]", body);
								break;
							}
						}
					} catch (Throwable e) {
						LOGGER.error("", e);
					}
				} else {
					send(client, cmd, true);
				}

			});
		} catch (Throwable e) {
			LOGGER.error("{} start exception", GameClientApp.class.getSimpleName(), e);
			System.exit(1);
		}
	}

	private static <T> T send(Client<Long> client, Protocol protocol, Class<T> returnClass, long waitTimeout, Object... params) {
		RequestContext messageContent = MessageContexts.requestParams(protocol, params);
		if (waitTimeout > 0) {
			SendReceipt context = client.send(messageContent
					.willRespondAwaiter(waitTimeout));
			try {
				Message message = context.respond().get();
				T body = message.bodyAs(returnClass);
				LOGGER.info("Client receive : {}", body);
				return body;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			client.send(messageContent);
		}
		return null;
	}

	private static void send(Client<Long> client, String content, boolean wait) {
		RequestContext messageContent = MessageContexts.requestParams(Protocols.protocol(CtrlerIds.SPEAK$SAY), content);
		if (wait) {
			RpcResult<SayContentDTO> result = speakService.say(content);
			LOGGER.info("SpeakService receive [code({})] : {}", result.getCode(), result.getBody());
		} else {
			client.send(messageContent);
		}
	}

	private static void test(Client<Long> client, String content, boolean wait) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		RequestContext messageContent = MessageContexts.requestParams(Protocols.protocol(CtrlerIds.SPEAK$TEST),
				(byte)random.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE),
				(short)random.nextInt(Short.MIN_VALUE, Short.MAX_VALUE),
				random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE),
				random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE),
				random.nextFloat(),
				random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE),
				random.nextInt(2) == 1,
				content);
		//		RequestContext messageContent = MessageContexts.requestParams(Protocols.protocol(CtrlerIds.SPEAK$TEST),
		//				(byte)22,
		//				(short)-11163,
		//				-593234928,
		//				7561557239560349744L,
		//				0.24013591F,
		//				8.104598623440516E306,
		//				false,
		//				"jdsaf");
		System.out.println(messageContent.bodyAs(Object.class));
		if (wait) {
			SendReceipt context = client.send(messageContent.willRespondAwaiter(300000L));
			try {
				Message message = context.respond().get();
				LOGGER.info("Client receive : {}", message.bodyAs(SayContentDTO.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			client.send(messageContent);
		}
	}

}
