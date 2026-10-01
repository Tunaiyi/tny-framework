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
import org.junit.jupiter.api.*;

/**
 * 真实 TCP 消息往返端到端验证（specs R3"连接-认证-往返全链路"，add-integration-testing 任务 3.3）。
 * <p>
 * 范围披露：回执由 harness 通道层 echo（RESPOND）发出并经框架响应关联 future 收取，
 * 控制器派发栈不在本 harness 范围（见 {@code NetIntegrationHarness} 装配策略与 design D2/D3）；
 * 编解码、帧封装、TCP 传输、会话与通道生命周期均为真实生产实现。
 */
@Tag("integration")
class TcpTunnelRoundTripIT {

    @Test
    void requestFromClientIsEchoedBackOverRealSocket() throws Exception {
        try (NetIntegrationHarness harness = NetIntegrationHarness.startProtobuf()) {
            RoundTripScenario.assertRoundTrip(harness, "tny-integration-roundtrip");
        }
    }

}
