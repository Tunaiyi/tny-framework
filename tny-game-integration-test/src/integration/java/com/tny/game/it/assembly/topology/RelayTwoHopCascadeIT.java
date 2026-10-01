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
 * T3：client → 接入 → 独立中继 relay-1 → 独立中继 relay-2 → 业务 game-2 的两跳级联
 * （specs R 族需求"独立中继作为中间跳"MUST 完成装配与链路建立验证 + design D5"级联=
 * 每级同形应用、yml 参数不同"，add-relay-topology-integration-tests 任务 2.2 T3 级联段——
 * 审计 CRITICAL"账实不符"经用户裁决补齐实体）。
 * <p>
 * 双分支验收同 T2（架构缺席 D6 在级联下照传的实证）：三段链路建立全部可观测
 * （launchOrdered 出站锚点 + 两中继隧道接受日志）；业务调用预期在**首跳 relay-1 本地终止**
 * ——断链跳点定位断言 = relay-1 出现派发 miss 且 relay-2 **无**该 miss（流量根本没走到
 * 第二跳，证明缺席是每跳本地终止语义而非级联装配缺陷）；框架中转增强
 * （add-relay-transit-hop）落地后连通分支自动激活。四进程有界退出终态断言。
 */
@Tag("integration")
@Tag("docker")
@Isolated
@Testcontainers(disabledWithoutDocker = true)
class RelayTwoHopCascadeIT {

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
    void twoHopCascadeAssemblyVerifiableAndBreakLocalizableAtFirstRelayHop() throws Exception {
        int gwNetPort = DemoApps.freePort();
        int r1ServerPort = DemoApps.freePort();
        int r2ServerPort = DemoApps.freePort();
        int g2NetPort = DemoApps.freePort();
        int g2RelayPort = DemoApps.freePort();
        String mongoUri = MONGO.getConnectionString() + "/it-t3";
        String playerName = "it-t3-player-" + System.currentTimeMillis();

        Map<String, String> game2Args = redisArgs(new LinkedHashMap<>());
        game2Args.put("it.host", DemoApps.HOST);
        game2Args.put("it.g2.net.port", String.valueOf(g2NetPort));
        game2Args.put("it.g2.relay.port", String.valueOf(g2RelayPort));
        game2Args.put("it.mongo.uri", mongoUri);

        Map<String, String> relay1Args = redisArgs(new LinkedHashMap<>());
        relay1Args.put("it.host", DemoApps.HOST);
        relay1Args.put("it.r1.server.port", String.valueOf(r1ServerPort));
        relay1Args.put("it.r2.server.port", String.valueOf(r2ServerPort));
        noStore(relay1Args);

        Map<String, String> relay2Args = redisArgs(new LinkedHashMap<>());
        relay2Args.put("it.host", DemoApps.HOST);
        relay2Args.put("it.r2.server.port", String.valueOf(r2ServerPort));
        relay2Args.put("it.g2.relay.port", String.valueOf(g2RelayPort));
        noStore(relay2Args);

        Map<String, String> accessArgs = new LinkedHashMap<>();
        accessArgs.put("it.host", DemoApps.HOST);
        accessArgs.put("it.gw.net.port", String.valueOf(gwNetPort));
        accessArgs.put("it.r1.server.port", String.valueOf(r1ServerPort));
        noStore(accessArgs);

        // 倒序依赖启动（D5）：业务 → relay-2 → relay-1 → 接入（每一级出站前下游已就绪）
        List<DemoAppProcess> processes = new ArrayList<>();
        int loginCode;
        DemoAppProcess relay1;
        DemoAppProcess relay2;
        try {
            processes.add(ScenarioSupport.launchOrdered(RelayGameServerApp.class,
                    "relay-game-server", "it-t3-game2.yml", game2Args,
                    List.of(g2NetPort, g2RelayPort), false));
            processes.add(relay2 = ScenarioSupport.launchOrdered(RelayNodeApp.class,
                    "t3-relay-two", "it-t3-relay2.yml", relay2Args,
                    List.of(r2ServerPort), true));
            processes.add(relay1 = ScenarioSupport.launchOrdered(RelayNodeApp.class,
                    "t3-relay-one", "it-t3-relay1.yml", relay1Args,
                    List.of(r1ServerPort), true));
            processes.add(ScenarioSupport.launchOrdered(RelayGatewayServerApp.class,
                    "relay-gateway-server", "it-t3-access.yml", accessArgs,
                    List.of(gwNetPort), true));

            try (NetIntegrationHarness client = NetIntegrationHarness.startClientOnly(BodyCodecKind.TYPE_PROTOBUF)) {
                NetTunnel tunnel = client.connectTo(DemoApps.address(gwNetPort));
                // 双分支验收（同 T2 形态）：连通=框架增强落地后的主分支；
                // 可定位失败=中间跳本地终止语义在级联下照传的既定结局。悬挂两分支都不允许。
                loginCode = ScenarioSupport.loginCodeBounded(tunnel, 9301L, 73001L);
            }

            if (loginCode == 100) {
                // 连通分支（add-relay-transit-hop 落地后自动生效）：全链断言
                try (NetIntegrationHarness client = NetIntegrationHarness.startClientOnly(BodyCodecKind.TYPE_PROTOBUF)) {
                    NetTunnel tunnel = client.connectTo(DemoApps.address(gwNetPort));
                    ScenarioSupport.assertAdd(tunnel, 333001L, playerName, 20);
                    ScenarioSupport.assertGet(tunnel, 333001L);
                }
                ScenarioSupport.assertPersistedInMongo(mongoUri, "it-t3", playerName);
            } else {
                // 归因分支：三段链路建立可观测 + 断链固定于**首跳** relay-1 本地派发，
                // relay-2 派发面干净（流量未达第二跳——级联装配本身无缺陷的判别证据）。
                // 日志锚点分层（T3 首跑实证）：接入隧道层=「Tunnel(N) 连接接受」（仅 relay-1
                // 对 access），节点间 serve 链路层=「RelayLink(SERVER)」接受行
                assertThat(relay1.logTail(Integer.MAX_VALUE))
                        .as("第一段：接入→relay-1 玩家隧道建立可观测")
                        .containsPattern("Tunnel\\(\\d+\\) 连接接受");
                assertThat(relay2.logTail(Integer.MAX_VALUE))
                        .as("第二段：relay-1→relay-2 节点间链路建立可观测（服务端接受）")
                        .contains("RelayLink(SERVER)");
                assertThat(processes.get(0).logTail(Integer.MAX_VALUE))
                        .as("第三段：relay-2→game-2 节点间链路建立可观测（launchOrdered 已验 relay-2 出站）")
                        .contains("RelayLink(SERVER)");
                String r1 = relay1.logTail(Integer.MAX_VALUE);
                String r2 = relay2.logTail(Integer.MAX_VALUE);
                assertThat(r1).as("断链跳点=首跳 relay-1 本地派发 miss（D6 缺席在级联下照传）")
                        .contains("controller [10001] not exist");
                assertThat(r2).as("relay-2 从未收到业务流量——缺席语义在每跳本地终止、非级联装配缺陷")
                        .doesNotContain("controller [10001] not exist");
            }

            // 两分支共享：四进程全程存活（可定位失败≠崩溃悬挂）
            assertThat(processes.stream().allMatch(DemoAppProcess::isAlive))
                    .as("级联四进程失败/成功均不崩溃悬挂").isTrue();
        } finally {
            for (DemoAppProcess process : processes) {
                process.close();
            }
        }
        // 终态回收断言（specs R 族 S2"全部进程与资源有序回收"）
        for (DemoAppProcess process : processes) {
            assertThat(process.waitForExit(15_000)).as("级联四进程有界回收，无残留干扰").isTrue();
        }
    }

    private static Map<String, String> redisArgs(Map<String, String> args) {
        args.put("it.redis.host", REDIS.getHost());
        args.put("it.redis.port", String.valueOf(REDIS.getFirstMappedPort()));
        args.put("it.redis.password", DemoApps.REDIS_PASSWORD);
        return args;
    }

    private static void noStore(Map<String, String> args) {
        args.put("spring.autoconfigure.exclude", ScenarioSupport.NON_STORE_AUTOCONFIG_EXCLUDES);
    }

}
