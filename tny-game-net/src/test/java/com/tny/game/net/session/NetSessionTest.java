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

import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import com.tny.game.test.*;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.function.Predicate;

import static com.tny.game.net.transport.TestMessages.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2018/8/12.
 */
public abstract class NetSessionTest<E extends NetSession> extends SessionTest<E> {

    private static final int CACHE_MESSAGE_SIZE = 10;

    private static final Predicate<Message> all = (m) -> true;

    private static final Predicate<Message> gte1 = (m) -> m.getId() >= 1L;

    private static final Predicate<Message> lte3 = (m) -> m.getId() <= 3L;

    private static final Predicate<Message> gte2lte3 = (m) -> 2 <= m.getId() && m.getId() <= 3L;

    protected NetSessionTest() {
    }

    @Override
    protected SessionTestInstance<E> create(Certificate certificate) {
        MockNetTunnel tunnel = mockTunnel();
        E session = newSession(new CommonSessionSetting().setSendMessageCachedSize(CACHE_MESSAGE_SIZE), tunnel);
        tunnel.bind(session);
        if (certificate.isAuthenticated()) {
            TestAide.assertRunComplete("acceptTunnel", () -> session.online(certificate, tunnel));
        }
        return new SessionTestInstance<>(session, tunnel);
    }

    private MockNetTunnel mockTunnel() {
        return new MockNetTunnel(NetAccessMode.SERVER);
    }

    protected abstract E newSession(CommonSessionSetting setting, NetTunnel tunnel);

    @Override
    public E createNetter(Certificate certificate) {
        return create(certificate).getSesison();
    }

    @Override
    protected SessionTestInstance<E> create() {
        return create(certificateId, uid);
    }

    private SessionTestInstance<E> create(long certificateId, Long uid) {
        return create(createLoginCert(certificateId, uid));
    }

    @Override
    protected void doOffline(E session) {
        session.offline();
    }

    @Test
    void offline() {
        NetSession loginSession = create().getSesison();
        assertFalse(loginSession.isOffline());
        loginSession.offline();
        assertTrue(loginSession.isOffline());
    }

    @Test
    void onUnactivated() {
        Certificate certificate = createLoginCert();
        SessionTestInstance<E> object = create(certificate);
        E loginSession = object.getSesison();
        MockNetTunnel loginTunnel = object.getTunnel();

        assertFalse(loginSession.isOffline());
        loginSession.onUnactivated(loginTunnel);
        assertEquals(SessionStatus.ONLINE, loginSession.getStatus());

        // 当前 tunnel 下线
        loginTunnel.close();
        assertEquals(SessionStatus.OFFLINE, loginSession.getStatus());

        // 当前 tunnel 下线
        loginSession.onUnactivated(loginTunnel);
        assertEquals(SessionStatus.OFFLINE, loginSession.getStatus());

        // 关闭后下线
        object = create(certificate);
        loginSession = object.getSesison();
        loginSession.close();
        // loginSession.onUnactivated(loginTunnel);
        assertEquals(SessionStatus.CLOSE, loginSession.getStatus());
    }

    @Test
    void getSendMessages() {
        SessionTestInstance<E> object;
        List<Message> sentMessages;
        TestMessages messages;

        object = create();
        NetSession e0 = object.getSesison();
        NetTunnel t0 = object.getTunnel();
        messages = createMessages(e0);
        messages.messagesForEach(m -> assertEquals(0, e0.getSentMessages(sm -> sm.getId() == m.getId()).size()));
        messages.contextsForEach(c -> e0.createMessage(t0.getMessageFactory(), c));
        messages.messagesForEach(m -> assertEquals(1, e0.getSentMessages(sm -> sm.getId() == m.getId()).size()));

        object = create();
        NetSession e1 = object.getSesison();
        NetTunnel t1 = object.getTunnel();
        messages = createMessages(e1);
        // List<>messages.getMessages();

        sentMessages = e1.getSentMessages(all);
        assertTrue(sentMessages.isEmpty());
        messages.contextsForEach(c -> e1.createMessage(t1.getMessageFactory(), c));
        sentMessages = e1.getSentMessages(gte1);
        assertEquals(messages.getMessageSize(), sentMessages.size());
        sentMessages = e1.getSentMessages(all);
        assertEquals(messages.getMessageSize(), sentMessages.size());
        sentMessages = e1.getSentMessages(lte3);
        assertEquals(3, sentMessages.size());
        sentMessages = e1.getSentMessages(gte2lte3);
        assertEquals(2, sentMessages.size());

        object = create();
        NetSession e2 = object.getSesison();
        NetTunnel t2 = object.getTunnel();
        int messageSize = 13;
        messages = new TestMessages(e2);
        for (int index = 1; index <= messageSize; index++) {
            messages.addPush("push " + index);
        }
        sentMessages = e2.getSentMessages(all);
        assertTrue(sentMessages.isEmpty());

        messages.contextsForEach(c -> e2.createMessage(t2.getMessageFactory(), c));

        sentMessages = e2.getSentMessages(lte3);
        assertTrue(sentMessages.isEmpty());

        sentMessages = e2.getSentMessages(all);
        assertEquals(CACHE_MESSAGE_SIZE, sentMessages.size());

        sentMessages = e2.getSentMessages(m -> 4 <= m.getId() && m.getId() <= 13);
        assertEquals(CACHE_MESSAGE_SIZE, sentMessages.size());
    }

