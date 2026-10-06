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
import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;

import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

/**
 * 连接-认证-请求-回执场景（specs R3/R4 共用断言体）：harness 起服、真实客户端建连后
 * 以已认证凭证登录会话（{@code session.online}），经响应关联 future 收取服务端 echo 回执，
 * 双向内容一致。认证子步与 {@code TcpSessionResendIT} 复用同一 harness 认证原语。
 */
final class RoundTripScenario {

    static final Protocol ECHO_PROTOCOL = new Protocol() {

        @Override
        public int getProtocolId() {
            return 777_001;
        }

        @Override
        public int getLine() {
            return 0;
        }
    };

    static final ContactType PLAYER_TYPE = new ContactType() {

        @Override
        public int id() {
            return 1;
        }

        @Override
        public String name() {
            return "ITPlayer";
        }

        @Override
        public String getGroup() {
            return "IT";
        }
    };

    /**
     * 建连 + 认证：真实客户端隧道建连后写入已认证凭证（触发框架登录态），返回该会话的隧道。
     */
    static NetTunnel connectAndAuthenticate(NetIntegrationHarness harness, long identify) throws Exception {
        NetTunnel client = harness.connectClient();
        NetSession session = client.getSession();
        session.online(Certificates.createAuthenticated(identify, identify, identify, PLAYER_TYPE));
        assertThat(session.getCertificate().isAuthenticated()).as("客户端会话已认证").isTrue();
        return client;
    }

    private RoundTripScenario() {
    }

    static void assertRoundTrip(NetIntegrationHarness harness, String payload) throws Exception {
        NetTunnel client = connectAndAuthenticate(harness, 7001L);

        MessageSent sent = client.send(MessageContents.request(ECHO_PROTOCOL, payload).willRespondFuture(3000));
        assertThat(sent.isRespondAwaitable()).as("请求登记了响应 future").isTrue();

        NetMessage serverSeen = harness.serverInbound().poll(3, TimeUnit.SECONDS);
        assertThat(serverSeen).as("服务端经真实 socket 收到请求").isNotNull();
        assertThat(serverSeen.getMode()).isEqualTo(MessageMode.REQUEST);
        assertThat(serverSeen.getProtocolId()).isEqualTo(ECHO_PROTOCOL.getProtocolId());
        assertThat(bodyOf(serverSeen)).isEqualTo(payload);

        Message echoed = sent.respond().get(3, TimeUnit.SECONDS);
        assertThat(echoed).as("客户端在超时窗口内观察到回执").isNotNull();
        assertThat(echoed.getMode()).isEqualTo(MessageMode.RESPONSE);
        assertThat(echoed.getToMessage()).as("回执锚定请求消息 id").isEqualTo(serverSeen.getId());
        assertThat(bodyOf(echoed)).as("回执内容与请求内容一致").isEqualTo(payload);
    }

    static Object bodyOf(Message message) {
        Object body = message.getBody();
        if (body instanceof List<?> params && !params.isEmpty()) {
            return params.get(0);
        }
        return body;
    }

}
