package com.tny.game.net.demo.client;

import com.tny.game.common.utils.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.demo.common.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import com.tny.game.suite.launcher.*;
import com.tny.game.suite.spring.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-30 16:49
 */
@SpringBootConfiguration
@EnableNetAutoConfiguration
@ComponentScan(
        value = {"com.tny.game.net.demo.client", "com.tny.game.net.demo.common"},
        includeFilters = @Filter(Controller.class))
public class GameClientApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameClientApp.class);

    private static final String IDS = ThreadLocalRandom.current().nextInt(1, 900) + "";

    public static void main(String[] args) {
        try {
            ApplicationContext applicationContext = SpringApplication.run(GameClientApp.class, args);
            SuitApplication application = applicationContext.getBean(SuitApplication.class);
            application.start();
            ClientGuide clientGuide = applicationContext.getBean(ClientGuide.class);
            long userId = 1000;
            AtomicInteger times = new AtomicInteger();
            Client<Long> client = clientGuide.connect(URL.valueOf("protoex://127.0.0.1:16800"), 0L,
                    tunnel -> {
                        tunnel.setAccessId(4000);
                        String message = "[" + IDS + "] 请求登录 " + times.incrementAndGet() + " 次";
                        System.out.println("!!@   [发送] 请求 = " + message);
                        SendContext<Long> context = tunnel.send(MessageContexts.<Long>requestParams(ProtocolAide.protocol(CtrlerIDs.LOGIN$LOGIN), 888888L, userId)
                                .setTail(message)
                                .willWriteFuture(30000L)
                                .willResponseFuture(30000L));
                        try {
                            Message<Long> response = context.getRespondFuture().get(300000L, TimeUnit.MILLISECONDS);
                            System.out.println("!!@   [响应] 请求 = " + response.getBody(Object.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                        return true;
                    });
            application.waitForConsole("q", (cmd, cmds) -> {
                SendContext<Long> context = client.send(MessageContexts.<Long>requestParams(ProtocolAide.protocol(CtrlerIDs.LOGIN$SAY), cmd)
                        .willResponseFuture()
                        .willWriteFuture(300000L));
                try {
                    Message<Long> message = context.getRespondFuture().get();
                    LOGGER.info("Client receive : {}", message.getBody(String.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Throwable e) {
            LOGGER.error("{} start exception", GameClientApp.class.getSimpleName(), e);
            System.exit(1);
        }
    }

}
