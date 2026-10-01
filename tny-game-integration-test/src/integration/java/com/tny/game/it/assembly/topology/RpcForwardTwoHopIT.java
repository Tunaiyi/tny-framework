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
import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.demo.relay.server.*;
import com.tny.game.it.assembly.*;
import com.tny.game.it.assembly.topology.caller.*;
import com.tny.game.it.assembly.topology.forward.*;
import com.tny.game.it.harness.*;
import com.tny.game.it.harness.NetIntegrationHarness.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Isolated;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

/**
 * F2：client → caller → 转发应用×2 级联 → game-2 两跳 RPC 转发链
 * （specs"两跳级联转发连通或失败可定位归因"，add-relay-topology-integration-tests 任务 3.3）。
 * <p>
 * 实证目标（design D7"两跳 to 重写语义未实证"）：{@code RpcForwardCommand} 每跳把报文头
 * {@code to} 重写为下一跳自身接入点——跳二的 {@code tryForward} 以 to==currentType 判本地终止。
 * 双分支验收：连通=主分支（框架两跳语义落地后自动激活，不改测试）；不连通=断链跳点
 * 可定位（跳一"无 game-service 可用会话"或跳二"controller [20007] not exist"本地派发），
 * 且终端进程全程未被触达派发面（排除装配噪声），失败即移交"两跳转发语义"记录。
 * <p>
 * 首轮实证（已入 verification.md）：断链固定在**跳二本地终止**，且错误回程在跨跳改写中
 * 被咽成"code=100+空载荷"假成功——回程码/载荷跨跳保持属同族缺口，一并登记移交。
 */
@Tag("integration")
@Tag("docker")
@Isolated
@Testcontainers(disabledWithoutDocker = true)
class RpcForwardTwoHopIT {

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>("redis:7.2-alpine")
            .withCommand("redis-server", "--requirepass", DemoApps.REDIS_PASSWORD)
            .withExposedPorts(6379);

    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:4.4");

    static {
        TypeProtobufSchemeManager.getInstance().loadScheme(SayContentDTO.class);
    }

