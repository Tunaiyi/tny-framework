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
import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

/**
 * 断线重连补发（specs R3"断线重连后未确认消息补发"，追溯 {@code session-resend} 规格；
 * add-integration-testing 任务 4.1）。
 * <p>
 * 全程真实 socket + 真实 {@link BaseNetSession} 会话缓存/补发原语。登录由 harness 侧以
 * {@code session.online(已认证凭证)} 兑现——触发框架契约"已认证会话断开转离线保活而非关闭"
 * （{@code BaseNetSession.onUnactivated}），使缓存得以跨重连存活：
 * <ol>
 *   <li>客户端建连并登录 → 会话认证保活；</li>
 *   <li>启用发送缓存并经真实 socket 发 3 条消息（服务端首轮收齐并入缓存）；</li>
 *   <li>服务端断开——已认证会话进入 offline 保活（未被 close）；</li>
 *   <li>服务端重启、客户端以新连接重连；</li>
 *   <li>旧会话对新隧道 {@code resend} → 服务端按原序、原 id 经真实 socket 再收 3 条，
 *       且连接此后收发正常（重复送达不破坏可用性）。</li>
 * </ol>
 */
@Tag("integration")
class TcpSessionResendIT {

    private static final Protocol PUSH_PROTOCOL = new Protocol() {

        @Override
        public int getProtocolId() {
            return 777_002;
        }

        @Override
        public int getLine() {
            return 0;
        }
    };

    private static final ContactType PLAYER_TYPE = new ContactType() {

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

    @Test
    void authenticatedSessionResendsCachedMessagesInOrderAfterReconnect() throws Exception {
        try (NetIntegrationHarness harness = NetIntegrationHarness.startProtobuf()) {
            NetTunnel firstConnection = harness.connectClient();
            NetSession session = firstConnection.getSession();

            // harness 侧"登录"：写入已认证凭证，使会话断开时保活（offline）而非关闭
            session.online(Certificates.createAuthenticated(1L, 9001L, 9001L, PLAYER_TYPE));
            assertThat(session.getCertificate().isAuthenticated()).as("登录后会话凭证已认证").isTrue();

            session.setSendMessageCachedSize(16);
            for (int i = 1; i <= 3; i++) {
                firstConnection.send(MessageContents.push(PUSH_PROTOCOL, "msg-" + i));
            }
            // 先确认服务端收齐（createMessage 已在 event loop 上入缓存），再读缓存 id 序列
            List<NetMessage> initialDelivery = drainPushes(harness.serverInbound(), 3);
            List<Message> cached = session.getSentMessages(message -> PUSH_PROTOCOL.getProtocolId() == message.getProtocolId());
            assertThat(cached).as("发送缓存保留三条").hasSize(3);
            List<Long> sentIds = cached.stream().map(Message::getId).collect(Collectors.toList());
            assertThat(idsOf(initialDelivery)).as("首轮三条消息 id 序列").isEqualTo(sentIds);

            // 服务端断开：已认证会话应转离线保活（不被 close），缓存留存供补发
            harness.shutdownServer();
            assertThat(session.isClosed()).as("已认证会话断线后未被 close（离线保活）").isFalse();
            assertThat(session.isOffline()).as("断线后进入离线态").isTrue();

            // 服务端重启、客户端以新连接重连
            harness.restartServer();
            NetTunnel secondConnection = harness.connectClient();

            // 旧会话对新隧道补发缓存消息
            session.resend(secondConnection, message -> PUSH_PROTOCOL.getProtocolId() == message.getProtocolId());

            List<NetMessage> redelivered = drainPushes(harness.serverInbound(), 3);
            assertThat(idsOf(redelivered)).as("补发保持原顺序与原始 id").isEqualTo(sentIds);
            assertThat(redelivered.stream().map(RoundTripScenario::bodyOf).collect(Collectors.toList()))
                    .as("补发内容与原始一致").isEqualTo(List.of("msg-1", "msg-2", "msg-3"));

            // 重复送达不破坏会话可用：重连后新消息继续正常送达
            secondConnection.send(MessageContents.push(PUSH_PROTOCOL, "msg-health-check"));
            List<NetMessage> health = drainPushes(harness.serverInbound(), 1);
            assertThat(RoundTripScenario.bodyOf(health.get(0))).as("补发后重连仍可用").isEqualTo("msg-health-check");
        }
    }

    private static List<NetMessage> drainPushes(BlockingQueue<NetMessage> queue, int expected) throws InterruptedException {
        List<NetMessage> collected = new ArrayList<>();
        long deadline = System.currentTimeMillis() + 3000L;
        while (collected.size() < expected) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) {
                break;
            }
            NetMessage message = queue.poll(remaining, TimeUnit.MILLISECONDS);
            if (message == null) {
                break;
            }
            if (message.getProtocolId() == PUSH_PROTOCOL.getProtocolId() && message.getMode() == MessageMode.PUSH) {
                collected.add(message);
            }
        }
        assertThat(collected).as("收到 %s 条协议 %s 的 PUSH", expected, PUSH_PROTOCOL.getProtocolId()).hasSize(expected);
        return collected;
    }

    private static List<Long> idsOf(List<NetMessage> messages) {
        return messages.stream().map(Message::getId).collect(Collectors.toList());
    }

}
