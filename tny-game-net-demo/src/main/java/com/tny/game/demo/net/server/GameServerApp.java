package com.tny.game.demo.net.server;

import com.tny.game.boot.launcher.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.netty4.configuration.application.*;
import com.tny.game.net.netty4.network.annotation.*;
import org.slf4j.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

/**
 * <p>
 */
@SpringBootConfiguration
@EnableNetApplication
@EnableAutoConfiguration
@ComponentScan(
		basePackages = {"com.tny.game.demo.net.server", "com.tny.game.demo.core.common", "com.tny.game.demo.core.server"},
		includeFilters = @Filter(Controller.class))
public class GameServerApp {

	private static final Logger LOGGER = LoggerFactory.getLogger(GameServerApp.class);

	public static void main(String[] args) {
		try {
			ApplicationLauncherContext.register(GameServerApp.class);
			ApplicationContext context = SpringApplication.run(GameServerApp.class, args);
			NetApplication application = context.getBean(NetApplication.class);
			//                        application.start();
			application.waitForConsole("q");
		} catch (Throwable e) {
			LOGGER.error("{} start exception", GameServerApp.class.getSimpleName(), e);
			System.exit(1);
		}
	}

}