    //    @Override
    //    @Test
    //    public void receive() {
    //        TestMessages messages;
    //        NetSession session;
    //        SessionTestInstance<E> object;
    //
    //        // 接收 receive message
    //        object = create();
    //        session = object.getSession();
    //        handler = object.getHandler();
    //        messages = createMessages(session);
    //        messages.receive(session);
    //        assertEquals(handler.getInputTimes(), messages.getMessageSize());
    //
    //        // 接收 send message
    //        object = create();
    //        session = object.getSession();
    //        handler = object.getHandler();
    //        session.offline();
    //        messages = createMessages(session);
    //        messages.receive(session);
    //        assertEquals(handler.getInputTimes(), messages.getMessageSize());
    //
    //        // 关闭接收 send message
    //        object = create();
    //        session = object.getSession();
    //        handler = object.getHandler();
    //        session.close();
    //        messages = createMessages(session);
    //        messages.receive(session);
    //        assertEquals(handler.getInputTimes(), 0);
    //
    //        // filter 接收 MessageHandleStrategy.IGNORE
    //        object = create();
    //        session = object.getSession();
    //        handler = object.getHandler();
    //        session.setReceiveFilter((e, m) -> MessageHandleStrategy.IGNORE);
    //        messages = createMessages(session);
    //        messages.receive(session);
    //        assertEquals(handler.getInputTimes(), 0);
    //
    //        // filter 发送 MessageHandleStrategy.THROW
    //        object = create();
    //        session = object.getSession();
    //        handler = object.getHandler();
    //        session.setReceiveFilter((e, m) -> MessageHandleStrategy.THROW);
    //        messages = createMessages(session);
    //        NetSession e1 = session;
    //        TestMessages ms1 = messages;
    //        TestAide.assertRunWithException(() -> ms1.receive(e1));
    //        assertEquals(handler.getInputTimes(), 0);
    //    }
    //
    //    @Override
    //    @Test
    //    public void send() {
    //        TestMessages messages;
    //        NetSession session;
    //        SessionTestInstance<E> object;
    //        MockSessionEventsBoxHandler<E> handler;
    //
    //        // 发送 send message
    //        object = create();
    //        session = object.getSession();
    //        handler = object.getHandler();
    //        messages = createMessages(session);
    //        messages.send(session);
    //        assertEquals(handler.getOutputTimes(), messages.getMessageSize());
    //
    //        // 发送 send message willResponseFuture
    //        object = create();
    //        session = object.getSession();
    //        handler = object.getHandler();
    //        messages = createMessages(session);
    //        messages.forEach(p -> p.getRequestContext().willResponseFuture());
    //        messages.send(session);
    //        assertEquals(handler.getOutputTimes(), messages.getMessageSize());
    //
    //        // 离线发送 send message
    //        object = create();
    //        session = object.getSession();
    //        handler = object.getHandler();
    //        session.offline();
    //        messages = createMessages(session);
    //        messages.send(session);
    //        assertEquals(handler.getOutputTimes(), messages.getMessageSize());
    //
    //        // 关闭发送 send message
    //        object = create();
    //        session = object.getSession();
    //        handler = object.getHandler();
    //        session.close();
    //        messages = createMessages(session);
    //        messages.send(session);
    //        assertEquals(handler.getOutputTimes(), 0);
    //
    //        // filter 发送 MessageHandleStrategy.IGNORE
    //        object = create();
    //        session = object.getSession();
    //        handler = object.getHandler();
    //        session.setSendFilter((e, m) -> MessageHandleStrategy.IGNORE);
    //        messages = createMessages(session);
    //        messages.send(session);
    //        assertEquals(handler.getOutputTimes(), 0);
    //
    //        // filter 发送 MessageHandleStrategy.THROW
    //        object = create();
    //        session = object.getSession();
    //        handler = object.getHandler();
    //        session.setSendFilter((e, m) -> MessageHandleStrategy.THROW);
    //        messages = createMessages(session);
    //        NetSession e1 = session;
    //        TestMessages ms1 = messages;
    //        TestAide.assertRunWithException(() -> ms1.send(e1));
    //        assertEquals(handler.getOutputTimes(), 0);
    //    }
    //
    //    @Override
    //    @Test
    //    public void resend() {
    //        NetSession session;
    //        SessionTestInstance<E> object;
    //        MockSessionEventsBoxHandler<E> handler;
    //
    //        // 发送 resend message
    //        object = create();
    //        session = object.getSession();
    //        handler = object.getHandler();
    //        session.resend(null, all);
    //        session.resend(null, all);
    //        session.resend(null, all);
    //        assertEquals(3, handler.getOutputTimes());
    //
    //        // close 发送 resend message
    //        object = create();
    //        session = object.getSession();
    //        handler = object.getHandler();
    //        session.close();
    //        session.resend(null, all);
    //        session.resend(null, all);
    //        session.resend(null, all);
    //        assertEquals(0, handler.getOutputTimes());
    //    }

