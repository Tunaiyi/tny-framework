package com.tny.game.net.transport;

import com.google.common.collect.Range;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.message.Message;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(NetSessionTest.class);


    private static final AtomicLong TUNNEL_ID_CREATOR = new AtomicLong();

    protected NetSessionTest() {
    }

    @Override
    protected S createSession(Certificate<Long> certificate) {
        S session = newSession(certificate);
        TestAide.assertRunWithoutException("acceptTunnel", () -> session.acceptTunnel(mockTunnel(certificate)));
        return session;
    }

    protected S createSession(NetTunnel<Long> tunnel) {
        S session = newSession(tunnel.getCertificate());
        TestAide.assertRunWithoutException("acceptTunnel", () -> session.acceptTunnel(tunnel));
        return session;
    }

    protected NetTunnel<Long> mockTunnel() {
        return mockTunnel(createLoginCert());
    }

    protected NetTunnel<Long> mockTunnel(Certificate<Long> certificate) {
        long tunnelId = TUNNEL_ID_CREATOR.incrementAndGet();
        MessageEventsBox<Long> eventsBox = mockAs(MessageEventsBox.class);
        NetTunnel<Long> tunnel = mockAs(NetTunnel.class);
        when(tunnel.getCertificate()).thenReturn(certificate);
        when(tunnel.getId()).thenReturn(tunnelId);
        when(tunnel.getEventsBox()).thenReturn(eventsBox);
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
        assertFalse(loginSession.offlineIf(otherTunnel));
        assertFalse(loginSession.isOffline());

        // 当前 tunnel 下线
        assertTrue(loginSession.offlineIf(loginTunnel));
        assertTrue(loginSession.isOffline());
        // 下线后再次下线
        assertFalse(loginSession.offlineIf(loginTunnel));
        assertTrue(loginSession.isOffline());

        // 关闭后下线
        loginSession = createSession();
        loginSession.close();
        assertFalse(loginSession.offlineIf(loginTunnel));
        assertFalse(loginSession.isOffline());
    }

    @Test
    public void getSentMessageByToID() {
        NetSession<Long> session = createSession();
        TestMessages messages = new TestMessages(session)
                .addPush("request 1")
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
        NetSession<Long> session = createSession();
        TestMessages messages = new TestMessages(session)
                .addPush("request 1")
                .addResponse("request 2", 1)
                .addResponse("request 3", 1)
                .addResponse("request 4", 1)
                .addResponse("request 5", 1)
                .addResponse("request 6", 1)
                .addRequest("request 7");
        List<Message<Long>> sentMessages = session.getSentMessages(Range.closed(1L, (long) messages.getMessageSize()));
        assertTrue(sentMessages.isEmpty());
        messages.messagesForEach(session::addSentMessage);
        sentMessages = session.getSentMessages(Range.closed(1L, (long) messages.getMessageSize()));
        assertEquals(messages.getMessageSize(), sentMessages.size());
        sentMessages = session.getSentMessages(Range.closed(1L, 2L));
        assertEquals(2, sentMessages.size());
        sentMessages = session.getSentMessages(Range.closed(1L, (long) messages.getMessageSize() + 10));
        assertEquals(messages.getMessageSize(), sentMessages.size());
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
        messages.contentsForEach(c -> verify(tunnel1, times(1)).send(eq(c)));


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
        messages.contentsForEach(c -> verify(tunnel2, times(1)).send(eq(c)));


        // 关闭发送message
        tunnel = mockTunnel();
        session = createSession(tunnel);
        session.close();
        messages = new TestMessages(session)
                .addPush("push")
                .addPush("push")
                .addPush("push");
        NetSession<Long> closeSession = session;
        messages.contentsForEach(c ->
                TestAide.assertRunWithException("close session.send",
                        () -> closeSession.send(c), NetException.class));
        verify(tunnel, never()).send(any(MessageContext.class));

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
        TestAide.assertRunWithoutException("assertLoginOk", () -> {
            session.acceptTunnel(tunnel);
            assertTrue(session.isLogin());
        });
    }

    protected void assertAcceptTunnelException(NetSession<Long> session, NetTunnel<Long> tunnel) {
        TestAide.assertRunWithException("assertLoginException", () -> session.acceptTunnel(tunnel), ValidatorFailException.class);
    }

    protected TestMessages createMessage(NetSession<Long> session) {
        return new TestMessages(session)
                .addPush("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
    }

}