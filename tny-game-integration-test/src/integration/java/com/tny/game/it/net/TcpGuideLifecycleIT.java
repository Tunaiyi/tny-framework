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
