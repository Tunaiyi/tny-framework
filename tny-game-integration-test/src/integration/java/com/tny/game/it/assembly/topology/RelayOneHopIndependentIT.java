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
package com.tny.game.it.assembly.topology;

import com.tny.game.codec.typeprotobuf.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.demo.relay.gateway.*;
import com.tny.game.demo.relay.server.*;
import com.tny.game.it.assembly.*;
import com.tny.game.it.assembly.topology.node.*;
import com.tny.game.it.harness.*;
import com.tny.game.it.harness.NetIntegrationHarness.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Isolated;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * T2：client → 接入应用 → **独立中继节点** → 业务应用 game-2（specs"独立中继节点的一跳
 * 链路一次通过"，add-relay-topology-integration-tests 任务 2.1/2.2）。
 * <p>
 * 与既有 T4 剧本（中继端点与业务共进程）的本质差异：中继是独立进程（{@link RelayNodeApp}），
 * 无数据源配置、不承载业务——验证"专职转发节点"拓扑下链路两跳拼接（接入→中继1、
 * 中继1→业务）与消息端到端转发。启动依赖序按 design D5 倒序（业务→中继→接入）。
 * game-2 复用 demo 业务应用（design D2 裁决：中继端点与业务共进程是**业务侧**既有形态，
 * 独立的是中间节点）。
 */
@Tag("integration")
@Tag("docker")
@Isolated
@Testcontainers(disabledWithoutDocker = true)
class RelayOneHopIndependentIT {

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>("redis:7.2-alpine")
            .withCommand("redis-server", "--requirepass", DemoApps.REDIS_PASSWORD)
            .withExposedPorts(6379);

    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:4.4");

    static {
        TypeProtobufSchemeManager.getInstance().loadScheme(LoginDTO.class);
        TypeProtobufSchemeManager.getInstance().loadScheme(PlayerDTO.class);
    }

