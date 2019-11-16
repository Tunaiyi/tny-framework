package com.tny.game.net.demo.server;

import com.tny.game.net.annotation.*;
import com.tny.game.starter.net.netty4.appliaction.*;
import com.tny.game.starter.net.netty4.configuration.annotation.*;
import org.slf4j.*;
import org.springframework.boot.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

/**
 * <p>
 */
@SpringBootConfiguration
@EnableNetApplication
@ComponentScan(
        value = {"com.tny.game.net.demo.server", "com.tny.game.net.demo.common"},
        includeFilters = @Filter(Controller.class))
public class GameServerApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerApp.class);

    public static void main(String[] args) {
        try {
            ApplicationContext context = SpringApplication.run(GameServerApp.class, args);
            NetApplication application = context.getBean(NetApplication.class);
            application.start();
            application.waitForConsole("q");
        } catch (Throwable e) {
            LOGGER.error("{} start exception", GameServerApp.class.getSimpleName(), e);
            System.exit(1);
        }
    }

}
