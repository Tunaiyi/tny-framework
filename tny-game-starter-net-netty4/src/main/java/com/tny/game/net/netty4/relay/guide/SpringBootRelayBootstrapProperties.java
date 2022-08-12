/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
        this.server.setName("default");
        return this;
    }

    public SpringNettyRelayClientBootstrapSetting getClient() {
        return this.client;
    }

    public SpringBootRelayBootstrapProperties setClient(SpringNettyRelayClientBootstrapSetting client) {
        this.client = client;
        this.client.setName("default");
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
