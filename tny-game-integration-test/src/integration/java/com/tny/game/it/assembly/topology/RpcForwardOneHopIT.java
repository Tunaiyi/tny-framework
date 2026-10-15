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
package com.tny.game.it.assembly.topology;

import com.tny.game.codec.typeprotobuf.*;
import com.tny.game.common.result.*;
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
import static org.awaitility.Awaitility.*;

/**
 * F1：client → caller → 独立转发应用 → game-2 一跳 RPC 转发链（specs"一跳转发链一次通过"，
 * add-relay-topology-integration-tests 任务 3.2）。
 * 类名按任务书 3.2/4.1 声明命名（`*RpcForward*` 过滤器命中 F 族）；本类另含任务 3.4
 * "同型身份歧义隔离"对照对：同型缺失变体证明首跳会转回 caller 自身，异型正例证明
 * D7 身份隔离手段有效。
 * <p>
 * 转发报文走 {@code bootstrap.rpc} TCP 面（与 relay.clusters 平行，relay 链永不进转发节点集，
 * design D7）；终端 SPEAK$SAY_FOR_RPC 返回 "respond fwd-&lt;tag&gt;"，消息内容即全链到达证明。
 * 双分支验收（design D4 更新：F1 以连通为主、归因结案为兜底）：连通判别以**终端载荷在场**
 * 为准而非仅回执码——F2 实证框架会把断链错误回程咽成"code=100+空载荷"假成功，同型
 * 风险对此类一跳同样存在；兜底分支按进程日志做真实归因（非恒真断言，矩阵审计修正）。
 */
@Tag("integration")
@Tag("docker")
@Isolated
@Testcontainers(disabledWithoutDocker = true)
class RpcForwardOneHopIT {

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
    void oneHopRpcForwardReachesTerminalAndReturns() throws Exception {
        int cNetPort = DemoApps.freePort();
        int fRpcPort = DemoApps.freePort();
        int g2NetPort = DemoApps.freePort();
        int g2RelayPort = DemoApps.freePort();
        int g2RpcPort = DemoApps.freePort();
        String tag = "t1-" + System.currentTimeMillis();

        Map<String, String> game2Args = baseArgs(g2NetPort, g2RelayPort);
        game2Args.put("it.g2.rpc.port", String.valueOf(g2RpcPort));
        game2Args.put("it.mongo.uri", MONGO.getConnectionString() + "/it-f1");

        Map<String, String> forwardArgs = hostArgs();
        forwardArgs.put("it.f.rpc.port", String.valueOf(fRpcPort));
        forwardArgs.put("it.g2.rpc.port", String.valueOf(g2RpcPort));
        redisArgs(forwardArgs);
        excludeDataAutoconfig(forwardArgs);

        Map<String, String> callerArgs = hostArgs();
        callerArgs.put("it.c.net.port", String.valueOf(cNetPort));
        callerArgs.put("it.f.rpc.port", String.valueOf(fRpcPort));
        redisArgs(callerArgs);
        excludeDataAutoconfig(callerArgs);

        List<DemoAppProcess> processes = new ArrayList<>();
        Message ack;
        try {
            processes.add(ScenarioSupport.launchOrdered(RelayGameServerApp.class,
                    "relay-game-server", "it-f1-game2.yml", game2Args,
                    List.of(g2NetPort, g2RelayPort, g2RpcPort), false));
            processes.add(ScenarioSupport.launchOrdered(ForwardApp.class,
                    "t1-forward", "it-f1-forward.yml", forwardArgs,
                    List.of(fRpcPort), false));
            processes.add(ScenarioSupport.launchOrdered(CallerApp.class,
                    "t1-caller", "it-f1-caller.yml", callerArgs,
                    List.of(cNetPort), false));
            DemoAppProcess game2 = processes.get(0);
            DemoAppProcess forward = processes.get(1);
            DemoAppProcess caller = processes.get(2);

            // 拨号竞态防线（任务 3.3 实证教训）：caller→forward 认证完成前触发会被
            // 调用方代理咽掉，转发面从未被 exercised
            ScenarioSupport.awaitRpcDialAuthenticated(caller);
            try (NetIntegrationHarness client = NetIntegrationHarness.startClientOnly(BodyCodecKind.TYPE_PROTOBUF)) {
                NetTunnel tunnel = client.connectTo(DemoApps.address(cNetPort));
                ack = tunnel.send(MessageContents
                        .request(Protocols.protocol(ForwardTriggerController.TRIGGER), tag)
                        .willRespondFuture(15_000)).respond().get(15, TimeUnit.SECONDS);
            }

            SayContentDTO body = ack.bodyAs(SayContentDTO.class);
            if (ack.getCode() == ResultCode.SUCCESS_CODE && body != null && body.getMessage() != null) {
                // 连通主分支：显式结果码 + 终端到达标记逐字验证（specs THEN"结果码与内容正确"）
                assertThat(ack.getCode()).as("端到端回执成功码").isEqualTo(ResultCode.SUCCESS_CODE);
                assertThat(body.getMessage()).as("终端回执经转发链原路返回").isEqualTo("respond fwd-" + tag);
            } else {
                // 归因兜底分支（design D4 更新授权）：断链环节必须在某进程日志可定位——
                // 三处候选派发面/解析面逐一 grep，全空即不可定位=红（矩阵审计修正前的恒真断言作废）
                String diagnosis = "caller:" + brief(caller) + " forward:" + brief(forward)
                        + " game2:" + brief(game2);
                assertThat(diagnosis)
                        .as("一跳失败必须可定位归因（caller 拨号面 / forward 解析派发面 / game-2 执行面至少一处日志证据）")
                        .containsAnyOf("not exist", "未找到可用", "Exception", "无可用", "connect failed");
                System.out.println("F1-FAILURE-DIAGNOSIS code=" + ack.getCode() + "\n" + diagnosis);
            }

            // 两分支共享：三进程全程存活（可定位失败≠崩溃悬挂）
            assertThat(processes.stream().allMatch(DemoAppProcess::isAlive))
                    .as("三进程失败/成功均不崩溃悬挂").isTrue();
        } finally {
            for (DemoAppProcess process : processes) {
                process.close();
            }
        }
        assertTerminated(processes);
    }