    @Test
    void online() {
        MockNetTunnel loginTunnel;
        MockNetTunnel otherTunnel;
        Certificate newCertificate;
        Certificate certificate = createLoginCert();
        SessionTestInstance<E> object;
        E loginSession;

        // 用未登录授权登录
        object = create(createUnLoginCert());
        loginSession = object.getSesison();
        loginTunnel = object.getTunnel();
        newCertificate = createUnLoginCert();
        assertAcceptTunnelException(loginSession, newCertificate, loginTunnel);

        // 正常登录
        object = create(createUnLoginCert());
        loginSession = object.getSesison();
        loginTunnel = object.getTunnel();
        assertAcceptTunnelOk(loginSession, certificate, loginTunnel);

        // 重复登录 同一授权 同一tunnel
        object = create(certificate);
        loginSession = object.getSesison();
        loginTunnel = object.getTunnel();
        assertAcceptTunnelOk(loginSession, certificate, loginTunnel);

        // 重复登录 相同授权 同一tunnel
        object = create(certificate);
        loginSession = object.getSesison();
        newCertificate = createLoginCert();
        assertAcceptTunnelOk(loginSession, newCertificate, loginTunnel);

        // 重复登录 相同授权 其他tunnel
        object = create(certificate);
        loginSession = object.getSesison();
        newCertificate = createLoginCert();
        otherTunnel = create(createUnLoginCert()).getTunnel();
        assertAcceptTunnelOk(loginSession, newCertificate, otherTunnel);

        // 重复登录 不同授权 同一tunnel
        object = create(certificate);
        loginSession = object.getSesison();
        loginTunnel = object.getTunnel();
        newCertificate = createLoginCert(certificateId + 1, uid);
        assertAcceptTunnelException(loginSession, newCertificate, loginTunnel);

        // 重复登录 不同授权 不通tunnel
        object = create(certificate);
        loginSession = object.getSesison();
        otherTunnel = create(createUnLoginCert()).getTunnel();
        newCertificate = createLoginCert(certificateId + 1, uid);
        assertAcceptTunnelException(loginSession, newCertificate, otherTunnel);

        // session 离线 重登 session
        object = create(certificate);
        loginSession = object.getSesison();
        loginSession.offline();
        otherTunnel = create(createUnLoginCert()).getTunnel();
        newCertificate = createLoginCert();
        assertAcceptTunnelOk(loginSession, newCertificate, otherTunnel);

        // session 关闭登录
        object = create(certificate);
        loginSession = object.getSesison();
        loginSession.close();
        otherTunnel = create(createUnLoginCert()).getTunnel();
        newCertificate = createLoginCert();
        assertAcceptTunnelException(loginSession, newCertificate, otherTunnel);

        // session tunnel bind fail
        object = create(createUnLoginCert());
        loginSession = object.getSesison();
        loginTunnel = object.getTunnel();
        loginTunnel.setBindSuccess(false);
        newCertificate = createLoginCert();
        assertAcceptTunnelException(loginSession, newCertificate, loginTunnel);

        // session tunnel bind fail
        object = create();
        loginSession = object.getSesison();
        otherTunnel = create(createUnLoginCert()).getTunnel();
        otherTunnel.setBindSuccess(false);
        newCertificate = createLoginCert();
        assertAcceptTunnelException(loginSession, newCertificate, otherTunnel);

        // session tunnel bind fail
        object = create();
        loginSession = object.getSesison();
        loginTunnel = object.getTunnel();
        loginTunnel.setBindSuccess(false);
        newCertificate = createLoginCert();
        assertAcceptTunnelException(loginSession, newCertificate, loginTunnel);
    }

    private void assertAcceptTunnelOk(NetSession session, Certificate certificate, NetTunnel tunnel) {
        TestAide.assertRunComplete("assertLoginOk", () -> {
            session.online(certificate, tunnel);
            assertTrue(session.isAuthenticated());
        });
    }

    private void assertAcceptTunnelException(NetSession session, Certificate certificate, NetTunnel tunnel) {
        TestAide.assertRunWithException("assertLoginException", () -> session.online(certificate, tunnel), AuthFailedException.class);
    }

}