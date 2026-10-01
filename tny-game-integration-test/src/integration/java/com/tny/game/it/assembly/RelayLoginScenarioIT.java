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

import com.mongodb.client.*;
import com.tny.game.codec.typeprotobuf.*;
import com.tny.game.common.result.*;
import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.demo.relay.gateway.*;
import com.tny.game.demo.relay.server.*;
import com.tny.game.it.assembly.topology.*;
import com.tny.game.it.harness.*;
import com.tny.game.it.harness.NetIntegrationHarness.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.bson.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

/**
 * 多应用主干业务剧本（specs"多应用装配验证主干业务场景" Scenario 1，
 * add-starter-net-integration-tests 任务 3.1-3.3）。
 * <p>
 * 拓扑（design D2 实施修订 + D5，用户批准子进程模型）：测试 JVM 内起两个官方 demo 应用
 * 子进程——业务侧 {@code RelayGameServerApp}（真实 Redis/Mongo）与接入侧
 * {@code RelayGatewayServerApp}（静态集群指向业务侧中继端口，无注册中心）；装配结构声明于
 * classpath 测试 yml（additional-location），运行时仅传端口与容器地址。客户端经 harness
 * client-only 连接入侧：登录 → 经中继 PLAYER$ADD 触发异步落库 → PLAYER$GET 回读 →
 * Mongo 容器直查持久化结果 → 全部资源有序释放。
 */
@Tag("integration")
@Isolated
@Tag("docker")
@Testcontainers(disabledWithoutDocker = true)
class RelayLoginScenarioIT {

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>("redis:7.2-alpine")
            .withCommand("redis-server", "--requirepass", DemoApps.REDIS_PASSWORD)
            .withExposedPorts(6379);

    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:4.4");

    static {
        // 测试 JVM（client-only harness）不走 boot 扫描链，需显式装载 demo DTO 的
        // TypeProtobuf scheme——否则回程 RESPONSE（LoginDTO/PlayerDTO 体）解码期
        // "TypeProtobufScheme is no exist" 弃包（八点位探针定位终果，见 verification.md）
        TypeProtobufSchemeManager.getInstance().loadScheme(LoginDTO.class);
        TypeProtobufSchemeManager.getInstance().loadScheme(PlayerDTO.class);
    }

    @Test
    void loginThroughGatewayRelayToGameAndPersisted() throws Exception {
        int gameNetworkPort = DemoApps.freePort();
        int gameRelayPort = DemoApps.freePort();
        int gatewayNetworkPort = DemoApps.freePort();
        String mongoUri = MONGO.getConnectionString() + "/it-scenario";
        String playerName = "it-player-" + System.currentTimeMillis();

        Map<String, String> gameArgs = new LinkedHashMap<>();
        gameArgs.put("it.host", DemoApps.HOST);
        gameArgs.put("it.game.network.port", String.valueOf(gameNetworkPort));
        gameArgs.put("it.game.relay.port", String.valueOf(gameRelayPort));
        gameArgs.put("it.redis.host", REDIS.getHost());
        gameArgs.put("it.redis.port", String.valueOf(REDIS.getFirstMappedPort()));
        gameArgs.put("it.redis.password", DemoApps.REDIS_PASSWORD);
        gameArgs.put("it.mongo.uri", mongoUri);

        Map<String, String> gatewayArgs = new LinkedHashMap<>();
        gatewayArgs.put("it.host", DemoApps.HOST);
        gatewayArgs.put("it.gateway.network.port", String.valueOf(gatewayNetworkPort));
        gatewayArgs.put("it.game.relay.port", String.valueOf(gameRelayPort));

        try (DemoAppProcess game = DemoAppProcess.launch(RelayGameServerApp.class,
                "relay-game-server", itConfig("it-relay-game.yml"), gameArgs)) {
            game.awaitListening(List.of(gameNetworkPort, gameRelayPort));
            // 启动序即 demo 生产序：业务侧中继端口先就绪，接入侧首拨才不被永久终结
            try (DemoAppProcess gateway = DemoAppProcess.launch(RelayGatewayServerApp.class,
                    "relay-gateway-server", itConfig("it-relay-gateway.yml"), gatewayArgs)) {
                gateway.awaitListening(List.of(gatewayNetworkPort));
                gateway.awaitRelayLink();

                try (NetIntegrationHarness client = NetIntegrationHarness.startClientOnly(BodyCodecKind.TYPE_PROTOBUF)) {
                    NetTunnel tunnel = client.connectTo(DemoApps.address(gatewayNetworkPort));

                    // 断言基座提取至 ScenarioSupport（任务 1.2 零行为变化；DECISION 打印留档于
                    // verification，行为断言语义不变）
                    ScenarioSupport.login(tunnel, 9003L, 70003L);
                    long playerId = 330001L;
                    ScenarioSupport.assertAdd(tunnel, playerId, playerName, 18);
                    ScenarioSupport.assertGet(tunnel, playerId);
                }

                ScenarioSupport.assertPersistedInMongo(mongoUri, "it-scenario", playerName);
            }
        }
    }

    private static java.nio.file.Path itConfig(String resourceName) {
        return java.nio.file.Path.of(java.util.Objects.requireNonNull(
                RelayLoginScenarioIT.class.getResource("/" + resourceName), "缺少测试装配文件 " + resourceName).getPath());
    }

    private static MessageSent request(NetTunnel tunnel, int protocolId, Object... params) {
        return tunnel.send(MessageContents.request(Protocols.protocol(protocolId), params).willRespondFuture(10_000));
    }

}
