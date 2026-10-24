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
