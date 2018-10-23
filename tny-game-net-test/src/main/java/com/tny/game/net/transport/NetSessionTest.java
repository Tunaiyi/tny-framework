package com.tny.game.net.transport;

import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
import com.tny.game.test.TestAide;
import org.junit.*;

import java.util.concurrent.atomic.AtomicLong;

import static com.tny.game.test.MockAide.mockAs;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.tny.game.test.MockAide.*;

/**
 * Created by Kun Yang on 2018/8/12.
 */
public abstract class NetSessionTest<S extends NetSession<Long>> extends SessionTest<S> {

    private static final AtomicLong TUNNEL_ID_CREATOR = new AtomicLong();

    protected NetSessionTest() {
    }

    @Override
    protected S createSession(Certificate<Long> certificate) {
        S session = newSession(certificate);
        TestAide.assertRunComplete("acceptTunnel", () -> session.acceptTunnel(mockTunnel(certificate)));
        return session;
    }

    protected S createSession(NetTunnel<Long> tunnel) {
        S session = newSession(tunnel.getCertificate());
        TestAide.assertRunComplete("acceptTunnel", () -> session.acceptTunnel(tunnel));
        return session;
    }

    protected NetTunnel<Long> mockTunnel() {
        return mockTunnel(createLoginCert());
    }

    protected NetTunnel<Long> mockTunnel(Certificate<Long> certificate) {
        long tunnelId = TUNNEL_ID_CREATOR.incrementAndGet();
        NetTunnel<Long> tunnel = mockAs(NetTunnel.class);
        when(tunnel.bind(any())).thenReturn(true);
        when(tunnel.getCertificate()).thenReturn(certificate);
        when(tunnel.getId()).thenReturn(tunnelId);
        return tunnel;
    }

    protected abstract S newSession(Certificate<Long> certificate);

    @Override
    protected S createSession() {
        return createSession(certificateId, uid);
    }

    protected S createSession(long certificateId, Long uid) {
        return createSession(createLoginCert(certificateId, uid));
    }

    @Override
    protected void doOffline(S session) {
        session.offline();
    }

    @Test
    public abstract void acceptTunnel();

    @Test
    public void offline() {
        NetSession<Long> loginSession = createSession();
        assertFalse(loginSession.isOffline());
        loginSession.offline();
        assertTrue(loginSession.isOffline());
    }

    @Test
    public void offlineIfCurrent() {
        NetTunnel<Long> loginTunnel = mockTunnel(createLoginCert());
        NetSession<Long> loginSession = createSession(loginTunnel);

        assertFalse(loginSession.isOffline());
        when(loginTunnel.isClosed()).thenReturn(false);
        loginSession.onDisable(loginTunnel);
        assertEquals(SessionState.ONLINE, loginSession.getState());

        // 当前 tunnel 下线
        when(loginTunnel.isClosed()).thenReturn(true);
        loginSession.onDisable(loginTunnel);
        assertEquals(SessionState.OFFLINE, loginSession.getState());

        // 当前 tunnel 下线
        loginSession.onDisable(loginTunnel);
        assertEquals(SessionState.OFFLINE, loginSession.getState());

        // 关闭后下线
        loginSession = createSession();
        loginSession.close();
        loginSession.onDisable(loginTunnel);
        assertEquals(SessionState.CLOSE, loginSession.getState());
    }

    // @Test
    // public void getSentMessageByToID() {
    //     NetSession<Long> session = createSession();
    //     TestMessages messages = new TestMessages(session)
    //             .addPush("push 1")
    //             .addResponse("request 2", 1)
    //             .addResponse("request 3", 1)
    //             .addResponse("request 4", 1)
    //             .addResponse("request 5", 1)
    //             .addResponse("request 6", 1)
    //             .addRequest("request 7");
    //     messages.messagesForEach(m -> assertNull(session.getSentMessage(m.getId())));
    //     messages.messagesForEach(session::addSentMessage);
    //     // sendAndProcessOutput(messages, session);
    //     messages.messagesForEach(m -> assertNotNull(session.getSentMessage(m.getId())));
    // }

