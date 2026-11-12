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

import com.google.common.collect.ImmutableMap;
import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * SpringBootNet/RpcBootstrapProperties 共享的访问器样板父类（reduce-code-duplication D7：同包 public abstract 父，层级纯增量）。
 * <p>
 * 方法体逐字搬自原两类克隆段，现状差异仅以构造参数 {@code clientDefaultName} 承载
 * （Net="default"，Rpc="rpc"——禁止抹平），不做任何行为修正：
 * <ul>
 * <li>setServer/setClient 的改名走 Setting.setName 的 fill-if-blank 现状（已有名不覆盖，空白名补默认名），
 * null 传入置 null 不 NPE（`server: ~` 空值绑定不得击穿配置装配）；</li>
 * <li>setServers/setClients 仅对空白名条目以 map 键补名，null 传入忽略并保留现值；</li>
 * <li>getServers/getClients 每次返回现字段的只读包装视图（ImmutableMap.of() 空默认）。</li>
 * </ul>
 * 子类保留：链式 setter 协变覆写与协变 getter 覆写（消费者可见签名不动）、类上全部 Spring 注解
 * （本父类不标注 @ConfigurationProperties/@ConditionalOnMissingBean，注册点仍在子类）。
 * 绑定面回归钉桩见 BootstrapPropertiesBinderRegressionTest。
 */
public abstract class AbstractSpringBootBootstrapProperties<
        S extends SpringNettyNetServerBootstrapSetting,
        C extends SpringNettyNetClientBootstrapSetting> implements SpringBootNetBootstrapSettings {

    @NestedConfigurationProperty
    private S server;

    @NestedConfigurationProperty
    private C client;

    //    @NestedConfigurationProperty
    private Map<String, S> servers = ImmutableMap.of();

    //    @NestedConfigurationProperty
    private Map<String, C> clients = ImmutableMap.of();

    private final String clientDefaultName;

    protected AbstractSpringBootBootstrapProperties(String clientDefaultName) {
        this.clientDefaultName = clientDefaultName;
    }

    @Override
    public S getServer() {
        return this.server;
    }

    public AbstractSpringBootBootstrapProperties<S, C> setServer(S server) {
        this.server = server;
        if (server != null) {
            // `server: ~` 空值绑定不得 NPE 击穿配置装配
            server.setName("default");
        }
        return this;
    }

    @Override
    public C getClient() {
        return this.client;
    }

    public AbstractSpringBootBootstrapProperties<S, C> setClient(C client) {
        this.client = client;
        if (client != null) {
            client.setName(clientDefaultName);
        }
        return this;
    }

    @Override
    public Map<String, S> getServers() {
        return Collections.unmodifiableMap(this.servers);
    }

    public AbstractSpringBootBootstrapProperties<S, C> setServers(Map<String, S> servers) {
        if (servers != null) {
            servers.forEach((name, setting) -> setting.setName(name));
            this.servers = servers;
        }
        return this;
    }

    @Override
    public Map<String, C> getClients() {
        return Collections.unmodifiableMap(this.clients);
    }

    public AbstractSpringBootBootstrapProperties<S, C> setClients(Map<String, C> clients) {
        if (clients != null) {
            clients.forEach((name, setting) -> setting.setName(name));
            this.clients = clients;
        }
        return this;
    }

}