    @Test
    void twoHopRpcForwardChainIsGreenOrAttributableToExactlyOneHop() throws Exception {
        int cNetPort = DemoApps.freePort();
        int f1RpcPort = DemoApps.freePort();
        int f2RpcPort = DemoApps.freePort();
        int g2NetPort = DemoApps.freePort();
        int g2RelayPort = DemoApps.freePort();
        int g2RpcPort = DemoApps.freePort();
        String tag = "t2-" + System.currentTimeMillis();

        Map<String, String> game2Args = new LinkedHashMap<>();
        game2Args.put("it.host", DemoApps.HOST);
        game2Args.put("it.g2.net.port", String.valueOf(g2NetPort));
        game2Args.put("it.g2.relay.port", String.valueOf(g2RelayPort));
        game2Args.put("it.g2.rpc.port", String.valueOf(g2RpcPort));
        game2Args.put("it.mongo.uri", MONGO.getConnectionString() + "/it-f2");
        redisArgs(game2Args);

        // 倒序依赖启动（D5）：终端 → 跳二 → 跳一 → 调用方（拨出方永远晚于受拨方）
        Map<String, String> forward2Args = new LinkedHashMap<>();
        forward2Args.put("it.host", DemoApps.HOST);
        forward2Args.put("it.f2.rpc.port", String.valueOf(f2RpcPort));
        forward2Args.put("it.g2.rpc.port", String.valueOf(g2RpcPort));
        redisArgs(forward2Args);

        Map<String, String> forward1Args = new LinkedHashMap<>();
        forward1Args.put("it.host", DemoApps.HOST);
        forward1Args.put("it.f1.rpc.port", String.valueOf(f1RpcPort));
        forward1Args.put("it.f2.rpc.port", String.valueOf(f2RpcPort));
        redisArgs(forward1Args);

        Map<String, String> callerArgs = new LinkedHashMap<>();
        callerArgs.put("it.host", DemoApps.HOST);
        callerArgs.put("it.c.net.port", String.valueOf(cNetPort));
        callerArgs.put("it.f1.rpc.port", String.valueOf(f1RpcPort));
        redisArgs(callerArgs);
        callerArgs.put("spring.autoconfigure.exclude", ScenarioSupport.NON_STORE_AUTOCONFIG_EXCLUDES);
        forward1Args.put("spring.autoconfigure.exclude", ScenarioSupport.NON_STORE_AUTOCONFIG_EXCLUDES);
        forward2Args.put("spring.autoconfigure.exclude", ScenarioSupport.NON_STORE_AUTOCONFIG_EXCLUDES);

        List<DemoAppProcess> processes = new ArrayList<>();
        Message ack;
        try {
            processes.add(ScenarioSupport.launchOrdered(RelayGameServerApp.class,
                    "relay-game-server", "it-f2-game2.yml", game2Args,
                    List.of(g2NetPort, g2RelayPort, g2RpcPort), false));
            processes.add(ScenarioSupport.launchOrdered(ForwardApp.class,
                    "t2-forward2", "it-f2-forward2.yml", forward2Args,
                    List.of(f2RpcPort), false));
            processes.add(ScenarioSupport.launchOrdered(ForwardApp.class,
                    "t2-forward1", "it-f2-forward1.yml", forward1Args,
                    List.of(f1RpcPort), false));
            processes.add(ScenarioSupport.launchOrdered(CallerApp.class,
                    "t2-caller", "it-f2-caller.yml", callerArgs,
                    List.of(cNetPort), false));
            DemoAppProcess game2 = processes.get(0);
            DemoAppProcess forward2 = processes.get(1);
            DemoAppProcess forward1 = processes.get(2);
            DemoAppProcess caller = processes.get(3);

            // 拨号竞态防线（任务 3.3 实证）：caller→跳一认证完成前触发会被代理咽掉
            ScenarioSupport.awaitRpcDialAuthenticated(caller);

            try (NetIntegrationHarness client = NetIntegrationHarness.startClientOnly(BodyCodecKind.TYPE_PROTOBUF)) {
                NetTunnel tunnel = client.connectTo(DemoApps.address(cNetPort));
                ack = tunnel.send(MessageContents
                        .request(Protocols.protocol(ForwardTriggerController.TRIGGER), tag)
                        .willRespondFuture(15_000)).respond().get(15, TimeUnit.SECONDS);
            }

            // 连通判别以**终端载荷**为准而非回执码：首轮实证跳二失败的错误回程被逐跳
            // 改写咽成"code=100 + 空载荷"假成功——载荷缺失本身就是断链证据（移交记录）。
            SayContentDTO body = ack.bodyAs(SayContentDTO.class);
            if (ack.getCode() == 100 && body != null && body.getMessage() != null) {
                // 连通主分支：终端到达标记逐字验证（两跳语义落地后自动生效）
                assertThat(body.getMessage()).as("终端回执经两跳转发链原路返回").isEqualTo("respond fwd-" + tag);
            } else {
                // 失败归因分支：断链跳点二选一定位；终端派发面必须干净（排除装配噪声）
                String hop1Miss = grep(forward1, "未找到可用");
                String hop2Miss = grep(forward2, "controller [" + CtrlerIds.SPEAK$SAY_FOR_RPC + "] not exist");
                if (ack.getCode() == 100) {
                    System.out.println("F2-FAKE-SUCCESS: 回执码 100 但载荷为空——失败回程被咽掉，"
                            + "属两跳回程语义缺口（与 D7 to 重写同族，移交记录）");
                }
                assertThat(hop1Miss + hop2Miss)
                        .as("两跳失败必须可定位到具体跳点（跳一 game-service 会话解析 miss 或跳二本地派发 miss）")
                        .isNotEmpty();
                assertThat(game2.logTail(Integer.MAX_VALUE))
                        .as("断链在转发链而非终端（终端有该协议派发面）")
                        .doesNotContain("controller [" + CtrlerIds.SPEAK$SAY_FOR_RPC + "] not exist");
                System.out.println("F2-FAILURE-DIAGNOSIS code=" + ack.getCode()
                        + "\n  hop1-miss: " + hop1Miss + "\n  hop2-miss: " + hop2Miss
                        + "\n  caller: " + grep(caller, "Exception"));
            }

            // 两分支共享：级联四进程全程存活（可定位失败≠崩溃悬挂）
            assertThat(processes.stream().allMatch(DemoAppProcess::isAlive))
                    .as("级联四进程失败/成功均不崩溃悬挂").isTrue();
        } finally {
            for (DemoAppProcess process : processes) {
                process.close();
            }
        }
        // 完整资源回收：全部子进程有界退出，无残留干扰（specs 互不串扰前提）
        for (DemoAppProcess process : processes) {
            assertThat(process.waitForExit(15_000)).as("级联进程有界回收").isTrue();
        }
    }

    private static void redisArgs(Map<String, String> args) {
        args.put("it.redis.host", REDIS.getHost());
        args.put("it.redis.port", String.valueOf(REDIS.getFirstMappedPort()));
        args.put("it.redis.password", DemoApps.REDIS_PASSWORD);
    }

    private static String grep(DemoAppProcess process, String needle) {
        List<String> hits = new ArrayList<>();
        for (String line : process.logTail(Integer.MAX_VALUE).split("\n")) {
            if (line.contains(needle)) {
                hits.add(line.length() > 180 ? line.substring(0, 180) : line);
            }
        }
        return String.join(" ;; ", hits);
    }

}
