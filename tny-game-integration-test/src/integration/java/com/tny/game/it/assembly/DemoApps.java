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
package com.tny.game.it.assembly;

import com.tny.game.boot.launcher.*;
import com.tny.game.demo.net.server.*;
import com.tny.game.demo.relay.gateway.*;
import com.tny.game.demo.relay.server.*;
import com.tny.game.net.application.*;
import org.slf4j.*;
import org.springframework.boot.*;
import org.springframework.context.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * demo 应用装配编排（add-starter-net-integration-tests design D1/D2/D6）。
 * <p>
 * 复刻 demo 入口的启动序（launcher 注册 → {@code SpringApplication.run}）以保证被测装配与
 * 用户实际路径逐字节同源；环境相关键（监听端口、数据源裁剪）经<b>命令行参数</b>注入——demo 的
 * yml 位于 classpath 且优先级高于 defaultProperties，唯命令行可覆盖（design D2 实现注记）。
 * 上下文按启动序登记，{@link #close()} 逆序释放（Spring 销毁钩子驱动 {@code NetApplication.close}
 * 关闭全部引导器）。
 */
public final class DemoApps implements AutoCloseable {

    public static final String HOST = "127.0.0.1";

    /** demo application-server.yml 的 redis 默认口令（容器按此启动，实现零修改复用）。 */
    public static final String REDIS_PASSWORD = "tNbrH3NSErGd";

    private final List<ConfigurableApplicationContext> contexts = new ArrayList<>();

    private int networkPort;

    private int relayPort;

    public static int freePort() {
        try (ServerSocket socket = new ServerSocket(0, 1, InetAddress.getByName(HOST))) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static InetSocketAddress address(int port) {
        return new InetSocketAddress(HOST, port);
    }

    /**
     * 单应用完整装配（design D3 实施修订，用户批准：demo 应用对数据子系统为硬依赖——
     * PlayerController→EntityCacheManager，"无中间件档"不成立）：真实中间件地址注入，
     * redis 容器需以 {@link #REDIS_PASSWORD} 起 requirepass 以匹配 demo 既定配置零修改。
     * 网络/rpc 监听端口自动探测，起服后可经 {@link #networkPort()} 取用。
     */
    public static DemoApps gameServer(String redisHost, int redisPort, String mongoUri) {
        DemoApps apps = new DemoApps();
        int networkPort = freePort();
        int rpcPort = freePort();
        Map<String, String> overrides = gameDataOverrides(redisHost, redisPort, mongoUri);
        overrides.put("tny.net.bootstrap.network.server.bind-address", HOST + ":" + networkPort);
        overrides.put("tny.net.bootstrap.network.rpc.server.bind-address", HOST + ":" + rpcPort);
        apps.networkPort = networkPort;
        apps.launch(GameServerApp.class, "server", overrides);
        return apps;
    }

    /** demo 游戏服务端网络监听端口（{@link #gameServer} 起服后可用）。 */
    public int networkPort() {
        return networkPort;
    }

    /**
     * 中继业务侧（剧本）：net 监听 + relay server 监听 + 真实数据源。
     * 监听端口自动探测，起服后经 {@link #relayPort()} 取中继地址供接入侧注入。
     */
    public static DemoApps relayGame(String redisHost, int redisPort, String mongoUri) {
        DemoApps apps = new DemoApps();
        int networkPort = freePort();
        int relayPort = freePort();
        Map<String, String> overrides = gameDataOverrides(redisHost, redisPort, mongoUri);
        overrides.put("tny.net.bootstrap.network.server.bind-address", HOST + ":" + networkPort);
        overrides.put("tny.net.bootstrap.relay.server.bind-address", HOST + ":" + relayPort);
        apps.networkPort = networkPort;
        apps.relayPort = relayPort;
        apps.launch(RelayGameServerApp.class, "relay-game-server", overrides);
        return apps;
    }

    /**
     * game 侧数据源注入：{@code tny.datasource.*}（框架自有装配）之外，还必须注入
     * {@code spring.data.redis.*}——redisson-spring-boot-starter 的官方自动配置独立建
     * RedissonClient（默认打 localhost:6379 密码空），只改 tny 前缀会连不上容器。
     */
    private static Map<String, String> gameDataOverrides(String redisHost, int redisPort, String mongoUri) {
        Map<String, String> overrides = new LinkedHashMap<>();
        overrides.put("tny.datasource.redisson.setting.host", redisHost);
        overrides.put("tny.datasource.redisson.setting.port", String.valueOf(redisPort));
        overrides.put("spring.data.redis.host", redisHost);
        overrides.put("spring.data.redis.port", String.valueOf(redisPort));
        overrides.put("spring.data.redis.password", REDIS_PASSWORD);
        overrides.put("tny.datasource.mongodb.setting.uri", mongoUri);
        return overrides;
    }

    /**
     * 中继接入侧（剧本）：客户端监听 + 静态节点指向 game 中继端口（无注册中心，design D5）；
     * 网关 profile 自带 data.enable=false，无中间件依赖。
     */
    public static DemoApps relayGateway(String redisHost, int redisPort, String redisPassword, int gameRelayPort) {
        DemoApps apps = new DemoApps();
        int networkPort = freePort();
        Map<String, String> overrides = new LinkedHashMap<>();
        overrides.put("tny.net.bootstrap.network.server.bind-address", HOST + ":" + networkPort);
        // 同 JVM 双官方应用的静态扫描面（ApplicationLauncherContext/单元表）为 JVM 级，
        // 网关上下文会连带实例化 game 实体存储 bean——为免其连默认 localhost:6379，
        // 统一指向容器地址（实体 bean 惰性使用，网关不实际触达）
        overrides.put("spring.data.redis.host", redisHost);
        overrides.put("spring.data.redis.port", String.valueOf(redisPort));
        overrides.put("spring.data.redis.password", redisPassword);
        overrides.put("tny.datasource.redisson.setting.host", redisHost);
        overrides.put("tny.datasource.redisson.setting.port", String.valueOf(redisPort));
        overrides.put("tny.datasource.redisson.setting.password", redisPassword);
        // Spring 命令行索引属性整段替换 yml list——须把 cluster 条目全字段给齐（service 为中继路由键）
        overrides.put("tny.net.relay.clusters[0].service", "game-service");
        overrides.put("tny.net.relay.clusters[0].discovery", "false");
        overrides.put("tny.net.relay.clusters[0].connection-size", "1");
        overrides.put("tny.net.relay.clusters[0].serve-nodes[0].id", "1");
        overrides.put("tny.net.relay.clusters[0].serve-nodes[0].url", "tcp://" + HOST + ":" + gameRelayPort);
        apps.networkPort = networkPort;
        apps.launch(RelayGatewayServerApp.class, "relay-gateway-server", overrides);
        return apps;
    }

    /** 中继 server 监听端口（{@link #relayGame} 起服后可用）。 */
    public int relayPort() {
        return relayPort;
    }

    /** 装配产出的全部服务端引导器是否至少有一个在受理（官方装配的"起服可观测"锚点）。 */
    public boolean isNetworkServerBound() {
        for (ConfigurableApplicationContext context : contexts) {
            if (!context.isActive()) {
                continue;
            }
            for (ServerGuide guide : context.getBeansOfType(ServerGuide.class).values()) {
                if (guide.isBound()) {
                    return true;
                }
            }
        }
        return false;
    }

    public ConfigurableApplicationContext lastContext() {
        return contexts.get(contexts.size() - 1);
    }

    public <T> T bean(String beanName, Class<T> type) {
        return lastContext().getBean(beanName, type);
    }

    private ConfigurableApplicationContext launch(Class<?> appClass, String profile, Map<String, String> overrides) {
        assertLog4j2Binding();
        ApplicationLauncherContext.register(appClass);
        SpringApplication application = new SpringApplication(appClass);
        application.setWebApplicationType(WebApplicationType.NONE);
        List<String> args = new ArrayList<>();
        args.add("--spring.profiles.active=" + profile);
        overrides.forEach((key, value) -> args.add("--" + key + "=" + value));
        ConfigurableApplicationContext context = application.run(args.toArray(String[]::new));
        contexts.add(context);
        return context;
    }

    /** 装配前置体检：slf4j 绑定必须且仅为 log4j2（防依赖回潮导致日志/捕获静默失效）。 */
    private static void assertLog4j2Binding() {
        String binding = LoggerFactory.getILoggerFactory().getClass().getName();
        if (!binding.startsWith("org.apache.logging.slf4j.")) {
            throw new IllegalStateException("集成测试要求 log4j2 为唯一 slf4j 绑定，实际: " + binding);
        }
    }

    @Override
    public void close() {
        List<ConfigurableApplicationContext> reverse = new ArrayList<>(contexts);
        Collections.reverse(reverse);
        RuntimeException first = null;
        for (ConfigurableApplicationContext context : reverse) {
            try {
                context.close();
            } catch (RuntimeException e) {
                first = first == null ? e : first;
            }
        }
        contexts.clear();
        if (first != null) {
            throw first;
        }
    }

}
