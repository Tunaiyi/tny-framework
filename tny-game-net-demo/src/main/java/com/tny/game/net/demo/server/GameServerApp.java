package com.tny.game.net.demo.server;

import com.tny.game.net.annotation.Controller;
import com.tny.game.net.netty4.codec.*;
import com.tny.game.suite.annotation.EnableNetAutoConfiguration;
import com.tny.game.suite.launcher.SuitApplication;
import com.tny.game.suite.net.spring.ReadTimeoutChannelMaker;
import org.slf4j.*;
import org.springframework.boot.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-30 16:49
 */
@SpringBootConfiguration
@EnableNetAutoConfiguration
@ComponentScan(
        value = {"com.tny.game.net.demo.server", "com.tny.game.net.demo.common"},
        includeFilters = @Filter(Controller.class))
@PropertySource("classpath:server-application.properties")
public class GameServerApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerApp.class);

    public static void main(String[] args) {
        try {
            SuitApplication application = new SuitApplication(() -> SpringApplication.run(GameServerApp.class, args));
            application.start();
            ApplicationContext context = application.getContext();
            context.getBeansOfType(DataPacketV1Encoder.class).forEach((k, v) -> System.out.println(k + " : " + v));
            context.getBeansOfType(DataPacketV1Decoder.class).forEach((k, v) -> System.out.println(k + " : " + v));
            context.getBeansOfType(ReadTimeoutChannelMaker.class).forEach((k, v) -> System.out.println(k + " : " + v.getIdleTimeout()));
            application.waitForConsole("q");
        } catch (Throwable e) {
            LOGGER.error("{} start exception", GameServerApp.class.getSimpleName(), e);
            System.exit(1);
        }
    }

}