    /**
     * 同型歧义缺失变体（任务 3.4 对照 A）：caller 以终端**同型** username=game-service
     * 拨入转发节点，且本对照无终端——转发节点 game-service 会话集里只剩 caller 自己的
     * 接入会话，FirstRpcForwarderStrategy 首跳即选中它：请求被转回 caller 自身，其派发面
     * 收到终端协议（SPEAK 20007）即"转回自身"的行为证据（D7 异型隔离手段的必要性实证）。
     */
    @Test
    void sameTypeIdentityWithoutIsolationLoopsFirstHopBackToCaller() throws Exception {
        ControlRun run = launchControlPair("it-f1-caller-ambiguous.yml");
        try {
            run.trigger("t1-amb-" + System.currentTimeMillis());
            System.out.println("CONTROL-A code=" + run.code + " caller-miss-evidence="
                    + run.log(run.caller).contains("controller [" + CtrlerIds.SPEAK$SAY_FOR_RPC + "]")
                    + " forward-tail=" + brief(run.forward));
            await().atMost(java.time.Duration.ofSeconds(10)).untilAsserted(() ->
                    assertThat(run.log(run.caller)).as("断链证据=请求被转回 caller 自身（其 rpc 会话收到终端协议）")
                            .contains("controller [" + CtrlerIds.SPEAK$SAY_FOR_RPC + "] not exist"));
        } finally {
            run.close();
        }
        assertTerminated(List.of(run.forward, run.caller));
    }

    /**
     * 异型隔离正例（任务 3.4 对照 B）：caller 以**异型** username=game-client 拨入、
     * 同样无终端——game-service 会话集为空，失败定位在转发节点本地（"未找到可用"），
     * caller 自身派发面绝不收到终端协议。与对照 A 合起来即"同型歧义有隔离手段"的
     * 断言证据（specs F 族 S3）。
     */
    @Test
    void distinctTypeIdentityKeepsCallerOutOfForwardSet() throws Exception {
        ControlRun run = launchControlPair("it-f1-caller.yml");
        try {
            run.trigger("t1-iso-" + System.currentTimeMillis());
            System.out.println("CONTROL-B code=" + run.code + " forward-tail=" + brief(run.forward));
            // 核心隔离断言：异型身份下 caller 自身绝不进入 game-service 候选集（对照 A 的
            // 转回自身不成立）——失败定位应在转发节点（无可用 game-service 会话）
            assertThat(run.log(run.caller)).as("异型隔离有效：caller 自身派发面从未收到终端协议")
                    .doesNotContain("controller [" + CtrlerIds.SPEAK$SAY_FOR_RPC + "] not exist");
        } finally {
            run.close();
        }
        assertTerminated(List.of(run.forward, run.caller));
    }

