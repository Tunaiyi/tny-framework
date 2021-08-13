package com.tny.game.net.demo.client;

import com.tny.game.boot.launcher.*;
import com.tny.game.common.number.*;
import com.tny.game.common.url.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.demo.common.*;
import com.tny.game.net.demo.common.dto.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.appliaction.*;
import com.tny.game.net.netty4.configuration.annotation.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-30 16:49
 */
@SpringBootConfiguration
@EnableNetApplication
@EnableAutoConfiguration
@ComponentScan(
		basePackages = {"com.tny.game.net.demo.client", "com.tny.game.net.demo.common"},
		includeFilters = @Filter(Controller.class))
public class GameClientApp {

	private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(1);

	private static final Logger LOGGER = LoggerFactory.getLogger(GameClientApp.class);

	private static final String IDS = ThreadLocalRandom.current().nextInt(1, 900) + "";

	public static void main(String[] args) {
		try {
			ApplicationLauncherContext.register(GameClientApp.class);
			ApplicationContext applicationContext = SpringApplication.run(GameClientApp.class, args);
			NetApplication application = applicationContext.getBean(NetApplication.class);
			ClientGuide clientGuide = applicationContext.getBean(ClientGuide.class);
			long userId = 1000;
			AtomicInteger times = new AtomicInteger();
			Client<Long> client = clientGuide.connect(URL.valueOf("protoex://127.0.0.1:16800"),
					tunnel -> {
						tunnel.setAccessId(4000);
						String message = "[" + IDS + "] 请求登录 " + times.incrementAndGet() + " 次";
						System.out.println("!!@   [发送] 请求 = " + message);
						SendContext context = tunnel
								.send(MessageContexts.<Long>requestParams(Protocols.protocol(CtrlerIDs.LOGIN$LOGIN), 888888L, userId)
										.willWriteFuture(30000L)
										.willResponseFuture(30000L));
						try {
							Message response = context.getRespondFuture().get(300000L, TimeUnit.MILLISECONDS);
							System.out.println("!!@   [响应] 请求 = " + response.bodyAs(Object.class));
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
						return true;
					});
			//            Client<Long> client = clientGuide.connect(URL.valueOf("protoex://127.0.0.1:2100"),
			//                    tunnel -> {
			//                        tunnel.setAccessId(4000);
			//                        String message = "[" + IDS + "] 请求登录 " + times.incrementAndGet() + " 次";
			//                        System.out.println("!!@   [发送] 请求 = " + message);
			//                        SendContext context = tunnel
			//                                .send(MessageContexts.<Long>requestParams(Protocols.protocol(CtrlerIDs.GAME_LOGIN$LOGIN), userId)
			//                                        .willWriteFuture(30000L)
			//                                        .willResponseFuture(30000L));
			//                        try {
			//                            Message response = context.getRespondFuture().get(300000L, TimeUnit.MILLISECONDS);
			//                            System.out.println("!!@   [响应] 请求 = " + response.getBody(Object.class));
			//                        } catch (Exception e) {
			//                            e.printStackTrace();
			//                            return false;
			//                        }
			//                        return true;
			//                    });
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
					send(client, Protocols.protocol(CtrlerIDs.LOGIN$DELAY_SAY), SayContentDTO.class, delay + 3000, message, delay);
				} else if (cmd.startsWith("@test ")) {
					String message = cmds[1];
					test(client, message, true);
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
			SendContext context = client.send(messageContent
					.willResponseFuture(waitTimeout)
					.willWriteFuture(300000L));
			try {
				Message message = context.getRespondFuture().get();
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
		RequestContext messageContent = MessageContexts.<Long>requestParams(Protocols.protocol(CtrlerIDs.LOGIN$SAY), content);
		if (wait) {
			SendContext context = client.send(messageContent.willResponseFuture().willWriteFuture(300000L));
			try {
				Message message = context.getRespondFuture().get();
				LOGGER.info("Client receive : {}", message.bodyAs(SayContentDTO.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			client.send(messageContent);
		}
	}

	private static void test(Client<Long> client, String content, boolean wait) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		RequestContext messageContent = MessageContexts.requestParams(Protocols.protocol(CtrlerIDs.LOGIN$TEST),
				(byte)random.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE),
				(short)random.nextInt(Short.MIN_VALUE, Short.MAX_VALUE),
				random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE),
				random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE),
				random.nextFloat(),
				random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE),
				random.nextInt(2) == 1,
				content);
		if (wait) {
			SendContext context = client.send(messageContent.willResponseFuture().willWriteFuture(300000L));
			try {
				Message message = context.getRespondFuture().get();
				LOGGER.info("Client receive : {}", message.bodyAs(SayContentDTO.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			client.send(messageContent);
		}
	}

}