    // @Test
    // public void getSentMessages() {
    //     NetSession<Long> session;
    //     List<Message<Long>> sentMessages;
    //     TestMessages messages;
    //
    //
    //     session = createSession();
    //     messages = new TestMessages(session)
    //             .addPush("push 1")
    //             .addResponse("request 2", 1)
    //             .addResponse("request 3", 1)
    //             .addResponse("request 4", 1)
    //             .addResponse("request 5", 1)
    //             .addResponse("request 6", 1)
    //             .addRequest("request 7");
    //     // List<>messages.getMessages();
    //     sentMessages = session.getSentMessages(3L, -1);
    //     assertTrue(sentMessages.isEmpty());
    //     messages.messagesForEach(session::addSentMessage);
    //     sentMessages = session.getSentMessages(1L, -1L);
    //     assertEquals(messages.getMessageSize(), sentMessages.size());
    //     sentMessages = session.getSentMessages(-1L, -1L);
    //     assertEquals(messages.getMessageSize(), sentMessages.size());
    //     sentMessages = session.getSentMessages(-1L, 3L);
    //     assertEquals(3, sentMessages.size());
    //     sentMessages = session.getSentMessages(2L, 3L);
    //     sentMessages = session.getSentMessages(2L, 3L);
    //     assertEquals(2, sentMessages.size());
    //     sentMessages = session.getSentMessages(1L, (long) messages.getMessageSize() + 10);
    //     assertEquals(messages.getMessageSize(), sentMessages.size());
    //
    //
    //     session = createSession();
    //     int messageSize = 13;
    //     messages = new TestMessages(session);
    //     for (int index = 1; index < messageSize; index++) {
    //         messages.addPush("push " + index);
    //     }
    //     sentMessages = session.getSentMessages(3L, -1);
    //     assertTrue(sentMessages.isEmpty());
    //     messages.messagesForEach(session::addSentMessage);
    //     sentMessages = session.getSentMessages(1L, -1L);
    //     assertTrue(sentMessages.isEmpty());
    //     sentMessages = session.getSentMessages(-1L, -1L);
    //     assertEquals(CACHE_MESSAGE_SIZE, sentMessages.size());
    //     sentMessages = session.getSentMessages(messageSize - CACHE_MESSAGE_SIZE, messageSize - 1);
    //     assertEquals(CACHE_MESSAGE_SIZE, sentMessages.size());
    //     sentMessages = session.getSentMessages(4L, 6L);
    //     assertEquals(3, sentMessages.size());
    //     sentMessages = session.getSentMessages(3L, (long) CACHE_MESSAGE_SIZE + 10);
    //     assertEquals(CACHE_MESSAGE_SIZE, sentMessages.size());
    // }


    @Override
    @Test
    public void send() {
        TestMessages messages;
        NetSession<Long> session;
        NetTunnel<Long> tunnel;
        long timeout = 1000L;

        // 发送 sendAsyn message
        tunnel = mockTunnel();
        session = createSession(tunnel);
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.sendAsyn(session);
        verify(tunnel, times(messages.getMessageSize())).write(any(Message.class), isNull(MessageContext.class), eq(0L), any());

        // 发送 sendAsyn message willSendFuture
        tunnel = mockTunnel();
        session = createSession(tunnel);
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.forEach(p -> p.context().willSendFuture());
        messages.sendAsyn(session);
        verify(tunnel, times(messages.getMessageSize())).write(any(Message.class), any(MessageContext.class), eq(0L), any());

        // 发送 sendAsyn message willResponseFuture
        tunnel = mockTunnel();
        session = createSession(tunnel);
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.forEach(p -> p.context().willResponseFuture());
        messages.sendAsyn(session);
        verify(tunnel, times(messages.getMessageSize())).write(any(Message.class), any(MessageContext.class), eq(0L), any());

        // 发送 sendSync message
        tunnel = mockTunnel();
        session = createSession(tunnel);
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.sendSync(session, timeout);
        verify(tunnel, times(messages.getMessageSize())).write(any(Message.class), isNull(MessageContext.class), eq(timeout), any());

        // 发送 sendSync message
        tunnel = mockTunnel();
        session = createSession(tunnel);
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.forEach(p -> p.context().willResponseFuture());
        messages.sendSync(session, timeout);
        verify(tunnel, times(messages.getMessageSize())).write(any(Message.class), any(MessageContext.class), eq(timeout), any());

        // 发送 sendSync message
        tunnel = mockTunnel();
        session = createSession(tunnel);
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.forEach(p -> p.context().willResponseFuture());
        messages.sendSync(session, timeout);
        verify(tunnel, times(messages.getMessageSize())).write(any(Message.class), any(MessageContext.class), eq(timeout), any());

        // 离线发送 sendAsyn message
        tunnel = mockTunnel();
        session = createSession(tunnel);
        session.offline();
        messages = new TestMessages(session)
                .addPush("push")
                .addPush("push")
                .addPush("push");
        messages.sendAsyn(session);
        verify(tunnel, times(messages.getMessageSize())).write(any(Message.class), isNull(MessageContext.class), eq(0L), any());

        // 关闭发送 sendAsyn message
        tunnel = mockTunnel();
        session = createSession(tunnel);
        session.close();
        messages = new TestMessages(session)
                .addPush("push")
                .addPush("push")
                .addPush("push");
        NetSession<Long> closeSession = session;
        messages.subjcetForEach(c ->
                TestAide.assertRunWithException("close session.send",
                        () -> closeSession.sendAsyn(c), NetException.class));
        verify(tunnel, never()).sendAsyn(any(MessageSubject.class));

        // filter 发送
        tunnel = mockTunnel();
        session = createSession(tunnel);
        session.setSendFilter(k -> false);
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.sendAsyn(session);
        verify(tunnel, never()).write(any(Message.class), isNull(MessageContext.class), eq(0L), any());
    }

    protected void assertAcceptTunnelOk(NetSession<Long> session, NetTunnel<Long> tunnel) {
        TestAide.assertRunComplete("assertLoginOk", () -> {
            session.acceptTunnel(tunnel);
            assertTrue(session.isLogin());
        });
    }

    protected void assertAcceptTunnelException(NetSession<Long> session, NetTunnel<Long> tunnel) {
        TestAide.assertRunWithException("assertLoginException", () -> session.acceptTunnel(tunnel), ValidatorFailException.class);
    }

    protected TestMessages createMessage(NetSession<Long> session) {
        return new TestMessages(session)
                .addPush("push 1")
                .addRequest("request 2")
                .addRequest("request 3");
    }

}