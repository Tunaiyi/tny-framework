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
package com.tny.game.it;

import com.tny.game.it.harness.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * harness 哨兵集成测试（add-integration-testing 任务 3.2，specs R3"连接-认证-往返全链路"的建连半程）。
 * <p>
 * 验证最小装配可起可连可关：真实 TCP 起服（临时端口）、真实客户端隧道建连、
 * 关闭后服务端不再受理（isBound 归真为假）。消息往返全链路见 {@code TcpTunnelRoundTripIT}。
 */
@Tag("integration")
class HarnessSmokeIT {

    @Test
    void serverBindsClientConnectsAndCloseIsClean() throws Exception {
        NetIntegrationHarness harness = NetIntegrationHarness.startProtobuf();
        try {
            assertThat(harness.isServerBound()).as("服务端绑定后 isBound 为真").isTrue();
            assertThat(harness.serverAddress().getPort()).as("实际端口非 0").isPositive();

            NetTunnel tunnel = harness.connectClient();
            assertThat(tunnel.isOpen()).as("客户端隧道已打开").isTrue();
            assertThat(tunnel.isActive()).as("客户端隧道活跃").isTrue();
        } finally {
            harness.close();
        }
        assertThat(harness.isServerBound()).as("关闭后服务端不再受理").isFalse();
    }

}
