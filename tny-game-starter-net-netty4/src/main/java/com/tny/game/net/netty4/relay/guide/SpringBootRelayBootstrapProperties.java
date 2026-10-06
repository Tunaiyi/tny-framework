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

package com.tny.game.net.netty4.relay.guide;

import com.google.common.collect.ImmutableMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 * 不主要加 @Configuration
 * 通过 ImportNetBootstrapDefinitionRegistrar 注册
 *
 * @author : kgtny
 * @date : 2021/7/13 8:26 下午
 */
//@Order(HIGHEST_PRECEDENCE)
@ConfigurationProperties(prefix = "tny.net.bootstrap.relay")
@ConditionalOnMissingBean(SpringBootRelayBootstrapProperties.class)
public class SpringBootRelayBootstrapProperties {

    @NestedConfigurationProperty
    private SpringNettyRelayServerBootstrapSetting server;

    @NestedConfigurationProperty
    private SpringNettyRelayClientBootstrapSetting client;

    //    @NestedConfigurationProperty
    private Map<String, SpringNettyRelayServerBootstrapSetting> servers = ImmutableMap.of();

    //    @NestedConfigurationProperty
    private Map<String, SpringNettyRelayClientBootstrapSetting> clients = ImmutableMap.of();

    public SpringNettyRelayServerBootstrapSetting getServer() {
        return this.server;
    }

    public SpringBootRelayBootstrapProperties setServer(SpringNettyRelayServerBootstrapSetting server) {
        this.server = server;
        if (server != null) {
            // `server: ~` 空值绑定不得 NPE 击穿配置装配
            server.setName("default");
        }
        return this;
    }

    public SpringNettyRelayClientBootstrapSetting getClient() {
        return this.client;
    }

    public SpringBootRelayBootstrapProperties setClient(SpringNettyRelayClientBootstrapSetting client) {
        this.client = client;
        if (client != null) {
            client.setName("default");
        }
        return this;
    }

    public Map<String, SpringNettyRelayServerBootstrapSetting> getServers() {
        return Collections.unmodifiableMap(this.servers);
    }

    public SpringBootRelayBootstrapProperties setServers(Map<String, SpringNettyRelayServerBootstrapSetting> servers) {
        if (servers != null) {
            servers.forEach((name, setting) -> setting.setName(name));
            this.servers = servers;
        }
        return this;
    }

    public Map<String, SpringNettyRelayClientBootstrapSetting> getClients() {
        return Collections.unmodifiableMap(this.clients);
    }

    public SpringBootRelayBootstrapProperties setClients(Map<String, SpringNettyRelayClientBootstrapSetting> clients) {
        if (clients != null) {
            clients.forEach((name, setting) -> setting.setName(name));
            this.clients = clients;
        }
        return this;
    }

}
