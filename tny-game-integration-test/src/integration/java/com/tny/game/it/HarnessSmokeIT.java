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
