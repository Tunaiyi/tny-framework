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

package com.tny.game.net.netty4.network.guide;

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
public class SpringBootNetBootstrapProperties
        extends AbstractSpringBootBootstrapProperties<SpringNettyNetServerBootstrapSetting, SpringNettyNetClientBootstrapSetting>
        implements SpringBootNetBootstrapSettings {

    public SpringBootNetBootstrapProperties() {
        // 现状：Net 侧 client 默认名为 default
        super("default");
    }

    @Override
    public SpringNettyNetServerBootstrapSetting getServer() {
        return super.getServer();
    }

    @Override
    public SpringBootNetBootstrapProperties setServer(SpringNettyNetServerBootstrapSetting server) {
        super.setServer(server);
        return this;
    }

    @Override
    public SpringNettyNetClientBootstrapSetting getClient() {
        return super.getClient();
    }

    @Override
    public SpringBootNetBootstrapProperties setClient(SpringNettyNetClientBootstrapSetting client) {
        super.setClient(client);
        return this;
    }

    @Override
    public Map<String, SpringNettyNetServerBootstrapSetting> getServers() {
        return super.getServers();
    }

    @Override
    public SpringBootNetBootstrapProperties setServers(Map<String, SpringNettyNetServerBootstrapSetting> servers) {
        super.setServers(servers);
        return this;
    }

    @Override
    public Map<String, SpringNettyNetClientBootstrapSetting> getClients() {
        return super.getClients();
    }

    @Override
    public SpringBootNetBootstrapProperties setClients(Map<String, SpringNettyNetClientBootstrapSetting> clients) {
        super.setClients(clients);
        return this;
    }

}
