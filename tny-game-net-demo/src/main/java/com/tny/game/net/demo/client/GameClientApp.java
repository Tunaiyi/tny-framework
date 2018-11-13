package com.tny.game.net.demo.client;

import com.tny.game.common.utils.URL;
import com.tny.game.net.annotation.Controller;
import com.tny.game.net.base.ClientGuide;
import com.tny.game.net.demo.common.CtrlerIDs;
import com.tny.game.net.endpoint.Client;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import com.tny.game.suite.annotation.EnableNetAutoConfiguration;
import com.tny.game.suite.launcher.SuitApplication;
import org.slf4j.*;
import org.springframework.boot.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;

import java.util.concurrent.*;
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
@PropertySource("classpath:client-application.properties")
public class GameClientApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameClientApp.class);

    private static final String IDS = ThreadLocalRandom.current().nextInt(1, 900) + "";

    public static void main(String[] args) {
        try {
            SuitApplication application = new SuitApplication(() -> SpringApplication.run(GameClientApp.class, args));
            application.start();
            ApplicationContext applicationContext = application.getContext();
            ClientGuide clientGuide = applicationContext.getBean(ClientGuide.class);
            long userId = 1000;
            AtomicInteger times = new AtomicInteger();
            Client<Long> client = clientGuide.connect(URL.valueOf("protoex://127.0.0.1:16800"),
                    Certificates.createAutherized(System.currentTimeMillis(), userId, Certificates.DEFAULT_USER_TYPE),
                    tunnel -> {
                        tunnel.setAccessId(4000);
                        String message = "[" + IDS + "] 请求登录 " + times.incrementAndGet() + " 次";
                        System.out.println("!!@   [发送] 请求 = " + message);
                        SendContext<Long> context = tunnel.sendSync(MessageContexts.<Long>requestParams(ProtocolAide.protocol(CtrlerIDs.LOGIN$LOGIN), 888888L, userId)
                                .setAttachment(message)
                                .willResponseFuture(), 300000L);
                        try {
                            Message<Long> response = context.getRespondFuture().get(300000L, TimeUnit.MILLISECONDS);
                            MessageHeader header = response.getHeader();
                            System.out.println("!!@   [响应] 请求 = " + header.getAttachment(Object.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                        return true;
                    });
            application.waitForConsole("q", (cmd, cmds) -> {
                SendContext<Long> context = client.sendSync(MessageContexts.<Long>requestParams(ProtocolAide.protocol(CtrlerIDs.LOGIN$SAY), cmd)
                        .willResponseFuture(), 300000L);
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
