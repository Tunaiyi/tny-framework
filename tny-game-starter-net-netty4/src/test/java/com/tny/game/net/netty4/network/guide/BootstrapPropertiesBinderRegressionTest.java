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

import org.junit.jupiter.api.*;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * reduce-code-duplication D7 绑定面回归钉桩（重构前绿）：SpringBootNet/RpcBootstrapProperties
 * 的键集 / 默认值 / setter 改名语义 / 嵌套 Setting 绑定现状账。
 * 生产路径两条都要钉：Registrar 的 `Binder.get(env).bind(prefix, Class)`（loadProperties）与
 * 链式 setter 直改（下游 new XxxProperties().setServer(s)）。提父收敛后期望值一字不改仍须全绿。
 */
class BootstrapPropertiesBinderRegressionTest {

    private static final String NET_PREFIX = "tny.net.bootstrap.network";
    private static final String RPC_PREFIX = "tny.net.bootstrap.rpc";

    private static Binder binderOf(String... lines) throws Exception {
        Properties properties = new Properties();
        properties.load(new StringReader(String.join("\n", lines)));
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new PropertiesPropertySource("pin", properties));
        return Binder.get(environment);
    }

    private static List<String> beanPropertyNames(Class<?> type) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(type, Object.class);
        return Arrays.stream(beanInfo.getPropertyDescriptors())
                .map(PropertyDescriptor::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    // ---------- 键集现状 ----------

    @Test
    @DisplayName("键集现状：Net/Rpc 均为 {client, clients, server, servers}，禁止父类泄漏多余键")
    void beanKeySet() throws Exception {
        assertEquals(List.of("client", "clients", "server", "servers"),
                beanPropertyNames(SpringBootNetBootstrapProperties.class));
        assertEquals(List.of("client", "clients", "server", "servers"),
                beanPropertyNames(SpringBootRpcBootstrapProperties.class));
    }

    // ---------- 默认值现状 ----------

    @Test
    @DisplayName("Net 默认值：server/client 为 null，servers/clients 空且 getter 返回只读包装（put 抛 UOE）")
    void netDefaults() {
        SpringBootNetBootstrapProperties properties = new SpringBootNetBootstrapProperties();
        assertNull(properties.getServer());
        assertNull(properties.getClient());
        assertTrue(properties.getServers().isEmpty());
        assertTrue(properties.getClients().isEmpty());
        assertThrows(UnsupportedOperationException.class,
                () -> properties.getServers().put("x", new SpringNettyNetServerBootstrapSetting()));
        assertThrows(UnsupportedOperationException.class,
                () -> properties.getClients().put("x", new SpringNettyNetClientBootstrapSetting()));
    }

    @Test
    @DisplayName("Rpc 默认值：同 Net 形态")
    void rpcDefaults() {
        SpringBootRpcBootstrapProperties properties = new SpringBootRpcBootstrapProperties();
        assertNull(properties.getServer());
        assertNull(properties.getClient());
        assertTrue(properties.getServers().isEmpty());
        assertTrue(properties.getClients().isEmpty());
        assertThrows(UnsupportedOperationException.class,
                () -> properties.getServers().put("x", new SpringNettyRpcServerBootstrapSetting()));
    }

    // ---------- setter 改名与空值语义现状 ----------

    @Test
    @DisplayName("Net setter 现状：setServer/setClient 改名走 setName(fill-if-blank)——已有名不覆盖，空白名补 default；setServers/setClients 以 map 键对空白名补键名")
    void netSetterRenameSemantics() {
        SpringBootNetBootstrapProperties properties = new SpringBootNetBootstrapProperties();

        SpringNettyNetServerBootstrapSetting server = new SpringNettyNetServerBootstrapSetting();
        server.setName("pre-set");
        assertSame(properties, properties.setServer(server), "链式返回子类自身不断链");
        assertSame(server, properties.getServer());
        assertEquals("pre-set", server.getName(),
                "现状：CommonNetBootstrapSetting.setName 仅当现名为空白才写入，setServer 不覆盖已有名（禁止顺手修成覆写语义）");

        SpringNettyNetClientBootstrapSetting client = new SpringNettyNetClientBootstrapSetting();
        client.setName("pre-set");
        assertSame(properties, properties.setClient(client));
        assertEquals("pre-set", client.getName(), "现状同上：fill-if-blank");

        SpringNettyNetServerBootstrapSetting blankServer = new SpringNettyNetServerBootstrapSetting();
        properties.setServer(blankServer);
        assertEquals("default", blankServer.getName(), "现状：空白名被补 default");
        SpringNettyNetClientBootstrapSetting blankClient = new SpringNettyNetClientBootstrapSetting();
        properties.setClient(blankClient);
        assertEquals("default", blankClient.getName());

        LinkedHashMap<String, SpringNettyNetServerBootstrapSetting> servers = new LinkedHashMap<>();
        SpringNettyNetServerBootstrapSetting sa = new SpringNettyNetServerBootstrapSetting();
        SpringNettyNetServerBootstrapSetting sb = new SpringNettyNetServerBootstrapSetting();
        servers.put("alpha", sa);
        servers.put("beta", sb);
        assertSame(properties, properties.setServers(servers));
        assertEquals("alpha", sa.getName(), "现状：setServers 以 map 键覆写各 setting 名");
        assertEquals("beta", sb.getName());
        assertSame(sa, properties.getServers().get("alpha"));

        LinkedHashMap<String, SpringNettyNetClientBootstrapSetting> clients = new LinkedHashMap<>();
        SpringNettyNetClientBootstrapSetting ca = new SpringNettyNetClientBootstrapSetting();
        clients.put("gamma", ca);
        assertSame(properties, properties.setClients(clients));
        assertEquals("gamma", ca.getName());
    }

    @Test
    @DisplayName("Rpc setter 现状：client 默认名为 rpc（与 Net 的 default 分裂），server 仍为 default")
    void rpcSetterRenameSemantics() {
        SpringBootRpcBootstrapProperties properties = new SpringBootRpcBootstrapProperties();

        SpringNettyRpcServerBootstrapSetting server = new SpringNettyRpcServerBootstrapSetting();
        assertSame(properties, properties.setServer(server));
        assertEquals("default", server.getName());

        SpringNettyRpcClientBootstrapSetting client = new SpringNettyRpcClientBootstrapSetting();
        assertSame(properties, properties.setClient(client));
        assertEquals("rpc", client.getName(), "现状：Rpc 空白名补 rpc（与 Net 的 default 分裂），不得被参数化抹平");
    }

    @Test
    @DisplayName("空值现状：setServer(null)/setClient(null) 置 null 不 NPE；setServers(null)/setClients(null) 忽略并保留现值")
    void nullToleranceSemantics() {
        SpringBootNetBootstrapProperties properties = new SpringBootNetBootstrapProperties();
        properties.setServer(null);
        properties.setClient(null);
        assertNull(properties.getServer());
        assertNull(properties.getClient());

        LinkedHashMap<String, SpringNettyNetServerBootstrapSetting> servers = new LinkedHashMap<>();
        SpringNettyNetServerBootstrapSetting sa = new SpringNettyNetServerBootstrapSetting();
        servers.put("alpha", sa);
        properties.setServers(servers);
        properties.setServers(null);
        assertEquals(Set.of("alpha"), properties.getServers().keySet(), "现状：null 传入被忽略，不冲空已绑集合");
        properties.setClients(null);
        assertTrue(properties.getClients().isEmpty());
    }

    // ---------- Binder 属性文件→对象现状 ----------

    @Test
    @DisplayName("Net Binder：嵌套 Setting 懒造入位、改名副作用与默认值保持按现状钉死")
    void netBinder() throws Exception {
        SpringBootNetBootstrapProperties properties = binderOf(
                NET_PREFIX + ".server.scheme=unix",
                NET_PREFIX + ".client.connector.retry-times=9",
                NET_PREFIX + ".servers.alpha.scheme=unix",
                NET_PREFIX + ".clients.beta.connector.retry-times=7"
        ).bind(NET_PREFIX, SpringBootNetBootstrapProperties.class)
                .orElseThrow(() -> new IllegalStateException("bind failed"));

        assertNotNull(properties.getServer());
        assertEquals("unix", properties.getServer().getScheme());
        assertNotNull(properties.getServer().getChannel(), "构造器默认 channel 现状（Spring 子类懒造 SpringNettyChannelSetting）");
        // 现状：绑定是否经 setServer（name=default）or 直填实例（name=null）——钉死观测值
        assertEquals("default", properties.getServer().getName(), "现状：绑定经 setServer 改名 default");

        assertNotNull(properties.getClient());
        assertEquals(9, properties.getClient().getConnector().getRetryTimes());
        assertEquals("default", properties.getClient().getName(), "现状：绑定经 setClient 改名 default");

        assertEquals(Set.of("alpha"), properties.getServers().keySet());
        assertEquals("unix", properties.getServers().get("alpha").getScheme());
        assertEquals("alpha", properties.getServers().get("alpha").getName(), "现状：绑定经 setServers 以键改名");

        assertEquals(Set.of("beta"), properties.getClients().keySet());
        assertEquals(7, properties.getClients().get("beta").getConnector().getRetryTimes());
        assertNotSame(properties.getClient(), properties.getClients().get("beta"));
        assertEquals("beta", properties.getClients().get("beta").getName());
    }

    @Test
    @DisplayName("Rpc Binder：同 Net 形态但 client 改名值为 rpc")
    void rpcBinder() throws Exception {
        SpringBootRpcBootstrapProperties properties = binderOf(
                RPC_PREFIX + ".server.scheme=unix",
                RPC_PREFIX + ".client.connector.retry-times=9",
                RPC_PREFIX + ".servers.alpha.scheme=unix",
                RPC_PREFIX + ".clients.beta.connector.retry-times=7"
        ).bind(RPC_PREFIX, SpringBootRpcBootstrapProperties.class)
                .orElseThrow(() -> new IllegalStateException("bind failed"));

        assertNotNull(properties.getServer());
        assertEquals("unix", properties.getServer().getScheme());
        assertEquals("default", properties.getServer().getName());

        assertNotNull(properties.getClient());
        assertEquals(9, properties.getClient().getConnector().getRetryTimes());
        assertEquals("rpc", properties.getClient().getName(), "现状：绑定路径同样吃到 rpc 改名");

        assertEquals(Set.of("alpha"), properties.getServers().keySet());
        assertEquals("alpha", properties.getServers().get("alpha").getName());
        assertEquals(Set.of("beta"), properties.getClients().keySet());
        assertEquals(7, properties.getClients().get("beta").getConnector().getRetryTimes());
        assertNotSame(properties.getClient(), properties.getClients().get("beta"));
        // 现状怪癖如实钉（观测实录）：Rpc 的 clients 复数条目最终名是单数缺省名 "rpc" 而非键名 "beta"
        // （Net 侧同形态却是键名 "beta"；差异源自绑定路径，禁止顺手修，登记遗留）
        assertEquals("rpc", properties.getClients().get("beta").getName(), "现状：Rpc 复数条目吃到单数改名值 rpc");
    }

    @Test
    @DisplayName("Rpc 协变 getter 现状：getServer/getClient 返回 Rpc 具体类型（链式协变覆写账）")
    void rpcCovariantGetters() {
        SpringBootRpcBootstrapProperties properties = new SpringBootRpcBootstrapProperties();
        SpringNettyRpcServerBootstrapSetting server = new SpringNettyRpcServerBootstrapSetting();
        SpringNettyRpcClientBootstrapSetting client = new SpringNettyRpcClientBootstrapSetting();
        SpringBootRpcBootstrapProperties chained = properties.setServer(server).setClient(client);
        assertSame(properties, chained);
        SpringNettyRpcServerBootstrapSetting gotServer = properties.getServer();
        SpringNettyRpcClientBootstrapSetting gotClient = properties.getClient();
        assertSame(server, gotServer);
        assertSame(client, gotClient);
    }

    @Test
    @DisplayName("Net 协变链现状：setServer(...).setClient(...).setServers(...).setClients(...) 不断链")
    void netChainedSettersKeepConcreteType() {
        SpringBootNetBootstrapProperties properties = new SpringBootNetBootstrapProperties();
        SpringBootNetBootstrapProperties chained = properties
                .setServer(new SpringNettyNetServerBootstrapSetting())
                .setClient(new SpringNettyNetClientBootstrapSetting())
                .setServers(new LinkedHashMap<>())
                .setClients(new LinkedHashMap<>());
        assertSame(properties, chained);
    }

}
