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
