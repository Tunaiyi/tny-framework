/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.transport;

import com.tny.game.net.command.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class NetTunnelTest<T extends NetTunnel<Long>, E extends MockNetEndpoint> extends TunnelTest<T> {

    protected TunnelTestInstance<T, E> create() {
        return create(createLoginCert(), true);
    }

    protected TunnelTestInstance<T, E> create(boolean open) {
        return create(createLoginCert(), open);
    }

    protected abstract TunnelTestInstance<T, E> create(Certificate<Long> certificate, boolean open);

    protected E createEndpoint() {
        return createEndpoint(createLoginCert());
    }

    protected E createEndpoint(Certificate<Long> certificate) {
        return create(certificate, false).getEndpoint();
    }

    @Override
    protected T createTunnel(Certificate<Long> certificate) {
        return create(certificate, true).getTunnel();
    }

    protected T createTunnel(Certificate<Long> certificate, boolean open) {
        return create(certificate, open).getTunnel();
    }

    @Test
    public abstract void ping();

    @Test
    public abstract void pong();

    @Test
    public abstract void send() throws ExecutionException, InterruptedException;

    @Test
    public abstract void receive() throws ExecutionException, InterruptedException;

    @Test
    public void getEndpoint() {
        T loginTunnel = createBindTunnel();
        assertNotNull(loginTunnel.getEndpoint());
        T unloginTunnel = createUnbindTunnel();
        assertNotNull(unloginTunnel.getEndpoint());
    }

    // @Test
    // public abstract void write() throws Exception;

    // @Override
    // @Test
    // public void send() {
    //     TestMessages messages;
    //     NetSession<Long> session;
    //     NetTunnel<Long> tunnel;
    //
    //     // ?????? message
    //     tunnel = createLoginTunnel();
    //     eventsBox = tunnel.getEventsBox();
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 1);
    //     messages.send(session);
    //     verify(tunnel, times(messages.getMessageSize())).addOutputEvent(any(MessageSendEvent.class));
    //
    //     // ????????????
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
    //     // ????????????message
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
    //     // ????????????message
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
    //     // ?????? futureWaitSend
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(MessageContext::willSendFuture);
    //     messages.send(session);
    //     scheduleSendSuccess(messages);
    //     assertWaitSendOk(messages);
    //
    //     // ?????? futureWaitSend Timeout
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(MessageContext::willSendFuture);
    //     messages.send(session);
    //     assertWaitSendException(messages, TimeoutException.class);
    //
    //     // offline ?????? futureWaitSend Timeout
    //     session.offline();
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(MessageContext::willSendFuture);
    //     messages.send(session);
    //     assertWaitSendException(messages, TimeoutException.class);
    //
    //     // close ?????? futureWaitSend Timeout
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
    //     // ?????? ?????? WaitSend
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(content -> content.willWaitForSend(100));
    //     scheduleSendSuccess(messages);
    //     messages.send(session);
    //     assertWaitSendOk(messages);
    //
    //     // ?????? ?????? WaitSend Timeout
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(content -> content.willWaitForSend(100));
    //     assertSyncSendException(session, messages, RemotingException.class);
    //
    //     // offline ?????? ?????? WaitSend Exception
    //     session.offline();
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(content -> content.willWaitForSend(100));
    //     assertSyncSendException(session, messages, RemotingException.class);
    //
    //     // close ?????? WaitSend Exception
    //     session.close();
    //     messages = new TestMessages(tunnel)
    //             .addPush("push")
    //             .addRequest("request")
    //             .addResponse("response", 2);
    //     messages.contentsForEach(content -> content.willWaitForSend(100));
    //     assertSyncSendException(session, messages, RemotingException.class);
    //
    //     // ?????? Wait Response
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
    //     // ?????? Wait Response Timeout
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
    //     // ?????? Wait Response  ????????????
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
    //     // ?????? resend
    //     tunnel = createLoginTunnel();
    //     eventsBox = tunnel.getEventsBox();
    //     session.resend(message);
    //     // assertEquals(1, session.getOutputEventSize());
    //     verify(tunnel, times(1)).addOutputEvent(any(MessageResendEvent.class));
    //
    //     // ?????? resend
    //     tunnel = createLoginTunnel();
    //     eventsBox = tunnel.getEventsBox();
    //     session.offline();
    //     assertResendException(session, message, ExecutionException.class);
    //     verify(tunnel, never()).addOutputEvent(any(MessageResendEvent.class));
    //
    //     // ?????? resend
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
    //     // ????????????
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
    //     verify(tunnel, times(1)).getEndpoint();
    //     verify(tunnel, times(1)).write(as(any(Message.class)), eq(event));
    //     verifyNoMoreInteractions(tunnel, event);
    //
    //     // ??????Tunnel write ??????
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
    //     verify(tunnel, times(1)).getEndpoint();
    //     verify(tunnel, times(1)).write(as(any(Message.class)), eq(event));
    //     verify(event, times(1)).sendFail(any(NullPointerException.class));
    //     verifyNoMoreInteractions(tunnel, event);
    //
    //     // ??????Tunnel ??????
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

    // protected void assertSendOk(TestMessages messages) {
    //     messages.contextsForEach(content -> TestAide.assertRunComplete("assertWaitSendOk",
    //             () -> assertNotNull(content.getWriteMessageFuture().get(1000, TimeUnit.MILLISECONDS))));
    // }
    //
    // protected void assertWaitSendException(TestMessages messages, Class<? extends Exception> exceptionClass) {
    //     messages.contextsForEach(content -> TestAide.assertRunWithException("assertWaitSendException",
    //             () -> content.getWriteMessageFuture().get(100, TimeUnit.MILLISECONDS), exceptionClass));
    // }
    //
    // protected void assertWaitResponseOK(TestMessages messages) {
    //     messages.contextsForEach(content -> TestAide.assertRunComplete("assertWaitResponseOK", () ->
    //             assertNotNull(content.getRespondFuture().get(300, TimeUnit.MILLISECONDS))));
    // }
    //
    // protected void assertWaitResponseException(TestMessages messages, Class<? extends Exception> exceptionClass) {
    //     messages.contextsForEach(content -> TestAide.assertRunWithException("assertWaitResponseException",
    //             () -> content.getRespondFuture().get(300, TimeUnit.MILLISECONDS), exceptionClass));
    // }
    //
    // protected void doReceive(Tunnel<Long> tunnel, TestMessages responses) {
    //     responses.messagesForEach(tunnel::receive);
    //     // return service.schedule(() -> {
    //     //     responses.messagesForEach(tunnel::receive);
    //     //     // MessageEventsBox<Long> eventsBox = tunnel.getEventsBox();
    //     //     // assertEquals(responses.getMessageSize(), eventsBox.getInputEventSize());
    //     //     // while (eventsBox.isHasInputEvent()) {
    //     //     //     MessageInputEvent<Long> event = eventsBox.pollInputEvent();
    //     //     //     if (event instanceof MessageReceiveEvent)
    //     //     //         ((MessageReceiveEvent<Long>) event).completeResponse();
    //     //     // }
    //     // }, 50, TimeUnit.MILLISECONDS);
    // }

}