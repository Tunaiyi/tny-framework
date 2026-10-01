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
