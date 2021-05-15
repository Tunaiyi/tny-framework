package com.tny.game.net.demo.client;

import com.tny.game.common.number.*;
import com.tny.game.common.url.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.demo.common.*;
import com.tny.game.net.demo.common.dto.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import com.tny.game.starter.net.netty4.appliaction.*;
import com.tny.game.starter.net.netty4.configuration.annotation.*;
import org.slf4j.*;
import org.springframework.boot.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
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
@EnableNetApplication
@ComponentScan(
        value = {"com.tny.game.net.demo.client", "com.tny.game.net.demo.common", "com.tny.game.starter"},
        includeFilters = @Filter(Controller.class))
public class GameClientApp {

    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(1);

    private static final Logger LOGGER = LoggerFactory.getLogger(GameClientApp.class);

    private static final String IDS = ThreadLocalRandom.current().nextInt(1, 900) + "";

    public static void main(String[] args) {
        try {
            ApplicationContext applicationContext = SpringApplication.run(GameClientApp.class, args);
            NetApplication application = applicationContext.getBean(NetApplication.class);
            application.start();
            ClientGuide clientGuide = applicationContext.getBean(ClientGuide.class);
            long userId = 1000;
            AtomicInteger times = new AtomicInteger();
            Client<Long> client = clientGuide.connect(URL.valueOf("protoex://127.0.0.1:16800"), tunnel -> {
                tunnel.setAccessId(4000);
                String message = "[" + IDS + "] 请求登录 " + times.incrementAndGet() + " 次";
                System.out.println("!!@   [发送] 请求 = " + message);
                SendContext<Long> context = tunnel
                        .send(MessageContexts.<Long>requestParams(ProtocolAide.protocol(CtrlerIDs.LOGIN$LOGIN), 888888L, userId)
                                .setTail(message)
                                .willWriteFuture(30000L)
                                .willResponseFuture(30000L));
                try {
                    Message response = context.getRespondFuture().get(300000L, TimeUnit.MILLISECONDS);
                    System.out.println("!!@   [响应] 请求 = " + response.getBody(Object.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            });
            application.waitForConsole("q", (cmd, cmds) -> {
                if (cmd.startsWith("@t")) {
                    if (cmds.length > 3) {
                        long delay = Long.parseLong(cmds[2]);
                        SERVICE.schedule(new Runnable() {

                            private final IntLocalNum num = new IntLocalNum(Integer.parseInt(cmds[1]));

                            @Override
                            public void run() {
                                send(client, cmds[3], false);
                                if (this.num.sub(1) > 0) {
                                    SERVICE.schedule(this, delay, TimeUnit.MILLISECONDS);
                                }
                            }
                        }, delay, TimeUnit.MILLISECONDS);
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

    private static void send(Client<Long> client, String content, boolean wait) {
        RequestContext<Long> messageContent = MessageContexts.<Long>requestParams(ProtocolAide.protocol(CtrlerIDs.LOGIN$SAY), content);
        if (wait) {
            SendContext<Long> context = client.send(messageContent.willResponseFuture().willWriteFuture(300000L));
            try {
                Message message = context.getRespondFuture().get();
                LOGGER.info("Client receive : {}", message.getBody(SayContentDTO.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            client.send(messageContent);
        }
    }

}
