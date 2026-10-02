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

import static org.assertj.core.api.Assertions.*;

/**
 * 引导器生命周期（specs R3"服务端停止与重启"，追溯 {@code net-guide-lifecycle} 规格；
 * add-integration-testing 任务 4.2）。
 * <p>
 * 将 {@code GuideLifecycleTest} 中"移交 demo 手动验收"的**真实连接受理**行为钉入集成通道：
 * 服务端 close 释放监听、同实例 open 重建线程组/构建器后再次受理新连接（兑现重开合同）。
 */
@Tag("integration")
class TcpGuideLifecycleIT {

    @Test
    void serverStopsReleasesAndReopensToAcceptAgain() throws Exception {
        try (NetIntegrationHarness harness = NetIntegrationHarness.startProtobuf()) {
            assertThat(harness.isServerBound()).as("初始受理中").isTrue();
            NetTunnel first = harness.connectClient();
            assertThat(first.isActive()).as("首个连接活跃").isTrue();

            harness.shutdownServer();
            assertThat(harness.isServerBound()).as("close 后不再受理（isBound 真值）").isFalse();

            harness.restartServer();
            assertThat(harness.isServerBound()).as("同实例 open 重建后再次受理").isTrue();

            NetTunnel second = harness.connectClient();
            assertThat(second.isActive()).as("重启后新客户端可建连").isTrue();
        }
    }

}
