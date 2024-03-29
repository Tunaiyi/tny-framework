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
package com.tny.game.demo.net.client;

import com.tny.game.boot.launcher.*;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.number.*;
import com.tny.game.common.result.*;
import com.tny.game.common.url.*;
import com.tny.game.demo.core.client.service.*;
import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.configuration.application.*;
import com.tny.game.net.netty4.network.annotation.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
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
 * @author Kun Yang
 * @date 2018-10-30 16:49
 */
@EnableNetApplication
@SpringBootApplication(
        scanBasePackages = {
                "com.tny.game.demo.net.client",
                "com.tny.game.demo.core.common",
                "com.tny.game.demo.core.client"
        }, exclude = {RedissonAutoConfigurationV2.class})
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
            ClientGuide clientGuide = applicationContext.getBean("defaultClientGuide", ClientGuide.class);
            long userId = 1000;
            AtomicInteger times = new AtomicInteger();
            var connector = new CommonTunnelConnector(clientGuide, URL.valueOf("protoex://127.0.0.1:16800"), tunnel -> {
                var futrue = new CompleteStageFuture<Boolean>();
                tunnel.setAccessId(4000);
                String message = "[" + IDS + "] 请求登录 " + times.incrementAndGet() + " 次";
                System.out.println("!!@   [发送] 请求 = " + message);
                MessageSent sent = tunnel.send(MessageContents
                        .request(Protocols.protocol(CtrlerIds.LOGIN$LOGIN), 888888L, userId)
                        .willRespondFuture(3000000L));
                sent.respond().whenComplete((response, cause) -> {
                    try {
                        if (cause != null) {
                            futrue.completeExceptionally(cause);
                            LOGGER.error("", cause);
                            return;
                        }
                        if (ResultCodes.isSuccess(response.getCode())) {
                            futrue.complete(true);
                        } else {
                            futrue.complete(false);
                        }
                    } catch (Exception e) {
                        futrue.completeExceptionally(e);
                    }
                });
                return futrue;
            }, SERVICE);
            Session session = connector.open().get();
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
                                    send(session, message, false);
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
                    send(session, Protocols.protocol(CtrlerIds.SPEAK$DELAY_SAY), SayContentDTO.class, delay + 3000, message, delay);
                } else if (cmd.startsWith("@test ")) {
                    String message = cmds[1];
                    test(session, message, true);
                } else if (cmd.startsWith("@player ")) {
                    String op = cmds[1];
                    long playerId = Long.parseLong(cmds[2]);
                    switch (op) {
                        case "g":
                            send(session, Protocols.protocol(CtrlerIds.PLAYER$GET), PlayerDTO.class, 3000, playerId);
                            break;
                        case "d":
                            send(session, Protocols.protocol(CtrlerIds.PLAYER$DELETE), PlayerDTO.class, 3000, playerId);
                            break;
                        case "a":
                            send(session, Protocols.protocol(CtrlerIds.PLAYER$ADD), PlayerDTO.class, 3000, playerId, cmds[3],
                                    Integer.parseInt(cmds[4]));
                            break;
                        case "u":
                            send(session, Protocols.protocol(CtrlerIds.PLAYER$UPDATE), PlayerDTO.class, 3000, playerId, cmds[3],
                                    Integer.parseInt(cmds[4]));
                            break;
                        case "s":
                            send(session, Protocols.protocol(CtrlerIds.PLAYER$SAVE), PlayerDTO.class, 3000, playerId, cmds[3],
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
                                LOGGER.info("Sync Call : RpcResult [ {}, {} ]", result.resultCode(), result.get());
                                break;
                            }
                            case "sb": {
                                SayContentDTO body = speakService.sayForBody(message);
                                LOGGER.info("Sync Call : Body [ {} ]", body);
                                break;
                            }
                            case "cnt": {
                                SayContentDTO content = new SayContentDTO(123, message);
                                SayContentDTO body = speakService.sayForContent(content);
                                LOGGER.info("Sync Call : Content [ {} ]", body);
                                break;
                            }
                            case "a": {
                                RpcFuture<SayContentDTO> future = speakService.asyncSay(message);
                                RpcResult<SayContentDTO> result = future.get();
                                LOGGER.info("Async Call : RpcResult [ {}, {} ]", result.resultCode(), result.get());
                                break;
                            }
                            case "ab": {
                                RpcFuture<SayContentDTO> future = speakService.asyncSay(message);
                                SayContentDTO body = future.getBody();
                                LOGGER.info("Sync Call : Body [ {} ]", body);
                                break;
                            }
                        }
                    } catch (Throwable e) {
                        LOGGER.error("", e);
                    }
                } else {
                    send(session, cmd, true);
                }

            });
        } catch (Throwable e) {
            e.printStackTrace();
            // LOGGER.error("{} start exception", GameClientApp.class.getSimpleName(), e);
            System.exit(1);
        }
    }

    private static <T> T send(Session session, Protocol protocol, Class<T> returnClass, long waitTimeout, Object... params) {
        RequestContent messageContent = MessageContents.request(protocol, params);
        if (waitTimeout > 0) {
            MessageSent context = session.send(messageContent
                    .willRespondFuture(waitTimeout));
            try {
                Message message = context.respond().get();
                T body = message.bodyAs(returnClass);
                LOGGER.info("Client receive : {}", body);
                return body;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            session.send(messageContent);
        }
        return null;
    }

    private static void send(Session session, String content, boolean wait) {
        try {
            RequestContent messageContent = MessageContents.request(Protocols.protocol(CtrlerIds.SPEAK$SAY), content);
            if (wait) {
                RpcResult<SayContentDTO> result = speakService.say(content);
                LOGGER.info("SpeakService receive [code({})] : {}", result.getCode(), result.getBody());
            } else {
                session.send(messageContent);
            }
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    private static void test(Session session, String content, boolean wait) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        RequestContent messageContent = MessageContents.request(Protocols.protocol(CtrlerIds.SPEAK$TEST),
                (byte) random.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE),
                (short) random.nextInt(Short.MIN_VALUE, Short.MAX_VALUE),
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
            MessageSent context = session.send(messageContent.willRespondFuture(300000L));
            try {
                Message message = context.respond().get();
                LOGGER.info("Client receive : {}", message.bodyAs(SayContentDTO.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            session.send(messageContent);
        }
    }

}
