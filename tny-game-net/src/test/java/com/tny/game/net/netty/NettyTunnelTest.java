package com.tny.game.net.netty;

import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.*;
import com.tny.game.net.transport.message.common.CommonMessageFactory;
import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.*;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import test.*;

import java.util.concurrent.*;
import java.util.function.Consumer;

import static com.tny.game.common.utils.ObjectAide.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static test.MockAide.any;
import static test.MockAide.anyLong;
import static test.MockAide.*;
import static test.MockAide.eq;
import static test.MockAide.never;
import static test.TestAide.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class NettyTunnelTest<T extends NettyTunnel<Long>> extends NetTunnelTest<T> {

    protected abstract T newTunnel(Certificate<Long> certificate);

    @Override
    protected T createTunnel(Certificate<Long> certificate) {
        MessageHandler<Long> messageHandler = mockAs(MessageHandler.class);
        // MessageFactory<Long> messageFactory = mockAs(MessageFactory.class);
        T tunnel = newTunnel(certificate);
        tunnel.setMessageHandler(messageHandler)
                .setMessageHandler(messageHandler)
                .setMessageFactory(new CommonMessageFactory<>());
        return tunnel;
    }

    @Override
    @Test
    public void remoteAddress() {
        T tunnel = createLoginTunnel();
        assertNotNull(tunnel.remoteAddress());
    }

    @Override
    @Test
    public void localAddress() {
        T tunnel = createLoginTunnel();
        assertNotNull(tunnel.localAddress());
    }

    @Override
    @Test
    public void isClosed() {
        T tunnel = createLoginTunnel();
        assertFalse(tunnel.isClosed());
        Channel channel = mockTunnelChannel(tunnel);
        when(channel.isActive()).thenReturn(false);
        assertTrue(tunnel.isClosed());
    }

    @Test
    public void close() throws Exception {
        T tunnel = createLoginTunnel();
        Channel channel = mockTunnelChannel(tunnel);
        ChannelPromise promise = mock(ChannelPromise.class);
        Future<Void> closeFuture = mockAs(Future.class);
        ArgumentCaptor<GenericFutureListener<Future<Void>>> listener = captorAs(GenericFutureListener.class);

        when(channel.close()).thenReturn(promise);
        when(closeFuture.isSuccess()).thenReturn(true);

        tunnel.close();

        verify(channel, times(1)).isActive();
        verify(channel, times(1)).close();
        verify(promise, times(1)).addListener(listener.capture());

        listener.getValue().operationComplete(closeFuture);
        verifyNoMoreInteractions(channel, promise);
    }

    @Override
    @Test
    public void ping() {
        testPingPong(1, T::ping, MessageMode.PING);
    }

    @Override
    @Test
    public void pong() {
        testPingPong(1, T::pong, MessageMode.PONG);
    }

    @Override
    @Test
    public void receive() {
        TestMessages messages;
        MessageHandler<Long> messageHandler;
        ArgumentCaptor<NetMessage<Long>> messageCaptor;
        T tunnel;
        // 接受Message
        tunnel = createLoginTunnel();
        messageHandler = tunnel.getMessageHandler();
        messageCaptor = MockAide.captorAs(NetMessage.class);
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.receive(tunnel);
        verify(messageHandler, times(messages.getMessageSize())).handle(eq(tunnel), messageCaptor.capture());
        assertTrue(CollectionUtils.containsAll(messages.getMessages(), messageCaptor.getAllValues()));
        verifyNoMoreInteractions(messageHandler);

        // 接受ping
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addPing()
                .addPing()
                .addPing();
        messages.receive(tunnel);
        assertPingPong(messages.getPingSize(), tunnel, MessageMode.PONG);

        // 接受pong
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addPong()
                .addPong()
                .addPong();
        messages.receive(tunnel);
        assertPingPong(0, tunnel, MessageMode.PONG);

        // 排除接受 all
        tunnel = createLoginTunnel();
        messageHandler = tunnel.getMessageHandler();
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addRequest("request")
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeReceiveModes(MessageMode.values());
        messages.receive(tunnel);
        verify(messageHandler, never()).handle(any(), any());
        verifyNoMoreInteractions(messageHandler);


        // 排除接受 REQUEST
        tunnel = createLoginTunnel();
        messageHandler = tunnel.getMessageHandler();
        messageCaptor = MockAide.captorAs(NetMessage.class);
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addRequest("request")
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeReceiveModes(MessageMode.REQUEST);
        messages.receive(tunnel);
        verify(messageHandler, times(messages.getPushSize() + messages.getResponseSize())).handle(eq(tunnel), messageCaptor.capture());
        assertTrue(CollectionUtils.containsAll(messages.getMessages(MessageMode.PUSH, MessageMode.RESPONSE), messageCaptor.getAllValues()));
        verifyNoMoreInteractions(messageHandler);


        // 排除接受 PUSH
        tunnel = createLoginTunnel();
        messageHandler = tunnel.getMessageHandler();
        messageCaptor = MockAide.captorAs(NetMessage.class);
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeReceiveModes(MessageMode.PUSH);
        messages.receive(tunnel);
        verify(messageHandler, times(messages.getRequestSize() + messages.getResponseSize())).handle(eq(tunnel), messageCaptor.capture());
        assertTrue(CollectionUtils.containsAll(messages.getMessages(MessageMode.REQUEST, MessageMode.RESPONSE), messageCaptor.getAllValues()));
        verifyNoMoreInteractions(messageHandler);

        // 排除接受 RESPONSE
        tunnel = createLoginTunnel();
        messageHandler = tunnel.getMessageHandler();
        messageCaptor = MockAide.captorAs(NetMessage.class);
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeReceiveModes(MessageMode.RESPONSE);
        messages.receive(tunnel);
        verify(messageHandler, times(messages.getRequestSize() + messages.getPushSize())).handle(eq(tunnel), messageCaptor.capture());
        assertTrue(CollectionUtils.containsAll(messages.getMessages(MessageMode.REQUEST, MessageMode.PUSH), messageCaptor.getAllValues()));
        verifyNoMoreInteractions(messageHandler);

    }

    @Override
    @Test
    public void send() throws ExecutionException, InterruptedException {
        T tunnel;
        TestMessages messages;
        NetSession<Long> session;
        Channel channel;
        ArgumentCaptor<NetMessage<Long>> messageCaptor;
        int times;

        // 发送Message
        tunnel = createLoginTunnel();
        channel = tunnel.getChannel();
        session = tunnel.getBindSession().orElse(null);
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.send(tunnel);
        times = messages.getMessageSize();
        verify(channel, times(times)).writeAndFlush(any());
        verify(session, times(times)).addSentMessage(any(NetMessage.class));
        verify(session, times(times)).createMessageId();
        verifyNoMoreInteractions(channel, session);

        // 排除接受 all
        tunnel = createLoginTunnel();
        channel = tunnel.getChannel();
        session = tunnel.getBindSession().orElse(null);
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addRequest("request")
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeSendModes(MessageMode.values());
        messages.send(tunnel);
        verify(channel, never()).writeAndFlush(any());
        verify(session, never()).addSentMessage(any(NetMessage.class));
        verify(session, never()).createMessageId();
        verifyNoMoreInteractions(channel, session);

        // 排除接受 REQUEST
        tunnel = createLoginTunnel();
        channel = tunnel.getChannel();
        session = tunnel.getBindSession().orElse(null);
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addRequest("request")
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeSendModes(MessageMode.REQUEST);
        times = messages.getPushSize() + messages.getResponseSize();
        messages.send(tunnel);
        verify(channel, times(times)).writeAndFlush(any());
        verify(session, times(times)).addSentMessage(any(NetMessage.class));
        verify(session, times(times)).createMessageId();
        verifyNoMoreInteractions(channel, session);

        // 排除接受 PUSH
        tunnel = createLoginTunnel();
        channel = tunnel.getChannel();
        session = tunnel.getBindSession().orElse(null);
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeSendModes(MessageMode.PUSH);
        times = messages.getRequestSize() + messages.getResponseSize();
        messages.send(tunnel);
        verify(channel, times(times)).writeAndFlush(any());
        verify(session, times(times)).addSentMessage(any(NetMessage.class));
        verify(session, times(times)).createMessageId();
        verifyNoMoreInteractions(channel, session);

        // 排除接受 RESPONSE
        tunnel = createLoginTunnel();
        channel = tunnel.getChannel();
        session = tunnel.getBindSession().orElse(null);
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeSendModes(MessageMode.RESPONSE);
        times = messages.getRequestSize() + messages.getPushSize();
        messages.send(tunnel);
        verify(channel, times(times)).writeAndFlush(any());
        verify(session, times(times)).addSentMessage(any(NetMessage.class));
        verify(session, times(times)).createMessageId();
        verifyNoMoreInteractions(channel, session);

        // 发送 futureWaitSend
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        messages.contentsForEach(MessageContext::willSendFuture);
        messages.contentsForEach(c ->
                TestAide.assertRunComplete("get send result", () -> assertFalse(c.getSendFuture().isDone())));
        mockChannelWrite(tunnel.getChannel());
        messages.send(tunnel);
        assertSendOk(messages);

        // 发送 futureWaitSend Timeout
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        messages.contentsForEach(MessageContext::willSendFuture);
        messages.send(tunnel);
        assertWaitSendException(messages, TimeoutException.class);

        // 发送 futureWaitSend
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        messages.contentsForEach(MessageContext::willSendFuture);
        messages.contentsForEach(c ->
                TestAide.assertRunComplete("get send result", () -> assertFalse(c.getSendFuture().isDone())));
        mockChannelWrite(tunnel.getChannel());
        messages.send(tunnel);
        assertSendOk(messages);

        // 发送 waitForSend
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        mockChannelWrite(tunnel.getChannel());
        NetTunnel<Long> waitForSendTunnel = tunnel;
        messages.subjcetForEach(c -> assertRunComplete(() -> waitForSendTunnel.sendSync(c, 1000L)));
        verify(channel, times(times)).writeAndFlush(any());
        verify(session, times(times)).addSentMessage(any(NetMessage.class));
        verify(session, times(times)).createMessageId();
        verifyNoMoreInteractions(channel, session);

        // 发送 waitForSend Timeout
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel).addResponse("response", 2);
        mockChannelWriteTimeout(tunnel.getChannel());
        NetTunnel<Long> waitForSendTimeoutTunnel = tunnel;
        messages.subjcetForEach(c -> assertRunWithException(() -> waitForSendTimeoutTunnel.sendSync(c, 1000L), TimeoutException.class));
        verify(channel, times(times)).writeAndFlush(any());
        verify(session, times(times)).addSentMessage(any(NetMessage.class));
        verify(session, times(times)).createMessageId();
        verifyNoMoreInteractions(channel, session);

        // 发送 futureWaitSend Timeout
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        messages.contentsForEach(MessageContext::willSendFuture);
        messages.send(tunnel);
        assertWaitSendException(messages, TimeoutException.class);

        // 发送 futureWaitSend Timeout
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        messages.contentsForEach(MessageContext::willSendFuture);
        messages.send(tunnel);
        assertWaitSendException(messages, TimeoutException.class);

        // 发送 Wait Response
        tunnel = createLoginTunnel();
        channel = tunnel.getChannel();
        session = tunnel.getBindSession().orElse(null);
        messageCaptor = MockAide.captorAs(NetMessage.class);
        messages = new TestMessages(tunnel)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        times = messages.getMessageSize();
        TestMessages responses = new TestMessages(tunnel);
        messages.contentsForEach(MessageContext::willResponseFuture);
        mockChannelWrite(tunnel.getChannel());
        messages.send(tunnel);
        verify(channel, times(times)).writeAndFlush(any());
        verify(session, times(times)).addSentMessage(messageCaptor.capture());
        messageCaptor.getAllValues().forEach(m -> responses.addResponse("response", m.getId()));
        scheduleReceive(tunnel, responses).get();
        assertWaitResponseOK(messages);

        // 发送 Wait Response Timeout
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        messages.contentsForEach(MessageContext::willResponseFuture);
        mockChannelWrite(tunnel.getChannel());
        messages.send(tunnel);
        assertWaitResponseException(messages, TimeoutException.class);

        // 发送 Wait Response  发送失败
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        messages.contentsForEach(MessageContext::willResponseFuture);
        messages.sendFailed(new SessionException(""));
        assertWaitResponseException(messages, ExecutionException.class);
    }

    @Override
    @Test
    public void resend() throws NetException {
        T tunnel;
        TestMessages messages;
        NetSession<Long> session;
        Channel channel;
        int times;
        // tunnel = createUnloginTunnel();

        // 接受ResendMessage
        tunnel = createLoginTunnel();
        channel = tunnel.getChannel();
        session = tunnel.getBindSession().orElse(null);
        messages = new TestMessages(tunnel)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        messages.send(tunnel);
        mockChannelWrite(tunnel.getChannel());
        tunnel.resend(3L, 7L);
        times = messages.getMessageSize() * 2;
        verify(channel, times(times)).writeAndFlush(any());
        verify(session, times(messages.getMessageSize())).addSentMessage(any(NetMessage.class));
        verify(session, times(messages.getMessageSize())).createMessageId();
        verify(session, times(1)).getSentMessages(anyLong(), anyLong());
        verifyNoMoreInteractions(channel, session);

        // 接受ResendMessage
        tunnel = createLoginTunnel();
        channel = tunnel.getChannel();
        session = tunnel.getBindSession().orElse(null);
        messages = new TestMessages(tunnel)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        messages.send(tunnel);
        mockChannelWrite(tunnel.getChannel());
        NetTunnel<Long> noInRangeTunnel = tunnel;
        assertRunWithException(() -> noInRangeTunnel.resend(1L, 7L), NetException.class);

        // 没有 session
        tunnel = createUnloginTunnel();
        messages = new TestMessages(tunnel)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        messages.send(tunnel);
        NetTunnel<Long> noSessionTunnel = tunnel;
        assertRunWithException(() -> noSessionTunnel.resend(3L, 7L), NetException.class);


    }

    protected void mockChannelWrite(Channel channel) {
        when(channel.writeAndFlush(any())).thenAnswer(mk -> {
            ChannelFuture channelFuture = mock(ChannelFuture.class);
            when(channelFuture.isSuccess()).thenReturn(true);
            when(channelFuture.addListener(any())).thenAnswer((mkListener) -> {
                GenericFutureListener<Future<Void>> listener = as(mkListener.getArguments()[0]);
                listener.operationComplete(channelFuture);
                return null;
            });
            when(channelFuture.awaitUninterruptibly(anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);
            return channelFuture;
        });
    }

    protected void mockChannelWriteTimeout(Channel channel) {
        when(channel.writeAndFlush(any())).thenAnswer(mk -> {
            ChannelFuture channelFuture = mock(ChannelFuture.class);
            when(channelFuture.addListener(any())).thenAnswer((mkListener) -> {
                GenericFutureListener<Future<Void>> listener = as(mkListener.getArguments()[0]);
                listener.operationComplete(channelFuture);
                return null;
            });
            when(channelFuture.awaitUninterruptibly(anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(false);
            when(channelFuture.isSuccess()).thenReturn(false);
            when(channelFuture.cause()).thenReturn(new TimeoutException());
            return channelFuture;
        });
    }

    protected Channel mockTunnelChannel(T tunnel) {
        return tunnel.getChannel();
    }

    protected NetSession<Long> mockTunnelSession(T tunnel) {
        return as(tunnel.getBindSession().orElse(null));
    }

    private void testPingPong(int times, Consumer<T> consumer, MessageMode mode) {
        T tunnel = createLoginTunnel();
        Channel channel = mockTunnelChannel(tunnel);
        when(channel.isActive()).thenReturn(true);
        consumer.accept(tunnel);
        assertPingPong(times, tunnel, mode);
        // verify(channel, times(times)).isActive();
        // verify(channel, times(times)).writeAndFlush(message.capture());
        // assertEquals(times, message.getAllValues().size());
        // for (DetectMessage<Long> value : message.getAllValues())
        //     assertEquals(mode, value.getMode());
        // verifyNoMoreInteractions(channel);

    }

    private void assertPingPong(int times, T tunnel, MessageMode mode) {
        ArgumentCaptor<DetectMessage<Long>> message = captorAs(DetectMessage.class);
        Channel channel = mockTunnelChannel(tunnel);
        verify(channel, times(times)).isActive();
        verify(channel, times(times)).writeAndFlush(message.capture());
        assertEquals(times, message.getAllValues().size());
        for (DetectMessage<Long> value : message.getAllValues())
            assertEquals(mode, value.getMode());
        verifyNoMoreInteractions(channel);
    }


}