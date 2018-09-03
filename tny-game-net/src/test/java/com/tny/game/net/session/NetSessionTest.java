package com.tny.game.net.session;

import com.google.common.collect.Range;
import com.tny.game.common.concurrent.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.CommonMessageBuilderFactory;
import com.tny.game.net.session.MessageEvent.SessionEventType;
import com.tny.game.net.tunnel.*;
import org.junit.*;
import org.mockito.*;
import org.slf4j.*;
import test.TestAide;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.tny.game.common.utils.ObjectAide.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static test.MockAide.*;

/**
 * Created by Kun Yang on 2018/8/12.
 */
public abstract class NetSessionTest<S extends NetSession<Long>> extends SessionTest<S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetSessionTest.class);


    private static final AtomicLong TUNNEL_ID_CREATOR = new AtomicLong();



    protected CommonMessageBuilderFactory<Long> messageBuilderFactory = new CommonMessageBuilderFactory<>();

    private static ScheduledExecutorService service = Executors.newScheduledThreadPool(1, new CoreThreadFactory("NetSessionTestThread", true));

    protected NetSessionTest() {
    }

    @Override
    protected S createUnloginSession() {
        MessageEventsBox<Long> eventsBox = mockAs(MessageEventsBox.class);
        long tunnelId = TUNNEL_ID_CREATOR.incrementAndGet();
        NetTunnel<Long> tunnel = mockAs(NetTunnel.class);
        when(tunnel.getId()).thenReturn(tunnelId);
        when(tunnel.getEventsBox()).thenReturn(eventsBox);
        return createUnloginSession(tunnel);
    }

    protected abstract S createUnloginSession(NetTunnel<Long> tunnel);

    @Override
    protected S createLoginSession() {
        return createLoginSession(certificateId, uid);
    }

    protected S createLoginSession(long certificateId, Long uid) {
        S session = createUnloginSession();
        try {
            session.login(createLoginCert(certificateId, uid));
            NetTunnel<Long> tunnel = mockNetTunnel(session);
            when(tunnel.getCertificate()).thenReturn(session.getCertificate());
        } catch (ValidatorFailException e) {
            fail("login fail");
        }
        return session;
    }

    protected S createLoginSession(NetCertificate<Long> certificate) {
        S session = createUnloginSession();
        try {
            session.login(certificate);
        } catch (ValidatorFailException e) {
            fail("login fail");
        }
        return session;
    }

    @Override
    protected void doOffline(S session) {
        session.offline();
    }

    @Test
    public void offline() {
        NetSession<Long> loginSession = createLoginSession();
        NetSession<Long> unloginSession = createUnloginSession();
        assertFalse(loginSession.isOffline());
        assertFalse(unloginSession.isOffline());
        loginSession.offline();
        unloginSession.offline();
        assertTrue(loginSession.isOffline());
        assertTrue(unloginSession.isOffline());
    }

    @Test
    public void offlineIfCurrent() {
        NetTunnel<Long> otherTunnel = mockAs(NetTunnel.class);
        NetSession<Long> loginSession = createLoginSession();
        NetSession<Long> unloginSession = createUnloginSession();
        assertFalse(loginSession.isOffline());
        assertFalse(unloginSession.isOffline());
        // 其他 otherTunnel 下线
        assertFalse(loginSession.offlineIfCurrent(otherTunnel));
        assertFalse(unloginSession.offlineIfCurrent(otherTunnel));
        assertFalse(loginSession.isOffline());
        assertFalse(unloginSession.isOffline());

        // 当前 tunnel 下线
        assertTrue(loginSession.offlineIfCurrent(mockNetTunnel(loginSession)));
        assertTrue(unloginSession.offlineIfCurrent(mockNetTunnel(unloginSession)));
        assertTrue(loginSession.isOffline());
        assertTrue(unloginSession.isOffline());
        // 下线后再次下线
        assertFalse(loginSession.offlineIfCurrent(mockNetTunnel(loginSession)));
        assertFalse(unloginSession.offlineIfCurrent(mockNetTunnel(unloginSession)));
        assertTrue(loginSession.isOffline());
        assertTrue(unloginSession.isOffline());

        // 关闭后下线
        loginSession = createLoginSession();
        unloginSession = createUnloginSession();
        loginSession.close();
        unloginSession.close();
        assertFalse(loginSession.offlineIfCurrent(mockNetTunnel(loginSession)));
        assertFalse(unloginSession.offlineIfCurrent(mockNetTunnel(unloginSession)));
        assertFalse(loginSession.isOffline());
        assertFalse(unloginSession.isOffline());
    }

    @Test
    public void getSentMessageByToID() {
        NetSession<Long> session = createUnloginSession();
        TestMessages messages = new TestMessages(session)
                .addPush("request 1")
                .addResponse("request 2", 1)
                .addResponse("request 3", 1)
                .addResponse("request 4", 1)
                .addResponse("request 5", 1)
                .addResponse("request 6", 1)
                .addRequest("request 7");
        messages.messagesForEach(m -> assertNull(session.getSentMessageByToID(m.getId())));
        sendAndProcessOutput(messages, session);
        messages.messagesForEach(m -> assertNotNull(session.getSentMessageByToID(m.getId())));
    }

    @Test
    public void getSentMessages() {
        NetSession<Long> session = createUnloginSession();
        TestMessages messages = new TestMessages(session)
                .addPush("request 1")
                .addResponse("request 2", 1)
                .addResponse("request 3", 1)
                .addResponse("request 4", 1)
                .addResponse("request 5", 1)
                .addResponse("request 6", 1)
                .addRequest("request 7");
        List<Message<Long>> sentMessages = session.getSentMessages(Range.closed(1, messages.getMessageSize()));
        assertTrue(sentMessages.isEmpty());
        sendAndProcessOutput(messages, session);
        sentMessages = session.getSentMessages(Range.closed(1, messages.getMessageSize()));
        assertEquals(messages.getMessageSize(), sentMessages.size());
        sentMessages = session.getSentMessages(Range.closed(1, 2));
        assertEquals(2, sentMessages.size());
        sentMessages = session.getSentMessages(Range.closed(1, messages.getMessageSize() + 10));
        assertEquals(messages.getMessageSize(), sentMessages.size());
    }

    @Test
    public void login() {
        // 正常登录
        NetSession<Long> sessionLoginOk = createUnloginSession();
        assertFalse(sessionLoginOk.isLogin());
        assertLoginOk(sessionLoginOk, createLoginCert());

        // 重复登录
        assertLoginException(sessionLoginOk, createLoginCert());

        // 用未登录授权登录
        NetSession<Long> sessionLoginWithUnLoginCert = createUnloginSession();
        assertLoginException(sessionLoginWithUnLoginCert, createUnLoginCert());

        // session 离线登录
        NetSession<Long> sessionOffline = createUnloginSession();
        sessionOffline.offline();
        assertLoginException(sessionOffline, createLoginCert());

        // session 关闭登录
        NetSession<Long> sessionClose = createUnloginSession();
        sessionClose.close();
        assertLoginException(sessionClose, createLoginCert());
    }

    @Test
    public void relogin() {
        TestMessages reloginMessages;
        // session
        NetSession<Long> session = createLoginSession();

        // 重登 session
        NetSession<Long> reloginSession = createLoginSession();
        reloginMessages = createMessage(reloginSession);
        reloginMessages.receive(reloginSession);
        assertReloginOk(session, reloginSession.getCurrentTunnel());

        // session 再次重登
        reloginSession = createLoginSession();
        reloginMessages = createMessage(reloginSession);
        reloginMessages.receive(reloginSession);
        assertReloginOk(session, reloginSession.getCurrentTunnel());

        // session 同个 sesion
        session = createLoginSession();
        assertReloginOk(session, session.getCurrentTunnel());

        // session Unlogin 重登
        session = createUnloginSession();
        reloginSession = createLoginSession();
        reloginMessages = createMessage(reloginSession);
        reloginMessages.receive(reloginSession);
        assertReloginException(session, reloginSession.getCurrentTunnel());

        // session 重登 reloginSession Unlogin
        session = createUnloginSession();
        reloginSession = createUnloginSession();
        reloginMessages = createMessage(reloginSession);
        reloginMessages.receive(reloginSession);
        assertReloginException(session, reloginSession.getCurrentTunnel());

        // session Offline 重登
        session = createLoginSession();
        session.offline();
        reloginSession = createLoginSession();
        reloginMessages = createMessage(reloginSession);
        reloginMessages.receive(reloginSession);
        assertReloginOk(session, reloginSession.getCurrentTunnel());

        // session close 重登
        session = createLoginSession();
        session.close();
        reloginSession = createLoginSession();
        reloginMessages = createMessage(reloginSession);
        reloginMessages.receive(reloginSession);
        assertReloginException(session, reloginSession.getCurrentTunnel());

        // session 不一致 uid
        session = createLoginSession();
        reloginSession = createLoginSession(certificateId, uid + 10);
        reloginMessages = createMessage(reloginSession);
        reloginMessages.receive(reloginSession);
        assertReloginException(session, reloginSession.getCurrentTunnel());

        // session 不一致 certificateId
        session = createLoginSession();
        reloginSession = createLoginSession(certificateId + 10, uid);
        reloginMessages = createMessage(reloginSession);
        reloginMessages.receive(reloginSession);
        assertReloginException(session, reloginSession.getCurrentTunnel());

    }

    @Override
    @Test
    public void receive() {
        TestMessages messages;
        NetSession<Long> session;
        NetTunnel<Long> tunnel;

        // 接受Message
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.receive(session);
        verify(tunnel, times(messages.getMessageSize())).addInputEvent(any(MessageReceiveEvent.class));

        // 接受ping
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        messages = new TestMessages(session)
                .addPing()
                .addPing()
                .addPing();
        messages.receive(session);
        verify(tunnel, times(messages.getPingSize())).pong();
        verify(tunnel, times(messages.getPingSize())).isReceiveExclude(MessageMode.PING);
        verifyNoMoreInteractions(tunnel);

        // 接受pong
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        messages = new TestMessages(session)
                .addPong()
                .addPong()
                .addPong();
        messages.receive(session);
        verify(tunnel, times(messages.getPongSize())).isReceiveExclude(MessageMode.PONG);
        verify(tunnel, never()).pong();
        verifyNoMoreInteractions(tunnel);

        // 排除接受
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        messages = new TestMessages(session)
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        when(tunnel.isReceiveExclude(any())).thenReturn(true);
        messages.receive(session);
        verify(tunnel, times(messages.getMessageSize())).isReceiveExclude(any(MessageMode.class));
        verify(tunnel, times(messages.getMessageSize())).getSession();
        // request 有返回
        verify(tunnel, times(1)).isSendExclude(MessageMode.RESPONSE);
        verify(tunnel, times(1)).addOutputEvent(any(MessageOutputEvent.class));
        verifyNoMoreInteractions(tunnel);

        // 排除接受
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addRequest("request")
                .addRequest("request")
                .addResponse("response", 1);
        when(tunnel.isReceiveExclude(any())).thenReturn(true);
        messages.receive(session);
        verify(tunnel, times(messages.getMessageSize())).isReceiveExclude(any(MessageMode.class));
        verify(tunnel, times(messages.getMessageSize())).getSession();
        // request 有返回
        verify(tunnel, times(messages.getRequestSize())).isSendExclude(MessageMode.RESPONSE);
        verify(tunnel, times(messages.getRequestSize())).addOutputEvent(any(MessageSendEvent.class));
        verifyNoMoreInteractions(tunnel);

        // 离线接受message
        session = createLoginSession();
        session.offline();
        tunnel = mockNetTunnel(session);
        messages = new TestMessages(session)
                .addPush("push")
                .addPush("push")
                .addPush("push");
        messages.receive(session);
        verify(tunnel, times(messages.getMessageSize())).addInputEvent(any());

        // 关闭接受message
        session = createLoginSession();
        session.close();
        tunnel = mockNetTunnel(session);
        messages = new TestMessages(session)
                .addPush("push")
                .addPush("push")
                .addPush("push");
        messages.receive(session);
        verify(tunnel, never()).addInputEvent(any());

    }


    @Override
    @Test
    public void send() {
        TestMessages messages;
        NetSession<Long> session;
        NetTunnel<Long> tunnel;

        // 发送 message
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.send(session);
        verify(tunnel, times(messages.getMessageSize())).addOutputEvent(any(MessageSendEvent.class));

        // 排除发送
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        when(tunnel.isSendExclude(any())).thenReturn(true);
        messages = new TestMessages(session)
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        messages.send(session);
        when(tunnel.isSendExclude(any())).thenReturn(false);

        // 离线发送message
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        session.offline();
        messages = new TestMessages(session)
                .addPush("push")
                .addPush("push")
                .addPush("push");
        messages.send(session);
        verify(tunnel, times(messages.getMessageSize())).addOutputEvent(any(MessageSendEvent.class));

        // 关闭发送message
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        session.close();
        messages = new TestMessages(session)
                .addPush("push")
                .addPush("push")
                .addPush("push");
        messages.send(session);
        verify(tunnel, never()).addOutputEvent(any(MessageSendEvent.class));

        // 发送 futureWaitSend
        session = createLoginSession();
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        messages.contentsForEach(MessageContent::sendFuture);
        messages.send(session);
        scheduleSendSuccess(messages);
        assertWaitSendOk(messages);

        // 发送 futureWaitSend Timeout
        session = createLoginSession();
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        messages.contentsForEach(MessageContent::sendFuture);
        messages.send(session);
        assertWaitSendException(messages, TimeoutException.class);

        // offline 发送 futureWaitSend Timeout
        session = createLoginSession();
        session.offline();
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        messages.contentsForEach(MessageContent::sendFuture);
        messages.send(session);
        assertWaitSendException(messages, TimeoutException.class);

        // close 发送 futureWaitSend Timeout
        session = createLoginSession();
        session.close();
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        messages.contentsForEach(MessageContent::sendFuture);
        messages.send(session);
        assertWaitSendException(messages, ExecutionException.class);


        // 发送 同步 WaitSend
        session = createLoginSession();
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        messages.contentsForEach(content -> content.waitForSent(100));
        scheduleSendSuccess(messages);
        messages.send(session);
        assertWaitSendOk(messages);

        // 发送 同步 WaitSend Timeout
        session = createLoginSession();
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        messages.contentsForEach(content -> content.waitForSent(100));
        assertSyncSendException(session, messages, RemotingException.class);

        // offline 发送 同步 WaitSend Exception
        session = createLoginSession();
        session.offline();
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        messages.contentsForEach(content -> content.waitForSent(100));
        assertSyncSendException(session, messages, RemotingException.class);

        // close 同步 WaitSend Exception
        session = createLoginSession();
        session.close();
        messages = new TestMessages(session)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        messages.contentsForEach(content -> content.waitForSent(100));
        assertSyncSendException(session, messages, RemotingException.class);

        // 发送 Wait Response
        session = createLoginSession();
        messages = new TestMessages(session)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        TestMessages responses = new TestMessages(session);
        messages.forEach((content, message) -> {
            content.messageFuture();
            content.sendSuccess(message);
            responses.addResponse("response", message.getId());
        });
        scheduleReceive(session, responses);


        messages.send(session);
        assertWaitResponseOK(messages);

        // 发送 Wait Response Timeout
        session = createLoginSession();
        messages = new TestMessages(session)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        messages.forEach((content, message) -> {
            content.messageFuture();
            content.sendSuccess(message);
        });
        messages.send(session);
        assertWaitResponseException(messages, TimeoutException.class);

        // 发送 Wait Response  发送失败
        session = createLoginSession();
        messages = new TestMessages(session)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        messages.forEach((content, message) -> {
            content.messageFuture();
            content.sendFailed(new SessionException(""));
        });
        messages.send(session);
        assertWaitResponseException(messages, ExecutionException.class);
    }

    @Test
    @Override
    public void resend() {
        ResendMessage<Long> message = ResendMessage.fromTo(1, 7);
        NetSession<Long> session;
        NetTunnel<Long> tunnel;

        // 在线 resend
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        session.resend(message);
        // assertEquals(1, session.getOutputEventSize());
        verify(tunnel, times(1)).addOutputEvent(any(MessageResendEvent.class));

        // 离线 resend
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        session.offline();
        assertResendException(session, message, ExecutionException.class);
        verify(tunnel, never()).addOutputEvent(any(MessageResendEvent.class));

        // 离线 resend
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        session.close();
        assertResendException(session, message, ExecutionException.class);
        verify(tunnel, never()).addOutputEvent(any(MessageResendEvent.class));

    }


    @Test
    public void write() throws TunnelWriteException {
        NetSession<Long> session;
        NetTunnel<Long> tunnel;
        MessageSendEvent<Long> event;
        MessageContent<Long> messageContent;

        // 正常写出
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        event = mockSendEvent();
        // stub
        messageContent = new TestMessages(session)
                .createPushContent("ok");
        when(event.getTunnel()).thenReturn(Optional.of(tunnel));
        when(event.getContent()).thenReturn(messageContent);
        //verify
        assertSendEventOk(session, event);
        verify(event, times(1)).getTunnel();
        verify(event, times(1)).getContent();
        verify(tunnel, times(1)).getMessageBuilderFactory();
        verify(tunnel, times(1)).getSession();
        verify(tunnel, times(1)).write(as(any(Message.class)), eq(event));
        verifyNoMoreInteractions(tunnel, event);

        // 写出Tunnel write 异常
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        event = mockSendEvent();
        // stub
        when(event.getTunnel()).thenReturn(Optional.of(tunnel));
        when(event.getContent()).thenReturn(messageContent);
        doThrow(NullPointerException.class).when(tunnel).write(as(any(Message.class)), eq(event));
        //verify
        assertSendEventException(session, event, NullPointerException.class);
        verify(event, times(1)).getTunnel();
        verify(event, times(1)).getContent();
        verify(tunnel, times(1)).getMessageBuilderFactory();
        verify(tunnel, times(1)).getSession();
        verify(tunnel, times(1)).write(as(any(Message.class)), eq(event));
        verify(event, times(1)).sendFail(any(NullPointerException.class));
        verifyNoMoreInteractions(tunnel, event);

        // 写出Tunnel 为空
        session = createLoginSession();
        tunnel = mockNetTunnel(session);
        event = mockSendEvent();
        // stub
        when(event.getTunnel()).thenReturn(Optional.empty());
        //verify
        assertSendEventException(session, event, TunnelException.class);
        verify(event, times(1)).getTunnel();
        verify(event, times(1)).sendFail(any(TunnelException.class));
        verifyNoMoreInteractions(tunnel, event);


    }


    private void assertSendEventOk(NetSession<Long> session, MessageSendEvent<Long> event) {
        TestAide.assertRunWithoutException("assertSendEventOk", () -> session.write(event));
    }

    private void assertSendEventException(NetSession<Long> session, MessageSendEvent<Long> event, Class<? extends Exception> exceptionClass) {
        try {
            session.write(event);
            fail("write success");
        } catch (Exception e) {
            if (exceptionClass.isInstance(e)) {
                assertTrue(true);
            } else {
                LOGGER.error("", e);
                fail("wait other exception");
            }
        }
    }

    protected NetTunnel<Long> mockNetTunnel(NetSession<Long> session) {
        return session.getCurrentTunnel();
    }


    private MessageSendEvent<Long> mockSendEvent() {
        return as(Mockito.mock(MessageSendEvent.class));
    }

    private void scheduleReceive(NetSession<Long> session, TestMessages responses) {
        service.schedule(() -> {
            @SuppressWarnings("unchecked")
            ArgumentCaptor<MessageReceiveEvent<Long>> receiveEvent = captorAs(MessageReceiveEvent.class);
            NetTunnel<Long> tunnel = mockNetTunnel(session);
            responses.messagesForEach(session::receive);
            verify(tunnel, times(responses.getMessageSize())).addInputEvent(receiveEvent.capture());
            List<MessageReceiveEvent<Long>> events = receiveEvent.getAllValues();
            for (MessageReceiveEvent<Long> event : events) {
                event.completeResponse();
            }
        }, 50, TimeUnit.MILLISECONDS);
    }

    private void scheduleSendSuccess(TestMessages responses) {
        service.schedule(() -> responses.forEach(MessageContent::sendSuccess), 50, TimeUnit.MILLISECONDS);
    }

    private void sendAndProcessOutput(TestMessages messages, NetSession<Long> session) {
        ArgumentCaptor<MessageOutputEvent<Long>> outputEvent = captorAs(MessageOutputEvent.class);
        NetTunnel<Long> tunnel = mockNetTunnel(session);
        messages.contentsForEach(session::send);
        verify(tunnel, times(messages.getMessageSize())).addOutputEvent(outputEvent.capture());
        List<MessageOutputEvent<Long>> events = outputEvent.getAllValues();
        for (MessageOutputEvent<Long> event : events) {
            if (event.getEventType() == SessionEventType.MESSAGE) {
                MessageSendEvent<Long> sendEvent = (MessageSendEvent<Long>) event;
                TestAide.assertRunWithoutException("processOutput", () -> session.write(sendEvent));
            }
        }
    }

    private void scheduleSendSuccess(TestMessages messages, NetSession<Long> session) {
        service.schedule(() -> sendAndProcessOutput(messages, session), 50, TimeUnit.MILLISECONDS);
    }


    private void assertWaitSendOk(TestMessages messages) {
        messages.contentsForEach(content -> TestAide.assertRunWithoutException("assertWaitSendOk",
                () -> assertNotNull(content.sendFuture().get())));
    }

    private void assertSyncSendException(NetSession<Long> session, TestMessages messages, Class<? extends Exception> exceptionClass) {
        TestAide.assertRunWithException("assertSyncSendException",
                () -> messages.send(session), exceptionClass);
    }

    private void assertWaitSendException(TestMessages messages, Class<? extends Exception> exceptionClass) {
        messages.contentsForEach(content -> TestAide.assertRunWithException("assertWaitSendException",
                () -> content.sendFuture().get(100, TimeUnit.MILLISECONDS), exceptionClass));
    }

    private void assertResendException(NetSession<Long> session, ResendMessage<Long> messages, Class<? extends Exception> exceptionClass) {
        TestAide.assertRunWithException("assertResendException", () -> {
            StageableFuture<ResendResult<Long>> future = messages.sendFuture();
            session.resend(messages);
            future.get(300, TimeUnit.MILLISECONDS);
        }, exceptionClass);
    }

    private void assertWaitResponseOK(TestMessages messages) {
        messages.contentsForEach(content -> TestAide.assertRunWithoutException("assertWaitResponseOK", () ->
                assertNotNull(content.messageFuture().get(300, TimeUnit.MILLISECONDS))));
    }

    protected void assertWaitResponseException(TestMessages messages, Class<? extends Exception> exceptionClass) {
        messages.contentsForEach(content -> TestAide.assertRunWithException("assertWaitResponseException",
                () -> content.messageFuture().get(300, TimeUnit.MILLISECONDS), exceptionClass));
    }

    protected void assertReloginOk(NetSession<Long> session, NetTunnel<Long> newTunnel) {
        TestAide.assertRunWithoutException("assertReloginOk", () -> session.acceptTunnel(newTunnel));
    }


    protected void assertReloginException(NetSession<Long> session, NetTunnel<Long> newTunne) {
        TestAide.assertRunWithException("assertReloginException",
                () -> session.acceptTunnel(newTunne), ValidatorFailException.class);
    }

    protected void assertLoginOk(NetSession<Long> session, NetCertificate<Long> certificate) {
        TestAide.assertRunWithoutException("assertLoginOk", () -> {
            session.login(certificate);
            assertTrue(session.isLogin());
        });
    }

    protected void assertLoginException(NetSession<Long> session, NetCertificate<Long> certificate) {
        TestAide.assertRunWithException("assertLoginException", () -> session.login(certificate), ValidatorFailException.class);
    }

    protected TestMessages createMessage(NetSession<Long> session) {
        return new TestMessages(session)
                .addPush("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
    }

    // @Test
    // public void pollInputEvent() {
    //     NetSession<Long> session = createUnloginSession();
    //     assertNull(session.pollInputEvent());
    //     TestMessages messages = new TestMessages(session)
    //             .addPush("request 1")
    //             .addResponse("request 2", 1)
    //             .addResponse("request 3", 1)
    //             .addResponse("request 4", 1)
    //             .addResponse("request 5", 1)
    //             .addResponse("request 6", 1)
    //             .addRequest("request 7");
    //     messages.receive(session);
    //     assertTrue(session.isHasInputEvent());
    //     messages.messagesForEach(m -> assertNotNull(session.pollInputEvent()));
    //     assertNull(session.pollInputEvent());
    // }
    //
    // @Test
    // public void pollOutputEvent() {
    //     NetSession<Long> session = createUnloginSession();
    //     assertNull(session.pollOutputEvent());
    //     TestMessages messages = new TestMessages(session)
    //             .addPush("request 1")
    //             .addResponse("request 2", 1)
    //             .addResponse("request 3", 1)
    //             .addResponse("request 4", 1)
    //             .addResponse("request 5", 1)
    //             .addResponse("request 6", 1)
    //             .addRequest("request 7");
    //     messages.send(session);
    //     assertTrue(session.isHasOutputEvent());
    //     messages.messagesForEach(m -> assertNotNull(session.pollOutputEvent()));
    //     assertNull(session.pollOutputEvent());
    // }
    //
    // @Test
    // public void hasInputEvent() {
    //     NetSession<Long> session = createUnloginSession();
    //     assertFalse(session.isHasInputEvent());
    //     assertEquals(0, session.getInputEventSize());
    //     TestMessages messages = new TestMessages(session)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2)
    //             .addPing()
    //             .addPing()
    //             .addPing()
    //             .addPong()
    //             .addPong();
    //     messages.receive(session);
    //     assertTrue(session.isHasInputEvent());
    //     assertEquals(messages.getMessageSize(), session.getInputEventSize());
    // }
    //
    // @Test
    // public void hasOutputEvent() {
    //     NetSession<Long> session = createUnloginSession();
    //     assertFalse(session.isHasOutputEvent());
    //     assertEquals(0, session.getOutputEventSize());
    //     TestMessages messages = new TestMessages(session)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.send(session);
    //     assertTrue(session.isHasOutputEvent());
    //     assertEquals(messages.getMessageSize(), session.getOutputEventSize());
    // }

}