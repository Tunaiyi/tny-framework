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
