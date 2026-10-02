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
@ConfigurationProperties(prefix = "tny.net.bootstrap.rpc")
@ConditionalOnMissingBean(SpringBootRpcBootstrapProperties.class)
public class SpringBootRpcBootstrapProperties
        extends AbstractSpringBootBootstrapProperties<SpringNettyRpcServerBootstrapSetting, SpringNettyRpcClientBootstrapSetting>
        implements SpringBootNetBootstrapSettings {

    public SpringBootRpcBootstrapProperties() {
        // 现状：Rpc 侧 client 默认名为 rpc（与 Net 的 default 分裂，禁止抹平）
        super("rpc");
    }

    @Override
    public SpringNettyRpcServerBootstrapSetting getServer() {
        return super.getServer();
    }

    @Override
    public SpringBootRpcBootstrapProperties setServer(SpringNettyRpcServerBootstrapSetting server) {
        super.setServer(server);
        return this;
    }

    @Override
    public SpringNettyRpcClientBootstrapSetting getClient() {
        return super.getClient();
    }

    @Override
    public SpringBootRpcBootstrapProperties setClient(SpringNettyRpcClientBootstrapSetting client) {
        super.setClient(client);
        return this;
    }

    @Override
    public Map<String, SpringNettyRpcServerBootstrapSetting> getServers() {
        return super.getServers();
    }

    @Override
    public SpringBootRpcBootstrapProperties setServers(Map<String, SpringNettyRpcServerBootstrapSetting> servers) {
        super.setServers(servers);
        return this;
    }

    @Override
    public Map<String, SpringNettyRpcClientBootstrapSetting> getClients() {
        return super.getClients();
    }

    @Override
    public SpringBootRpcBootstrapProperties setClients(Map<String, SpringNettyRpcClientBootstrapSetting> clients) {
        super.setClients(clients);
        return this;
    }

}
