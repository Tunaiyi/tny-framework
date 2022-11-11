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
package com.tny.game.demo.relay.gateway;

import com.tny.game.boot.launcher.*;
import com.tny.game.demo.net.server.*;
import com.tny.game.net.netty4.configuration.application.*;
import com.tny.game.net.netty4.network.annotation.*;
import com.tny.game.net.netty4.relay.annotation.*;
import org.slf4j.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/31 12:34 下午
 */
@EnableNetApplication
@EnableRelayClientApplication
@SpringBootApplication(scanBasePackages = {"com.tny.game.demo.relay.gateway", "com.tny.game.demo.core.common", "com.tny.game.demo.core.gateway"})
public class RelayGatewayServerApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelayGatewayServerApp.class);

    public static void main(String[] args) {
        try {
            ApplicationLauncherContext.register(RelayGatewayServerApp.class);
            ApplicationContext context = SpringApplication.run(RelayGatewayServerApp.class, args);
            NetApplication application = context.getBean(NetApplication.class);
            application.waitForConsole("q");
        } catch (Throwable e) {
            LOGGER.error("{} start exception", GameServerApp.class.getSimpleName(), e);
            System.exit(1);
        }
    }

}
