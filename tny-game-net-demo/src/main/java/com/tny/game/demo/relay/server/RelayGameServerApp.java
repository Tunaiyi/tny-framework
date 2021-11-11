package com.tny.game.demo.relay.server;

import com.tny.game.boot.launcher.*;
import com.tny.game.demo.net.server.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.netty4.configuration.application.*;
import com.tny.game.net.netty4.relay.annotation.*;
import org.slf4j.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/31 12:34 下午
 */
@EnableRelayTargetApplication
@SpringBootApplication(scanBasePackages = {"com.tny.game.demo.relay.server", "com.tny.game.demo.core.common", "com.tny.game.demo.core.server"})
@ComponentScan(includeFilters = @Filter(Rpc.class))
public class RelayGameServerApp {

	private static final Logger LOGGER = LoggerFactory.getLogger(RelayGameServerApp.class);

	public static void main(String[] args) {
		try {
			ApplicationLauncherContext.register(RelayGameServerApp.class);
			ApplicationContext context = SpringApplication.run(RelayGameServerApp.class, args);
			NetApplication application = context.getBean(NetApplication.class);
			application.waitForConsole("q");
		} catch (Throwable e) {
			LOGGER.error("{} start exception", GameServerApp.class.getSimpleName(), e);
			System.exit(1);
		}
	}

}