    /** 对照进程组（无终端的 caller+forward 两进程；forward 的终端端点指向未绑定端口）。 */
    private static ControlRun launchControlPair(String callerYml) throws Exception {
        int cNetPort = DemoApps.freePort();
        int fRpcPort = DemoApps.freePort();
        int absentTerminalPort = DemoApps.freePort();

        Map<String, String> forwardArgs = hostArgs();
        forwardArgs.put("it.f.rpc.port", String.valueOf(fRpcPort));
        forwardArgs.put("it.g2.rpc.port", String.valueOf(absentTerminalPort));
        redisArgs(forwardArgs);
        excludeDataAutoconfig(forwardArgs);

        Map<String, String> callerArgs = hostArgs();
        callerArgs.put("it.c.net.port", String.valueOf(cNetPort));
        callerArgs.put("it.f.rpc.port", String.valueOf(fRpcPort));
        redisArgs(callerArgs);
        excludeDataAutoconfig(callerArgs);

        DemoAppProcess forward = ScenarioSupport.launchOrdered(ForwardApp.class, "t1-forward",
                "it-f1-forward.yml", forwardArgs, List.of(fRpcPort), false);
        DemoAppProcess caller;
        try {
            caller = ScenarioSupport.launchOrdered(CallerApp.class, "t1-caller", callerYml,
                    callerArgs, List.of(cNetPort), false);
        } catch (Exception | Error e) {
            forward.close();
            throw e;
        }
        try {
            // 拨号竞态防线：caller→forward 认证完成后再放行触发（对照两例共用）
            ScenarioSupport.awaitRpcDialAuthenticated(caller);
        } catch (Exception | Error e) {
            caller.close();
            forward.close();
            throw e;
        }
        return new ControlRun(cNetPort, forward, caller);
    }

    /** 全部子进程有界退出断言（specs"进程组完整回收，无残留干扰"终态担保）。 */
    private static void assertTerminated(List<DemoAppProcess> processes) throws InterruptedException {
        for (DemoAppProcess process : processes) {
            assertThat(process.waitForExit(15_000)).as("子进程有界回收，无残留干扰").isTrue();
        }
    }

    private static Map<String, String> baseArgs(int netPort, int relayPort) {
        Map<String, String> args = hostArgs();
        args.put("it.g2.net.port", String.valueOf(netPort));
        args.put("it.g2.relay.port", String.valueOf(relayPort));
        redisArgs(args);
        return args;
    }

    private static Map<String, String> hostArgs() {
        Map<String, String> args = new LinkedHashMap<>();
        args.put("it.host", DemoApps.HOST);
        return args;
    }

    private static void redisArgs(Map<String, String> args) {
        args.put("it.redis.host", REDIS.getHost());
        args.put("it.redis.port", String.valueOf(REDIS.getFirstMappedPort()));
        args.put("it.redis.password", DemoApps.REDIS_PASSWORD);
    }

    /** 非存储进程统一命令行排除（最高优先级，yml exclude 合并层级不可靠——T2 实证）。 */
    private static void excludeDataAutoconfig(Map<String, String> args) {
        args.put("spring.autoconfigure.exclude", ScenarioSupport.NON_STORE_AUTOCONFIG_EXCLUDES);
    }

    private static final class ControlRun implements AutoCloseable {

        final int clientPort;

        final DemoAppProcess forward;

        final DemoAppProcess caller;

        int code = -1;

        ControlRun(int clientPort, DemoAppProcess forward, DemoAppProcess caller) {
            this.clientPort = clientPort;
            this.forward = forward;
            this.caller = caller;
        }

        /** 有界触发：错误回执取回执码，悬挂/超时记 -1（两形态都不允许绕过断链证据）。 */
        void trigger(String tag) {
            try (NetIntegrationHarness client = NetIntegrationHarness.startClientOnly(BodyCodecKind.TYPE_PROTOBUF)) {
                NetTunnel tunnel = client.connectTo(DemoApps.address(clientPort));
                Message ack = tunnel.send(MessageContents
                        .request(Protocols.protocol(ForwardTriggerController.TRIGGER), tag)
                        .willRespondFuture(15_000)).respond().get(15, TimeUnit.SECONDS);
                code = ack.getCode();
            } catch (Exception e) {
                code = -1;
            }
        }

        String log(DemoAppProcess process) {
            return process.logTail(Integer.MAX_VALUE);
        }

        @Override
        public void close() {
            caller.close();
            forward.close();
        }

    }

    private static String brief(DemoAppProcess process) {
        String tail = process.logTail(6000);
        List<String> hits = new ArrayList<>();
        for (String line : tail.split("\n")) {
            if (line.contains("not exist") || line.contains("转发") || line.contains("forward")
                    || line.contains("Exception") || line.contains("无可用")) {
                hits.add(line.length() > 180 ? line.substring(0, 180) : line);
            }
        }
        return String.join(" ;; ", hits);
    }

}
