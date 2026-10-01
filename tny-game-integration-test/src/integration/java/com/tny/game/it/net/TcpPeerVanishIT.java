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
package com.tny.game.it.net;

import com.tny.game.it.harness.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import static org.awaitility.Awaitility.*;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

/**
 * 通信中途对端消失（specs R3 Scenario"通信中途对端消失"，add-integration-testing 任务 3.4）：
 * 服务端进程内连接通道被强制关闭后，客户端隧道须在有限时间内观察到断开，不得无限挂起。
 */
@Tag("integration")
class TcpPeerVanishIT {

    @Test
    void clientObservesBoundedDisconnectWhenServerChannelCloses() throws Exception {
        try (NetIntegrationHarness harness = NetIntegrationHarness.startProtobuf()) {
            NetTunnel client = harness.connectClient();
            assertThat(client.isActive()).as("断开前连接活跃").isTrue();

            harness.shutdownServer();

            // Awaitility 的 atMost 即"有限时间"的证明：超时会使本用例失败而非挂死
            await().atMost(Duration.ofSeconds(5))
                   .pollInterval(Duration.ofMillis(50))
                   .until(() -> !client.isActive() || client.isClosed());
        }
    }

}