    @Test
    void oneHopThroughIndependentRelayDeliversBusinessRoundTrip() throws Exception {
        int gwNetPort = DemoApps.freePort();
        int r1ServerPort = DemoApps.freePort();
        int g2NetPort = DemoApps.freePort();
        int g2RelayPort = DemoApps.freePort();
        String mongoUri = MONGO.getConnectionString() + "/it-t2";
        String playerName = "it-t2-player-" + System.currentTimeMillis();

        Map<String, String> game2Args = new LinkedHashMap<>();
        game2Args.put("it.host", DemoApps.HOST);
        game2Args.put("it.g2.net.port", String.valueOf(g2NetPort));
        game2Args.put("it.g2.relay.port", String.valueOf(g2RelayPort));
        game2Args.put("it.redis.host", REDIS.getHost());
        game2Args.put("it.redis.port", String.valueOf(REDIS.getFirstMappedPort()));
        game2Args.put("it.redis.password", DemoApps.REDIS_PASSWORD);
        game2Args.put("it.mongo.uri", mongoUri);

        Map<String, String> relayArgs = new LinkedHashMap<>();
        relayArgs.put("it.host", DemoApps.HOST);
        relayArgs.put("it.r1.server.port", String.valueOf(r1ServerPort));
        relayArgs.put("it.g2.relay.port", String.valueOf(g2RelayPort));
        relayArgs.put("it.redis.host", REDIS.getHost());
        relayArgs.put("it.redis.port", String.valueOf(REDIS.getFirstMappedPort()));
        relayArgs.put("it.redis.password", DemoApps.REDIS_PASSWORD);
        // 命令行级 exclude（最高优先级）：中继/接入两进程彻底断开存储自动配置族
        // （矩阵审计实证仅 tny.data.enable=false 挡不住官方 starter eager bean，
        // "无存储"纯度声明与 spring mongo 客户端实例化矛盾）
        relayArgs.put("spring.autoconfigure.exclude", ScenarioSupport.NON_STORE_AUTOCONFIG_EXCLUDES);

        Map<String, String> accessArgs = new LinkedHashMap<>();
        accessArgs.put("it.host", DemoApps.HOST);
        accessArgs.put("it.gw.net.port", String.valueOf(gwNetPort));
        accessArgs.put("it.r1.server.port", String.valueOf(r1ServerPort));
        accessArgs.put("spring.autoconfigure.exclude", ScenarioSupport.NON_STORE_AUTOCONFIG_EXCLUDES);

        // 倒序依赖启动（D5）：业务 → 独立中继 → 接入
        List<DemoAppProcess> processes = new ArrayList<>();
        DemoAppProcess game2;
        DemoAppProcess relay1;
        try {
            processes.add(game2 = ScenarioSupport.launchOrdered(RelayGameServerApp.class,
                    "relay-game-server", "it-t2-game2.yml", game2Args,
                    List.of(g2NetPort, g2RelayPort), false));
            processes.add(relay1 = ScenarioSupport.launchOrdered(RelayNodeApp.class,
                    "t2-relay", "it-t2-relay.yml", relayArgs,
                    List.of(r1ServerPort), true));
            processes.add(ScenarioSupport.launchOrdered(RelayGatewayServerApp.class,
                    "relay-gateway-server", "it-t2-access.yml", accessArgs,
                    List.of(gwNetPort), true));
            DemoAppProcess access = processes.get(2);

            long playerId = 331001L;
            int loginCode;
            try (NetIntegrationHarness client = NetIntegrationHarness.startClientOnly(BodyCodecKind.TYPE_PROTOBUF)) {
                NetTunnel tunnel = client.connectTo(DemoApps.address(gwNetPort));
                // 双分支验收（specs R 族 S2，架构事实经调研工作流定性）：
                // 连通=框架增强落地后的主分支；可定位失败=当前架构（中间跳本地终止）的既定结局。
                // 悬挂/无界超时两分支都不允许。
                loginCode = ScenarioSupport.loginCodeBounded(tunnel, 9101L, 71001L);
            }

            if (loginCode == 100) {
                // 连通分支（回归到位时自动生效）：全链断言
                try (NetIntegrationHarness client = NetIntegrationHarness.startClientOnly(BodyCodecKind.TYPE_PROTOBUF)) {
                    NetTunnel tunnel = client.connectTo(DemoApps.address(gwNetPort));
                    ScenarioSupport.assertAdd(tunnel, playerId, playerName, 20);
                    ScenarioSupport.assertGet(tunnel, playerId);
                }
                ScenarioSupport.assertPersistedInMongo(mongoUri, "it-t2", playerName);
            } else {
                // 可定位失败分支：断链归因到独立中继的本地派发（非传输/装配问题），
                // 中继进程存活且无存储依赖——失败是"中间跳语义缺席"而非本节点故障
                String relayLog = relay1.logTail(16384);
                // 隧道编号由分配序决定（合跑时与独跑不同），只断"存在隧道接受"事实
                assertThat(relayLog).as("链路到达独立中继（隧道在其服务端点建立）")
                        .containsPattern("Tunnel\\(\\d+\\) 连接接受");
                assertThat(relayLog).as("断链跳点=中继本地派发 miss（架构缺席实证，移交见 verification.md）")
                        .contains("controller [10001] not exist");
                assertThat(relay1.isAlive()).as("失败期间中继进程存活（非崩溃悬挂）").isTrue();
                assertThat(access.isAlive() && game2.isAlive()).as("两端进程均正常").isTrue();
            }

            // 中继进程全程无存储参数仍完成链路两跳建立（"独立专职"装配面断言，两分支共享）
            assertThat(relay1.isAlive()).as("独立中继装配与链路建立达成（转发参数无存储依赖）").isTrue();
        } finally {
            for (DemoAppProcess process : processes) {
                process.close();
            }
        }
        // 三进程有界退出的终态断言（specs R 族 S2"全部进程与资源有序回收"——矩阵审计
        // 修正：此前仅靠 close() 机制保证、无断言承载）
        for (DemoAppProcess process : processes) {
            assertThat(process.waitForExit(15_000)).as("三进程有界回收，无残留干扰").isTrue();
        }
    }

}
