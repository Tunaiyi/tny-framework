package com.tny.game.net.netty;

import com.tny.game.net.exception.SessionException;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.*;
import io.netty.channel.*;
import io.netty.util.concurrent.*;
import io.netty.util.concurrent.Future;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import test.TestAide;

import java.util.concurrent.*;
import java.util.function.Consumer;

import static com.tny.game.common.utils.ObjectAide.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static test.MockAide.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class NettyTunnelTest<T extends NettyTunnel<Long>> extends NetTunnelTest<T> {

    protected abstract T newTunnel(Certificate<Long> certificate);

    @Override
    protected T createTunnel(Certificate<Long> certificate) {
        MessageInputEventHandler<Long, NetTunnel<Long>> inputEventHandler = mockAs(MessageInputEventHandler.class);
        MessageOutputEventHandler<Long, NetTunnel<Long>> outputEventHandler = mockAs(MessageOutputEventHandler.class);
        MessageBuilderFactory<Long> messageBuilderFactory = mockAs(MessageBuilderFactory.class);
        T tunnel = newTunnel(certificate);
        tunnel.setInputEventHandler(inputEventHandler)
                .setOutputEventHandler(outputEventHandler)
                .setMessageBuilderFactory(messageBuilderFactory);
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

        assertNotNull(tunnel.close());

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
    public void receive() throws ExecutionException, InterruptedException {
        TestMessages messages;
        T tunnel;
        MessageEventsBox<Long> eventsBox;

        // 接受Message
        tunnel = createLoginTunnel();
        eventsBox = tunnel.getEventsBox();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.receive(tunnel);
        assertEquals(messages.getMessageSize(), eventsBox.getInputEventSize());

        // 接受ping
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addPing()
                .addPing()
                .addPing();
        messages.receive(tunnel);
        assertPingPong(messages.getPingSize(), tunnel, MessageMode.PONG);
        // verify(channel, times(messages.getPingSize())).writeAndFlush(any(MessageReceiveEvent.class));
        // verify(tunnel, times(messages.getPingSize())).pong();
        // verify(tunnel, times(messages.getPingSize())).isExcludeReceiveMode(MessageMode.PING);
        // verifyNoMoreInteractions(tunnel);

        // 接受pong
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addPong()
                .addPong()
                .addPong();
        messages.receive(tunnel);
        assertPingPong(0, tunnel, MessageMode.PONG);
        // verify(tunnel, times(messages.getPongSize())).isExcludeReceiveMode(MessageMode.PONG);
        // verify(tunnel, never()).pong();
        // verifyNoMoreInteractions(tunnel);

        // 排除接受 all
        tunnel = createLoginTunnel();
        eventsBox = tunnel.getEventsBox();
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addRequest("request")
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeReceiveModes(MessageMode.values());
        messages.receive(tunnel);
        assertEquals(messages.getRequestSize(), eventsBox.getOutputEventSize());
        assertEquals(0, eventsBox.getInputEventSize());

        // 排除接受 REQUEST
        tunnel = createLoginTunnel();
        eventsBox = tunnel.getEventsBox();
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addRequest("request")
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeReceiveModes(MessageMode.REQUEST);
        messages.receive(tunnel);
        assertEquals(messages.getPushSize() + messages.getResponseSize(), eventsBox.getInputEventSize());
        assertEquals(messages.getRequestSize(), eventsBox.getOutputEventSize());

        // 排除接受 PUSH
        tunnel = createLoginTunnel();
        eventsBox = tunnel.getEventsBox();
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeReceiveModes(MessageMode.PUSH);
        messages.receive(tunnel);
        assertEquals(messages.getRequestSize() + messages.getResponseSize(), eventsBox.getInputEventSize());
        assertEquals(0, eventsBox.getOutputEventSize());

        // 排除接受 RESPONSE
        tunnel = createLoginTunnel();
        eventsBox = tunnel.getEventsBox();
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeReceiveModes(MessageMode.RESPONSE);
        messages.receive(tunnel);
        assertEquals(messages.getRequestSize() + messages.getPushSize(), eventsBox.getInputEventSize());
        assertEquals(0, eventsBox.getOutputEventSize());

    }

    @Override
    @Test
    public void send() throws ExecutionException, InterruptedException {
        T tunnel;
        MessageEventsBox<Long> eventsBox;
        TestMessages messages;

        // 接受Message
        tunnel = createLoginTunnel();
        eventsBox = tunnel.getEventsBox();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.send(tunnel);
        assertEquals(messages.getMessageSize(), eventsBox.getOutputEventSize());

        // 排除接受 all
        tunnel = createLoginTunnel();
        eventsBox = tunnel.getEventsBox();
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addRequest("request")
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeSendModes(MessageMode.values());
        messages.send(tunnel);
        assertEquals(0, eventsBox.getInputEventSize());
        assertEquals(0, eventsBox.getOutputEventSize());

        // 排除接受 REQUEST
        tunnel = createLoginTunnel();
        eventsBox = tunnel.getEventsBox();
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addRequest("request")
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeSendModes(MessageMode.REQUEST);
        messages.send(tunnel);
        assertEquals(messages.getPushSize() + messages.getResponseSize(), eventsBox.getOutputEventSize());
        assertEquals(0, eventsBox.getInputEventSize());

        // 排除接受 PUSH
        tunnel = createLoginTunnel();
        eventsBox = tunnel.getEventsBox();
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeSendModes(MessageMode.PUSH);
        messages.send(tunnel);
        assertEquals(messages.getRequestSize() + messages.getResponseSize(), eventsBox.getOutputEventSize());
        assertEquals(0, eventsBox.getInputEventSize());

        // 排除接受 RESPONSE
        tunnel = createLoginTunnel();
        eventsBox = tunnel.getEventsBox();
        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        tunnel.excludeSendModes(MessageMode.RESPONSE);
        messages.send(tunnel);
        assertEquals(messages.getRequestSize() + messages.getPushSize(), eventsBox.getOutputEventSize());
        assertEquals(0, eventsBox.getInputEventSize());

        // 发送 futureWaitSend
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 2);
        messages.contentsForEach(MessageContext::willSendFuture);
        messages.send(tunnel);
        messages.contentsForEach(c ->
                TestAide.assertRunWithoutException("get send result", () -> assertFalse(c.willSendFuture().isDone())));
        scheduleSendSuccess(tunnel, messages);
        assertWaitSendOk(messages);

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
        messages = new TestMessages(tunnel)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        TestMessages responses = new TestMessages(tunnel);
        messages.forEach((content, message) -> {
            content.willResponseFuture();
            responses.addResponse("response", message.getId());
        });
        messages.send(tunnel);
        messages.sendSuccess(tunnel);
        scheduleReceive(tunnel, responses).get();
        assertWaitResponseOK(messages);

        // 发送 Wait Response Timeout
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        messages.forEach((content, message) -> {
            content.willResponseFuture();
            responses.addResponse("response", message.getId());
        });
        messages.send(tunnel);
        messages.sendSuccess(tunnel);
        assertWaitResponseException(messages, TimeoutException.class);

        // 发送 Wait Response  发送失败
        tunnel = createLoginTunnel();
        messages = new TestMessages(tunnel)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        messages.forEach((content, message) -> {
            content.willResponseFuture();
        });
        messages.sendFailed(new SessionException(""));
        assertWaitResponseException(messages, ExecutionException.class);
    }

    @Override
    @Test
    public void resend() {
        T tunnel;
        MessageEventsBox<Long> eventsBox;

        // 接受ResendMessage
        tunnel = createLoginTunnel();
        eventsBox = tunnel.getEventsBox();
        tunnel.resend(ResendMessage.fromTo(10, 20));
        tunnel.resend(ResendMessage.fromTo(10, 20));
        tunnel.resend(ResendMessage.fromTo(10, 20));
        assertEquals(3, eventsBox.getOutputEventSize());
    }

    protected Channel mockTunnelChannel(T tunnel) {
        return tunnel.getChannel();
    }

    protected NetSession<Long> mockTunnelSession(T tunnel) {
        return as(tunnel.getSession().orElse(null));
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