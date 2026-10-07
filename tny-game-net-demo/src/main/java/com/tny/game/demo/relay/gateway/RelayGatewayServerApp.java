/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
@SpringBootApplication(
        scanBasePackages = {
                "com.tny.game.demo.relay.gateway",
                "com.tny.game.demo.core.common",
                "com.tny.game.demo.core.gateway"
        })
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
