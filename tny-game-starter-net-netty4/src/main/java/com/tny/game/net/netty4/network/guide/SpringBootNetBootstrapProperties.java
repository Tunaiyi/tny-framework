/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.network.guide;

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
@ConfigurationProperties(prefix = "tny.net.bootstrap.network")
@ConditionalOnMissingBean(SpringBootNetBootstrapProperties.class)
public class SpringBootNetBootstrapProperties implements SpringBootNetBootstrapSettings {

    @NestedConfigurationProperty
    private SpringNettyNetServerBootstrapSetting server;

    @NestedConfigurationProperty
    private SpringNettyNetClientBootstrapSetting client;

    //    @NestedConfigurationProperty
    private Map<String, SpringNettyNetServerBootstrapSetting> servers = ImmutableMap.of();

    //    @NestedConfigurationProperty
    private Map<String, SpringNettyNetClientBootstrapSetting> clients = ImmutableMap.of();

    @Override
    public SpringNettyNetServerBootstrapSetting getServer() {
        return this.server;
    }

    public SpringBootNetBootstrapProperties setServer(SpringNettyNetServerBootstrapSetting server) {
        this.server = server;
        this.server.setName("default");
        return this;
    }

    @Override
    public SpringNettyNetClientBootstrapSetting getClient() {
        return this.client;
    }

    public SpringBootNetBootstrapProperties setClient(SpringNettyNetClientBootstrapSetting client) {
        this.client = client;
        this.client.setName("default");
        return this;
    }

    @Override
    public Map<String, SpringNettyNetServerBootstrapSetting> getServers() {
        return Collections.unmodifiableMap(this.servers);
    }

    public SpringBootNetBootstrapProperties setServers(Map<String, SpringNettyNetServerBootstrapSetting> servers) {
        if (servers != null) {
            servers.forEach((name, setting) -> setting.setName(name));
            this.servers = servers;
        }
        return this;
    }

    @Override
    public Map<String, SpringNettyNetClientBootstrapSetting> getClients() {
        return Collections.unmodifiableMap(this.clients);
    }

    public SpringBootNetBootstrapProperties setClients(Map<String, SpringNettyNetClientBootstrapSetting> clients) {
        if (clients != null) {
            clients.forEach((name, setting) -> setting.setName(name));
            this.clients = clients;
        }
        return this;
    }

}
