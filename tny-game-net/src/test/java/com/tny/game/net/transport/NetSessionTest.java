package com.tny.game.net.transport;

import com.tny.game.net.exception.*;
import com.tny.game.net.transport.message.*;
import org.junit.*;
import org.slf4j.*;
import test.TestAide;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static test.MockAide.any;
import static test.MockAide.eq;
import static test.MockAide.*;
import static test.MockAide.never;
import static test.MockAide.times;
import static test.MockAide.verify;

/**
 * Created by Kun Yang on 2018/8/12.
 */
public abstract class NetSessionTest<S extends NetSession<Long>> extends SessionTest<S> {

    public static final int CACHE_MESSAGE_SIZE = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger(NetSessionTest.class);


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
        NetTunnel<Long> otherTunnel = mockAs(NetTunnel.class);
        NetTunnel<Long> loginTunnel = mockTunnel(createLoginCert());
        NetSession<Long> loginSession = createSession(loginTunnel);

        assertFalse(loginSession.isOffline());
        // 其他 otherTunnel 下线
        loginSession.discardTunnel(otherTunnel);
        assertFalse(loginSession.isOffline());

        // 当前 tunnel 下线
        loginSession.discardTunnel(loginTunnel);
        assertTrue(loginSession.isOffline());
        // 下线后再次下线
        loginSession.discardTunnel(loginTunnel);
        assertTrue(loginSession.isOffline());

        // 关闭后下线
        loginSession = createSession();
        loginSession.close();
        loginSession.discardTunnel(loginTunnel);
        assertFalse(loginSession.isOffline());
    }

    @Test
    public void getSentMessageByToID() {
        NetSession<Long> session = createSession();
        TestMessages messages = new TestMessages(session)
                .addPush("push 1")
                .addResponse("request 2", 1)
                .addResponse("request 3", 1)
                .addResponse("request 4", 1)
                .addResponse("request 5", 1)
                .addResponse("request 6", 1)
                .addRequest("request 7");
        messages.messagesForEach(m -> assertNull(session.getSentMessage(m.getId())));
        messages.messagesForEach(session::addSentMessage);
        // sendAndProcessOutput(messages, session);
        messages.messagesForEach(m -> assertNotNull(session.getSentMessage(m.getId())));
    }

    @Test
    public void getSentMessages() {
        NetSession<Long> session;
        List<Message<Long>> sentMessages;
        TestMessages messages;


        session = createSession();
        messages = new TestMessages(session)
                .addPush("push 1")
                .addResponse("request 2", 1)
                .addResponse("request 3", 1)
                .addResponse("request 4", 1)
                .addResponse("request 5", 1)
                .addResponse("request 6", 1)
                .addRequest("request 7");
        // List<>messages.getMessages();
        sentMessages = session.getSentMessages(3L, -1);
        assertTrue(sentMessages.isEmpty());
        messages.messagesForEach(session::addSentMessage);
        sentMessages = session.getSentMessages(1L, -1L);
        assertEquals(messages.getMessageSize(), sentMessages.size());
        sentMessages = session.getSentMessages(-1L, -1L);
        assertEquals(messages.getMessageSize(), sentMessages.size());
        sentMessages = session.getSentMessages(-1L, 3L);
        assertEquals(3, sentMessages.size());
        sentMessages = session.getSentMessages(2L, 3L);
        sentMessages = session.getSentMessages(2L, 3L);
        assertEquals(2, sentMessages.size());
        sentMessages = session.getSentMessages(1L, (long) messages.getMessageSize() + 10);
        assertEquals(messages.getMessageSize(), sentMessages.size());


        session = createSession();
        int messageSize = 13;
        messages = new TestMessages(session);
        for (int index = 1; index < messageSize; index++) {
            messages.addPush("push " + index);
        }
        sentMessages = session.getSentMessages(3L, -1);
        assertTrue(sentMessages.isEmpty());
        messages.messagesForEach(session::addSentMessage);
        sentMessages = session.getSentMessages(1L, -1L);
        assertTrue(sentMessages.isEmpty());
        sentMessages = session.getSentMessages(-1L, -1L);
        assertEquals(CACHE_MESSAGE_SIZE, sentMessages.size());
        sentMessages = session.getSentMessages(messageSize - CACHE_MESSAGE_SIZE, messageSize - 1);
        assertEquals(CACHE_MESSAGE_SIZE, sentMessages.size());
        sentMessages = session.getSentMessages(4L, 6L);
        assertEquals(3, sentMessages.size());
        sentMessages = session.getSentMessages(3L, (long) CACHE_MESSAGE_SIZE + 10);
        assertEquals(CACHE_MESSAGE_SIZE, sentMessages.size());
    }


    @Override
    @Test
    public void send() {
        TestMessages messages;
        NetSession<Long> session;
        NetTunnel<Long> tunnel;

        // 发送 message
        tunnel = mockTunnel();
        session = createSession(tunnel);
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.send(session);
        NetTunnel<Long> tunnel1 = tunnel;
        messages.subjcetForEach(c -> verify(tunnel1, times(1)).sendAsyn(eq(c)));


        // 离线发送message
        tunnel = mockTunnel();
        session = createSession(tunnel);
        session.offline();
        messages = new TestMessages(session)
                .addPush("push")
                .addPush("push")
                .addPush("push");
        messages.send(session);
        NetTunnel<Long> tunnel2 = tunnel;
        messages.subjcetForEach(c -> verify(tunnel2, times(1)).sendAsyn(eq(c)));


        // 关闭发送message
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

    }

    @Test
    @Override
    public void resend() {
        // ResendMessage<Long> message = ResendMessage.fromTo(1, 7);
        // NetSession<Long> session;
        // NetTunnel<Long> tunnel;
        //
        // // 在线 resend
        // session = createLoginSession();
        // tunnel = mockNetTunnel(session);
        // session.resend(message);
        // // assertEquals(1, session.getOutputEventSize());
        // verify(tunnel, times(1)).addOutputEvent(any(MessageResendEvent.class));
        //
        // // 离线 resend
        // session = createLoginSession();
        // tunnel = mockNetTunnel(session);
        // session.offline();
        // assertResendException(session, message, ExecutionException.class);
        // verify(tunnel, never()).addOutputEvent(any(MessageResendEvent.class));
        //
        // // 离线 resend
        // session = createLoginSession();
        // tunnel = mockNetTunnel(session);
        // session.close();
        // assertResendException(session, message, ExecutionException.class);
        // verify(tunnel, never()).addOutputEvent(any(MessageResendEvent.class));

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