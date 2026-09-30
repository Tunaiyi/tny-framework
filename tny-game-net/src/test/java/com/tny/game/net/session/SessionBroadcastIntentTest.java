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
package com.tny.game.net.session;

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import java.net.InetSocketAddress;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 全体广播意图固化（net-session 规格"覆盖全部注册会话且离线消息入窗"）。
 * <p>
 * 全绿基线型测试：行为零变更的更名桥接，测的是"现状=领域意图"——
 * 任一用例绿变红即提案对现状理解有误，须停下复核而非改代码。
 */
class SessionBroadcastIntentTest {

    static final ContactType PLAYER = new ContactType() {
        @Override
        public String getGroup() {
            return "player";
        }

        @Override
        public int id() {
            return 1;
        }

        @Override
        public String name() {
            return "player";
        }
    };

    /** ① 在线会话即时收到广播 */
    @Test
    void onlineSessionReceivesBroadcast() throws com.tny.game.net.exception.AuthFailedException {
        TestKit kit = new TestKit();
        NetSession online = kit.session(101L);
        online.online(kit.cert(101L));
        kit.register(online);

        kit.keeper.send2All(broadcast("公告A"));

        assertEquals(1, kit.tunnelOf(online).writes.size(), "在线会话应收到广播写出");
    }

    /** ② 离线会话：不即时写出（通道已断），但广播进入其重发窗口 */
    @Test
    void offlineSessionBroadcastEntersResendWindow() throws com.tny.game.net.exception.AuthFailedException {
        TestKit kit = new TestKit();
        NetSession session = kit.session(102L);
        session.online(kit.cert(102L));
        kit.register(session);
        session.offline();

        kit.keeper.send2All(broadcast("公告B"));

        assertEquals(0, kit.tunnelOf(session).writes.size(), "离线会话通道已断，无即时写出（写出为静默失败路径）");
        List<Message> window = session.getAllSendMessages();
        assertTrue(window.stream().anyMatch(m -> "公告B".equals(m.bodyAs(String.class))),
                "广播必须进入离线会话的重发窗口（必达意图核心）");
    }

    /** ③ 恢复重连后 resend 可补收历史广播（穿越广播=特性） */
    @Test
    void recoveredSessionCanResendHistoricalBroadcast() throws com.tny.game.net.exception.AuthFailedException {
        TestKit kit = new TestKit();
        NetSession session = kit.session(103L);
        session.online(kit.cert(103L));
        kit.register(session);
        session.offline();
        kit.keeper.send2All(broadcast("公告C"));
        long broadcastId = ((List<Message>) session.getAllSendMessages()).stream()
                .filter(m -> "公告C".equals(m.bodyAs(String.class))).findFirst().orElseThrow().getId();

        // 重连接管新通道并补收
        TestTunnel recovered = new TestTunnel();
        recovered.open();
        session.online(kit.cert(103L), recovered);
        session.resend(recovered, m -> m.getId() == broadcastId); // 谓词重载，不依赖区间边界语义

        assertTrue(recovered.writes.stream().anyMatch(m -> m.getId() == broadcastId),
                "恢复后经 resend 必须可补收断线期间广播（领域所有者裁定的必达设计）");
    }

    /** ④ 在线+离线混合广播不中断、各自处置 */
    @Test
    void mixedBroadcastCompletesWithoutInterruption() throws Exception {
        TestKit kit = new TestKit();
        NetSession online = kit.session(104L);
        online.online(kit.cert(104L));
        NetSession offline = kit.session(105L);
        offline.online(kit.cert(105L));
        kit.register(online);
        kit.register(offline);
        offline.offline();

        assertDoesNotThrow(() -> kit.keeper.send2All(broadcast("公告D")));
        assertEquals(1, kit.tunnelOf(online).writes.size(), "在线者照常送达");
        assertTrue(offline.getAllSendMessages().stream().anyMatch(m -> "公告D".equals(m.bodyAs(String.class))),
                "离线者照常入窗");
    }

    /** deprecated 桥接行为一致性：send2AllOnline 与 send2All 结果相同 */
    @Test
    void deprecatedBridgeBehavesIdentically() throws com.tny.game.net.exception.AuthFailedException {
        TestKit kit = new TestKit();
        NetSession session = kit.session(106L);
        session.online(kit.cert(106L));
        kit.register(session);

        @SuppressWarnings("deprecation")
        SessionKeeper keeper = kit.keeper;
        keeper.send2AllOnline(broadcast("公告E"));

        assertEquals(1, kit.tunnelOf(session).writes.size(), "旧名桥接后行为不变（下游零破坏证据）");
    }

    // ---------- 装配 ----------

    private static MessageContent broadcast(String body) {
        return MessageContents.push(Protocols.protocol(9000), body);
    }

    private static final class TestKit {
        final TestKeeper keeper = new TestKeeper();

        NetSession session(long identify) {
            TestTunnel tunnel = new TestTunnel();
            CommonSession session = new CommonSession(Certificates.anonymous(), new StubContext(), tunnel, 32);
            tunnels.put(identify, tunnel);
            return session;
        }

        void register(NetSession session) {
            tunnels.get(session.getIdentify()).open();   // 真实链路 initChannel 会 open；夹具须模拟
            keeper.registerSession(session);
        }

        TestTunnel tunnelOf(NetSession session) {
            return tunnels.get(session.getIdentify());
        }

        final Map<Long, TestTunnel> tunnels = new HashMap<>();

        Certificate cert(long identify) {
            return Certificates.createAuthenticated(identify, identify, identify, PLAYER);
        }
    }

    static final class TestKeeper extends AbstractSessionKeeper {
        TestKeeper() {
            super(PLAYER);
        }

        @Override
        public java.util.Optional<Session> online(Certificate certificate, NetTunnel tunnel) throws com.tny.game.net.exception.AuthFailedException {
            throw new UnsupportedOperationException("not used in broadcast intent tests");
        }

        void registerSession(NetSession session) {
            resetSession(session.getIdentify(), session);
        }
    }

    static final class TestTunnel extends TestTunnelFixture {
        final List<Message> writes = Collections.synchronizedList(new ArrayList<>());

        TestTunnel() {
            // NEVER：isActive 恒 false，让 open() 走完整激活流程置位 OPEN（write 门控用 isOpen）
            super(TestTunnelFixture.ActivePolicy.NEVER, 7100, 7000);
        }

        @Override
        public MessageWriteFuture write(MessageAllocator allocator, MessageContent content) {
            // 对齐真实 NettyChannelMessageTransport.write：allocate（入窗+登记）无条件执行，
            // 通道关闭只影响其后的 writeAndFlush（静默失败）——"离线入窗、恢复补收"的现状依据
            Message message = allocator.allocate(new CommonMessageFactory(), content);
            if (isOpen()) {
                writes.add(message);
            }
            return null;
        }

        @Override
        public MessageWriteFuture write(Message message, MessageWriteFuture promise) {
            if (!isOpen()) {
                return promise;
            }
            writes.add(message);
            return promise;
        }
    }

    static final class StubContext implements SessionContext {
        @Override
        public NetAccessMode getAccessMode() {
            return NetAccessMode.SERVER;
        }

        @Override
        public MessageDispatcher getMessageDispatcher() {
            return null;
        }

        @Override
        public CommandExecutorFactory getCommandExecutorFactory() {
            return session -> new CommandExecutor() {
                @Override
                public void executeCommand(RpcCommand command) {
                }

                @Override
                public void executeRunnable(Runnable runnable) {
                }

                @Override
                public void execute(Runnable command) {
                    command.run();
                }
            };
        }
    }

}
