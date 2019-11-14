package com.tny.game.net.demo.server;

import com.tny.game.net.annotation.*;
import com.tny.game.suite.launcher.*;
import com.tny.game.suite.spring.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

/**
 * <p>
 */
@SpringBootConfiguration
@EnableNetAutoConfiguration
@ComponentScan(
        value = {"com.tny.game.net.demo.server", "com.tny.game.net.demo.common"},
        includeFilters = @Filter(Controller.class))
public class GameServerApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerApp.class);

    public static void main(String[] args) {
        try {
            ApplicationContext context = SpringApplication.run(GameServerApp.class, args);
            SuitApplication application = context.getBean(SuitApplication.class);
            application.start();
            application.waitForConsole("q");
        } catch (Throwable e) {
            LOGGER.error("{} start exception", GameServerApp.class.getSimpleName(), e);
            System.exit(1);
        }
    }

}
