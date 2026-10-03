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
package com.tny.game.it.assembly;

import com.tny.game.common.result.*;
import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.it.harness.*;
import com.tny.game.it.harness.NetIntegrationHarness.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

/**
 * 官方装配形态的游戏服务端端到端验证（specs"应用装配链端到端可验证"，
 * add-starter-net-integration-tests 任务 2.1/2.3）。
 * <p>
 * 装配蓝本：{@code GameServerApp}（demo 模块，零修改）经 {@link DemoApps} 以
 * {@code SpringApplication} 启动，profile=server；中间件地址经命令行属性注入容器
 * （design D3 实施修订：demo 对数据子系统为硬依赖）。客户端复用 harness client-only 装配
 * （服务端装配面才是本变更被测对象）。
 * <p>
 * 类级共享单一服务端上下文：Spring 装配的 @Unit bean 注册进 JVM 级静态 UnitLoader 表，
 * 同 JVM 起第二个 demo 上下文会撞注册键（实施发现，见 verification 记录）；用例间以
 * 独立连接与不同 identify 隔离会话。
 * <p>
 * 断言面：登录（认证校验器真实执行）→ 业务调用经 {@code @RpcController} 派发正确应答 →
 * 会话保持已认证；未认证调用被拒（非成功结果码）且同连接恢复可用。
 */
@Tag("integration")
@Isolated
@Tag("docker")
@Testcontainers(disabledWithoutDocker = true)
class SpringAssemblyGameServerIT {

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>("redis:7.2-alpine")
            .withCommand("redis-server", "--requirepass", DemoApps.REDIS_PASSWORD)
            .withExposedPorts(6379);

    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:4.4");

    private static DemoApps apps;

    @BeforeAll
    static void startGameServer() {
        apps = DemoApps.gameServer(REDIS.getHost(), REDIS.getFirstMappedPort(),
                MONGO.getConnectionString() + "/it-assembly");
        assertThat(apps.isNetworkServerBound()).as("官方装配产出的服务端受理中").isTrue();
    }

    @AfterAll
    static void stopGameServer() {
        if (apps != null) {
            apps.close();
        }
    }

    @Test
    void loginThenSaySucceedsAndSessionStaysAuthenticated() throws Exception {
        try (NetIntegrationHarness client = NetIntegrationHarness.startClientOnly(BodyCodecKind.TYPE_PROTOBUF)) {
            NetTunnel tunnel = client.connectTo(DemoApps.address(apps.networkPort()));
            login(tunnel, 9001L, 70001L);

            SayContentDTO first = say(tunnel, "hello-assembly");
            assertThat(first.getMessage()).startsWith("respond hello-assembly");

            // 会话保持已认证：免重登二次调用直接成功
            SayContentDTO second = say(tunnel, "again-without-relogin");
            assertThat(second.getMessage()).startsWith("respond again-without-relogin");
        }
    }

    @Test
    void unauthenticatedSayIsRejectedAndConnectionRecovers() throws Exception {
        try (NetIntegrationHarness client = NetIntegrationHarness.startClientOnly(BodyCodecKind.TYPE_PROTOBUF)) {
            NetTunnel tunnel = client.connectTo(DemoApps.address(apps.networkPort()));

            Message rejected = request(tunnel, CtrlerIds.SPEAK$SAY, "before-login").respond().get(5, TimeUnit.SECONDS);
            assertThat(rejected.getCode()).as("未认证调用返回非成功结果码").isNotEqualTo(ResultCode.SUCCESS_CODE);
            assertThat(tunnel.isActive()).as("拒绝不破坏连接").isTrue();

            // 同连接登录后重发同一调用获得正确应答
            login(tunnel, 9002L, 70002L);
            SayContentDTO ok = say(tunnel, "after-login");
            assertThat(ok.getMessage()).startsWith("respond after-login");
        }
    }

    private static void login(NetTunnel tunnel, long sessionId, long userId) throws Exception {
        Message ack = request(tunnel, CtrlerIds.LOGIN$LOGIN, sessionId, userId).respond().get(5, TimeUnit.SECONDS);
        assertThat(ack.getMode()).as("登录回执").isEqualTo(MessageMode.RESPONSE);
        assertThat(ack.getCode()).as("登录成功").isEqualTo(ResultCode.SUCCESS_CODE);
    }

    private static SayContentDTO say(NetTunnel tunnel, String text) throws Exception {
        Message ack = request(tunnel, CtrlerIds.SPEAK$SAY, text).respond().get(5, TimeUnit.SECONDS);
        assertThat(ack.getCode()).as("say 回执结果码").isEqualTo(ResultCode.SUCCESS_CODE);
        return ack.bodyAs(SayContentDTO.class);
    }

    private static MessageSent request(NetTunnel tunnel, int protocolId, Object... params) {
        return tunnel.send(MessageContents.request(Protocols.protocol(protocolId), params).willRespondFuture(5000));
    }

}
