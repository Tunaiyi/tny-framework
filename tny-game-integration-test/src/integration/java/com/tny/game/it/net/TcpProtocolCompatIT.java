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
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.*;
import java.time.*;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

/**
 * 报文兼容与损坏路径（specs R4"旧版本报文兼容"/"损坏报文被拒且可观察"，追溯 {@code net-protocol}；
 * add-integration-testing 任务 4.4）。
 * <p>
 * 兼容锚：经框架真实编码器产出"最低特性集帧"（verify/encrypt/waste 可选位全关——即旧版报文形态，
 * 常量化的 accessId=7/id=1/体"legacy-hello"使字节确定），由 raw socket 直接注入服务端监听端口，
 * 证明当前服务端编解码栈脱离客户端传输层仍可独立解析并 echo。
 * 损坏路径：坏 magic + payloadLength 越界的字节流被拒（不产生合法回执），健康连接不受影响。
 */
@Tag("integration")
class TcpProtocolCompatIT {

    private static final Protocol COMPAT_PROTOCOL = new Protocol() {

        @Override
        public int getProtocolId() {
            return 777_003;
        }

        @Override
        public int getLine() {
            return 0;
        }
    };

    @Test
    void legacyMinimumFeatureFrameDecodesAndEchoesOnRawSocket() throws Exception {
        try (NetIntegrationHarness harness = NetIntegrationHarness.startProtobuf()) {
            NetMessage message = new CommonMessageFactory().create(1L, MessageContents.request(COMPAT_PROTOCOL, "legacy-hello"));
            byte[] frame = harness.encodeClientFrame(message);

            try (Socket socket = new Socket()) {
                socket.connect(harness.serverAddress(), 2000);
                socket.setSoTimeout(3000);
                socket.getOutputStream().write(frame);
                socket.getOutputStream().flush();

                byte[] echo = socket.getInputStream().readNBytes(16);
                assertThat(echo).as("服务端独立解析最低特性集帧并 echo 回执帧").isNotEmpty();
            }
            // 注入的 COMPAT_PROTOCOL 请求也进了 serverInbound，清空后再验证健康连接不受影响
            harness.serverInbound().clear();
            RoundTripScenario.assertRoundTrip(harness, "compat-still-healthy");
        }
    }

    @Test
    void corruptFrameIsRejectedWithoutAffectingHealthyConnection() throws Exception {
        try (NetIntegrationHarness harness = NetIntegrationHarness.startProtobuf();
             LogCapture capture = LogCapture.attach()) {
            // 帧 = 4 字节 magic + 1 字节 option + 4 字节 fixed32 payloadLength（此处声明超大值越界）；magic 非法
            byte[] corrupt = new byte[]{0, 0, 0, 0, 0x20, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x7F, 1, 2, 3};
            int corruptClientPort;
            try (Socket socket = new Socket()) {
                socket.connect(harness.serverAddress(), 2000);
                socket.setSoTimeout(2000);
                corruptClientPort = socket.getLocalPort();
                socket.getOutputStream().write(corrupt);
                socket.getOutputStream().flush();
                try {
                    int read = socket.getInputStream().read();
                    assertThat(read).as("损坏帧不产生合法回执").isLessThan(0);
                } catch (IOException expected) {
                    // 连接被服务端关闭/重置，符合"拒绝"语义
                }
            }
            // specs R4"损坏报文被拒且可观察"：解码异常告警须可定位到该损坏连接的远端端口
            final int port = corruptClientPort;
            await().atMost(Duration.ofSeconds(3)).until(() ->
                    capture.events().anyMatch(text -> text.contains("Tunnel") && text.contains(":" + port)));
            // 无跨连接污染：健康客户端仍能正常往返
            RoundTripScenario.assertRoundTrip(harness, "corrupt-no-cross-impact");
        }
    }

}
