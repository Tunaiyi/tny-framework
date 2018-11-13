package com.tny.game.net.transport;

import com.tny.game.common.concurrent.CoreThreadFactory;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.message.Message;
import com.tny.game.net.endpoint.NetSession;
import com.tny.game.test.TestAide;
import org.junit.*;

import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.test.MockAide.*;
import static com.tny.game.test.MockAide.any;
import static com.tny.game.test.MockAide.doAnswer;
import static com.tny.game.test.MockAide.when;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class NetTunnelTest<T extends NetTunnel<Long>> extends TunnelTest<T> {

    private static ScheduledExecutorService service = Executors.newScheduledThreadPool(1, new CoreThreadFactory("NetSessionTestThread", true));

    @Override
    protected T createUnbindTunnel() {
        return this.createTunnel(createUnLoginCert());
    }

    @Override
    protected T createBindTunnel() {
        T tunnel = this.createTunnel(createLoginCert());
        mockBindLoginSession(tunnel);
        return tunnel;
    }

    protected void mockBindLoginSession(NetTunnel<Long> tunnel) {
        NetSession<Long> session = mockSession(tunnel.getCertificate());
        tunnel.bind(session);
        verify(session, times(1)).getCertificate();
    }

    protected NetSession<Long> mockSession(Certificate<Long> certificate) {
        NetSession<Long> session = mockAs(NetSession.class);
        RespondFutureHolder holder = RespondFutureHolder.getHolder(session);
        // MessageFactory<Long> messageBuilderFactory = new CommonMessageFactory<>();
        when(session.getCertificate()).thenReturn(certificate);
        // doAnswer((mk) -> {
        //     MessageContext<Long> context = as(mk.getArguments()[2]);
        //     if (context != null) {
        //         Message<Long> message = as(mk.getArguments()[1]);
        //         holder.putFuture(message.getId(), context.getRespondFuture());
        //     }
        //     return null;
        // }).when(session).onSend(any(), any(), any());
        doAnswer((mk) -> {
            Message<Long> message = as(mk.getArguments()[1]);
            holder.pollFuture(as(message.getHeader().getToMessage()));
            return null;
        }).when(session).receive(any(), any());
        // MessageIdCreator idCreator = new MessageIdCreator(MessageIdCreator.ENDPOINT_SENDER_MESSAGE_ID_MARK);
        // when(session.createMessage(any())).thenAnswer((mk) ->
        //         messageBuilderFactory.create(idCreator.createId(), as(mk.getArguments()[0]), certificate));
        when(session.getUserId()).thenReturn(certificate.getUserId());
        when(session.isLogin()).thenReturn(certificate.isAutherized());
        when(session.getUserType()).thenReturn(certificate.getUserType());
        return session;
    }


    @Test
    public void authenticate() {
        NetTunnel<Long> tunnel;
        // 正常登录
        tunnel = createUnbindTunnel();
        try {
            tunnel.authenticate(createLoginCert());
            assertTrue(tunnel.isLogin());
        } catch (ValidatorFailException e) {
            LOGGER.error("", e);
            fail("authenticate fail");
        }

        // 重复登录
        try {
            tunnel.authenticate(createLoginCert());
            fail("authenticate success");
        } catch (ValidatorFailException e) {
            assertTrue(true);
        }
        assertTrue(tunnel.isLogin());

        // 接受未认证凭证
        tunnel = createUnbindTunnel();
        try {
            tunnel.authenticate(createUnLoginCert());
            fail("authenticate success");
        } catch (ValidatorFailException e) {
            assertTrue(true);
        }
        assertFalse(tunnel.isLogin());
    }

    @Test
    public void bind() {
        long cert1Id = 100001;
        long cert2Id = 100002;
        NetTunnel<Long> tunnel;
        NetSession<Long> session;
        Certificate<Long> certificate1 = createLoginCert(cert1Id, uid);
        Certificate<Long> certificate2 = createLoginCert(cert2Id, uid);

        tunnel = createTunnel(certificate1);
        session = mockSession(certificate1);
        assertTrue(tunnel.bind(session));
        assertFalse(tunnel.bind(session));

        tunnel = createTunnel(certificate1);
        session = mockSession(certificate2);
        assertFalse(tunnel.bind(session));

        tunnel = createUnbindTunnel();
        session = mockSession(certificate1);
        assertFalse(tunnel.bind(session));

    }

    @Test
    public abstract void ping();

    @Test
    public abstract void pong();

    @Test
    public abstract void send() throws ExecutionException, InterruptedException;

    @Test
    public abstract void receive() throws ExecutionException, InterruptedException;

    // @Test
    // public abstract void write() throws Exception;

    // @Override
    // @Test
    // public void send() {
    //     TestMessages messages;
    //     NetSession<Long> session;
    //     NetTunnel<Long> tunnel;
    //
    //     // 发送 message
    //     tunnel = createLoginTunnel();
    //     eventsBox = tunnel.getEventsBox();
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 1);
    //     messages.send(session);
    //     verify(tunnel, times(messages.getMessageSize())).addOutputEvent(any(MessageSendEvent.class));
    //
    //     // 排除发送
    //     tunnel = createLoginTunnel();
    //     eventsBox = tunnel.getEventsBox();
    //     when(tunnel.isExcludeSendMode(any())).thenReturn(true);
    //     messages = new TestMessages(tunnel)
    //             .addRequest("request")
    //             .addPush("push")
    //             .addResponse("response", 1);
    //     messages.send(session);
    //     when(tunnel.isExcludeSendMode(any())).thenReturn(false);
    //
    //     // 离线发送message
    //     tunnel = createLoginTunnel();
    //     eventsBox = tunnel.getEventsBox();
    //     session.offline();
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addPush("push")
    //             .addPush("push");
    //     messages.send(session);
    //     verify(tunnel, times(messages.getMessageSize())).addOutputEvent(any(MessageSendEvent.class));
    //
    //     // 关闭发送message
    //     tunnel = createLoginTunnel();
    //     eventsBox = tunnel.getEventsBox();
    //     session.close();
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addPush("push")
    //             .addPush("push");
    //     messages.send(session);
    //     verify(tunnel, never()).addOutputEvent(any(MessageSendEvent.class));
    //
    //     // 发送 futureWaitSend
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(MessageContext::willSendFuture);
    //     messages.send(session);
    //     scheduleSendSuccess(messages);
    //     assertWaitSendOk(messages);
    //
    //     // 发送 futureWaitSend Timeout
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(MessageContext::willSendFuture);
    //     messages.send(session);
    //     assertWaitSendException(messages, TimeoutException.class);
    //
    //     // offline 发送 futureWaitSend Timeout
    //     session.offline();
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(MessageContext::willSendFuture);
    //     messages.send(session);
    //     assertWaitSendException(messages, TimeoutException.class);
    //
    //     // close 发送 futureWaitSend Timeout
    //     session.close();
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(MessageContext::willSendFuture);
    //     messages.send(session);
    //     assertWaitSendException(messages, ExecutionException.class);
    //
    //
    //     // 发送 同步 WaitSend
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(content -> content.willWaitForSend(100));
    //     scheduleSendSuccess(messages);
    //     messages.send(session);
    //     assertWaitSendOk(messages);
    //
    //     // 发送 同步 WaitSend Timeout
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(content -> content.willWaitForSend(100));
    //     assertSyncSendException(session, messages, RemotingException.class);
    //
    //     // offline 发送 同步 WaitSend Exception
    //     session.offline();
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(content -> content.willWaitForSend(100));
    //     assertSyncSendException(session, messages, RemotingException.class);
    //
    //     // close 同步 WaitSend Exception
    //     session.close();
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(content -> content.willWaitForSend(100));
    //     assertSyncSendException(session, messages, RemotingException.class);
    //
    //     // 发送 Wait Response
    //     messages = new TestMessages(tunnel)
    //             .addRequest("request 1")
    //             .addRequest("request 2")
    //             .addRequest("request 3");
    //     TestMessages responses = new TestMessages(session);
    //     messages.forEach((content, message) -> {
    //         content.willResponseFuture();
    //         content.sendSuccess(message);
    //         responses.addResponse("response", message.getId());
    //     });
    //     scheduleReceive(session, responses);
    //
    //
    //     messages.send(session);
    //     assertWaitResponseOK(messages);
    //
    //     // 发送 Wait Response Timeout
    //     messages = new TestMessages(tunnel)
    //             .addRequest("request 1")
    //             .addRequest("request 2")
    //             .addRequest("request 3");
    //     messages.forEach((content, message) -> {
    //         content.willResponseFuture();
    //         content.sendSuccess(message);
    //     });
    //     messages.send(session);
    //     assertWaitResponseException(messages, TimeoutException.class);
    //
    //     // 发送 Wait Response  发送失败
    //     messages = new TestMessages(tunnel)
    //             .addRequest("request 1")
    //             .addRequest("request 2")
    //             .addRequest("request 3");
    //     messages.forEach((content, message) -> {
    //         content.willResponseFuture();
    //         content.sendFailed(new SessionException(""));
    //     });
    //     messages.send(session);
    //     assertWaitResponseException(messages, ExecutionException.class);
    // }
    //
    // @Test
    // @Override
    // public void resend() {
    //     ResendMessage<Long> message = ResendMessage.fromTo(1, 7);
    //     NetSession<Long> session;
    //     NetTunnel<Long> tunnel;
    //
    //     // 在线 resend
    //     tunnel = createLoginTunnel();
    //     eventsBox = tunnel.getEventsBox();
    //     session.resend(message);
    //     // assertEquals(1, session.getOutputEventSize());
    //     verify(tunnel, times(1)).addOutputEvent(any(MessageResendEvent.class));
    //
    //     // 离线 resend
    //     tunnel = createLoginTunnel();
    //     eventsBox = tunnel.getEventsBox();
    //     session.offline();
    //     assertResendException(session, message, ExecutionException.class);
    //     verify(tunnel, never()).addOutputEvent(any(MessageResendEvent.class));
    //
    //     // 离线 resend
    //     tunnel = createLoginTunnel();
    //     eventsBox = tunnel.getEventsBox();
    //     session.close();
    //     assertResendException(session, message, ExecutionException.class);
    //     verify(tunnel, never()).addOutputEvent(any(MessageResendEvent.class));
    //
    // }
    //
    //
    // @Test
    // public void write() throws TunnelWriteException {
    //     NetSession<Long> session;
    //     NetTunnel<Long> tunnel;
    //     MessageSendEvent<Long> event;
    //     MessageContext<Long> messageContent;
    //
    //     // 正常写出
    //     tunnel = createLoginTunnel();
    //     eventsBox = tunnel.getEventsBox();
    //     event = mockSendEvent();
    //     // stub
    //     messageContent = new TestMessages(session)
    //             .createPushContent("ok");
    //     when(event.getTunnel()).thenReturn(Optional.of(tunnel));
    //     when(event.getMessageContext()).thenReturn(messageContent);
    //     //verify
    //     assertSendEventOk(session, event);
    //     verify(event, times(1)).getTunnel();
    //     verify(event, times(1)).getMessageContext();
    //     verify(tunnel, times(1)).getMessageBuilderFactory();
    //     verify(tunnel, times(1)).getSession();
    //     verify(tunnel, times(1)).write(as(any(Message.class)), eq(event));
    //     verifyNoMoreInteractions(tunnel, event);
    //
    //     // 写出Tunnel write 异常
    //     tunnel = createLoginTunnel();
    //     eventsBox = tunnel.getEventsBox();
    //     event = mockSendEvent();
    //     // stub
    //     when(event.getTunnel()).thenReturn(Optional.of(tunnel));
    //     when(event.getMessageContext()).thenReturn(messageContent);
    //     doThrow(NullPointerException.class).when(tunnel).write(as(any(Message.class)), eq(event));
    //     //verify
    //     assertSendEventException(session, event, NullPointerException.class);
    //     verify(event, times(1)).getTunnel();
    //     verify(event, times(1)).getMessageContext();
    //     verify(tunnel, times(1)).getMessageBuilderFactory();
    //     verify(tunnel, times(1)).getSession();
    //     verify(tunnel, times(1)).write(as(any(Message.class)), eq(event));
    //     verify(event, times(1)).sendFail(any(NullPointerException.class));
    //     verifyNoMoreInteractions(tunnel, event);
    //
    //     // 写出Tunnel 为空
    //     tunnel = createLoginTunnel();
    //     eventsBox = tunnel.getEventsBox();
    //     event = mockSendEvent();
    //     // stub
    //     when(event.getTunnel()).thenReturn(Optional.empty());
    //     //verify
    //     assertSendEventException(session, event, TunnelException.class);
    //     verify(event, times(1)).getTunnel();
    //     verify(event, times(1)).sendFail(any(TunnelException.class));
    //     verifyNoMoreInteractions(tunnel, event);
    //
    //
    // }
    //


    @Test
    public void getBindEndpoint() {
        T loginTunnel = createBindTunnel();
        assertTrue(loginTunnel.getBindEndpoint().isPresent());
        T unloginTunnel = createUnbindTunnel();
        assertFalse(unloginTunnel.getBindEndpoint().isPresent());
    }

    protected void assertSendOk(TestMessages messages) {
        messages.contextsForEach(content -> TestAide.assertRunComplete("assertWaitSendOk",
                () -> assertNotNull(content.getSendFuture().get(1000, TimeUnit.MILLISECONDS))));
    }

    protected void assertWaitSendException(TestMessages messages, Class<? extends Exception> exceptionClass) {
        messages.contextsForEach(content -> TestAide.assertRunWithException("assertWaitSendException",
                () -> content.getSendFuture().get(100, TimeUnit.MILLISECONDS), exceptionClass));
    }

    protected void assertWaitResponseOK(TestMessages messages) {
        messages.contextsForEach(content -> TestAide.assertRunComplete("assertWaitResponseOK", () ->
                assertNotNull(content.getRespondFuture().get(300, TimeUnit.MILLISECONDS))));
    }

    protected void assertWaitResponseException(TestMessages messages, Class<? extends Exception> exceptionClass) {
        messages.contextsForEach(content -> TestAide.assertRunWithException("assertWaitResponseException",
                () -> content.getRespondFuture().get(300, TimeUnit.MILLISECONDS), exceptionClass));
    }

    protected void doReceive(Tunnel<Long> tunnel, TestMessages responses) {
        responses.messagesForEach(tunnel::receive);
        // return service.schedule(() -> {
        //     responses.messagesForEach(tunnel::receive);
        //     // MessageEventsBox<Long> eventsBox = tunnel.getEventsBox();
        //     // assertEquals(responses.getMessageSize(), eventsBox.getInputEventSize());
        //     // while (eventsBox.isHasInputEvent()) {
        //     //     MessageInputEvent<Long> event = eventsBox.pollInputEvent();
        //     //     if (event instanceof MessageReceiveEvent)
        //     //         ((MessageReceiveEvent<Long>) event).completeResponse();
        //     // }
        // }, 50, TimeUnit.MILLISECONDS);
    }

}