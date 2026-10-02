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
