package com.tny.game.net.netty4;

import com.tny.game.net.endpoint.NetEndpoint;
import com.tny.game.net.exception.SendTimeoutException;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import com.tny.game.test.TestAide;
import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.*;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.*;
import org.mockito.ArgumentCaptor;

import java.net.InetSocketAddress;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.test.MockAide.*;
import static com.tny.game.test.TestAide.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class NettyTunnelTest<T extends NettyTunnel<Long>> extends NetTunnelTest<T> {

    protected abstract T newTunnel(Certificate<Long> certificate);

    protected Channel mockChannel() {
        Channel channel = mock(Channel.class);
        when(channel.isActive()).thenReturn(true);
        when(channel.writeAndFlush(any())).thenAnswer(mk -> mock(ChannelPromise.class));
        when(channel.remoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 1000));
        when(channel.localAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 9000));
        // when(channel.newPromise()).thenAnswer(m -> mock(ChannelPromise.class));
        return channel;
    }

    @Override
    protected T createTunnel(Certificate<Long> certificate) {
        T tunnel = newTunnel(certificate);
        return tunnel;
    }

    @Override
    @Test
    public void remoteAddress() {
        T tunnel = createBindTunnel();
        assertNotNull(tunnel.getRemoteAddress());
    }

    @Override
    @Test
    public void localAddress() {
        T tunnel = createBindTunnel();
        assertNotNull(tunnel.getLocalAddress());
    }

    @Override
    @Test
    public void isClosed() {
        T tunnel = createBindTunnel();
        assertFalse(tunnel.isClosed());
        assertTrue(tunnel.isAvailable());
        Channel channel = mockTunnelChannel(tunnel);
        when(channel.isActive()).thenReturn(false);
        assertFalse(tunnel.isAvailable());
        assertFalse(tunnel.isClosed());
        tunnel.close();
        assertTrue(tunnel.isClosed());
    }

    @Test
    public void close() throws Exception {
        T tunnel;
        Channel channel;
        tunnel = createBindTunnel();
        channel = mockTunnelChannel(tunnel);
        // 正常关闭
        tunnel.close();
        verify(channel, times(1)).close();
        assertTrue(tunnel.isClosed());

        tunnel.close();
        verify(channel, times(1)).close();
        assertTrue(tunnel.isClosed());


        tunnel = createBindTunnel();
        channel = mockTunnelChannel(tunnel);
        when(channel.isActive()).thenReturn(false);
        tunnel.close();
        verify(channel, never()).close();
        assertTrue(tunnel.isClosed());
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
        NetEndpoint<Long> endpoint;
        ArgumentCaptor<NetMessage<Long>> messageCaptor;
        T tunnel;
        // 接受Message
        tunnel = createBindTunnel();
        messageCaptor = captorAs(NetMessage.class);
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.receive(tunnel);
        assertTrue(CollectionUtils.containsAll(messages.getMessages(), messageCaptor.getAllValues()));

        // 排除接受 all
        tunnel = createBindTunnel();
        endpoint = mockEndpoint(tunnel);

        messages = new TestMessages(tunnel)
                .addRequest("request")
                .addRequest("request")
                .addRequest("request")
                .addPush("push")
                .addResponse("response", 1);
        when(endpoint.getReceiveFilter()).thenReturn(m -> false);
        messages.receive(tunnel, (m, r) -> assertFalse(r));

    }

    @Override
    @Test
    public void send() throws ExecutionException, InterruptedException {
        T tunnel;
        TestMessages messages;
        NetEndpoint<Long> endpoint;
        Channel channel;
        ArgumentCaptor<NetMessage<Long>> messageCaptor;
        int times;
        long respontsId = MessageAide.RESPONSE_TO_MESSAGE_MIN_ID;

        // 发送Message 有 endpoint
        tunnel = createBindTunnel();
        endpoint = mockEndpoint(tunnel);
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.sendAsyn(tunnel);
        times = messages.getMessageSize();
        verify(endpoint, times(times)).sendAsyn(eq(tunnel), any());
        verifyNoMoreInteractions(endpoint);

        // 发送Message 无 endpoint
        tunnel = createUnbindTunnel();
        channel = tunnel.getChannel();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.sendAsyn(tunnel);
        times = messages.getMessageSize();
        // verify(channel, times(times)).newPromise();
        verify(channel, times(times)).writeAndFlush(any());


        // 发送Message 有 endpoint 有 filter
        tunnel = createBindTunnel();
        endpoint = mockEndpoint(tunnel);
        when(endpoint.getSendFilter()).thenReturn(m -> false);
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", respontsId++);
        messages.sendAsyn(tunnel);
        times = messages.getMessageSize();
        verify(endpoint, times(times)).sendAsyn(eq(tunnel), any());

        // 发送 willSendFuture
        tunnel = createUnbindTunnel();
        channel = tunnel.getChannel();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", respontsId++);
        messages.contextsForEach(MessageContext::willSendFuture);
        messages.contextsForEach(c ->
                TestAide.assertRunComplete("get send result", () -> assertFalse(c.getSendFuture().isDone())));
        times = messages.getMessageSize();
        mockChannelWrite(tunnel.getChannel());
        messages.sendAsyn(tunnel);
        // verify(channel, times(times)).newPromise();
        verify(channel, times(times)).writeAndFlush(any());
        assertSendOk(messages);

        // 发送 willSendFuture Timeout
        tunnel = createUnbindTunnel();
        channel = tunnel.getChannel();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", respontsId++);
        messages.contextsForEach(MessageContext::willSendFuture);
        messages.sendAsyn(tunnel);
        times = messages.getMessageSize();
        // verify(channel, times(times)).newPromise();
        verify(channel, times(times)).writeAndFlush(any());
        assertWaitSendException(messages, TimeoutException.class);

        // 发送 sendSync
        tunnel = createUnbindTunnel();
        channel = tunnel.getChannel();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", respontsId++);
        messages.contextsForEach(MessageContext::willSendFuture);
        messages.contextsForEach(c ->
                TestAide.assertRunComplete("get send result", () -> assertFalse(c.getSendFuture().isDone())));
        mockChannelWrite(tunnel.getChannel());
        messages.sendSync(tunnel, 5000L);
        // verify(channel, times(times)).newPromise();
        verify(channel, times(times)).writeAndFlush(any());
        assertSendOk(messages);

        // 发送 waitForSend Timeout
        tunnel = createUnbindTunnel();
        channel = tunnel.getChannel();
        messages = new TestMessages(tunnel)
                .addResponse("response", respontsId++);
        times = messages.getMessageSize();
        NetTunnel<Long> waitForSendTimeoutTunnel = tunnel;
        messages.contextsForEach(c -> assertRunWithException(() ->
                waitForSendTimeoutTunnel.sendSync(c, 1000L), SendTimeoutException.class));
        // verify(channel, times(times)).newPromise();
        verify(channel, times(times)).writeAndFlush(any());

        // 发送 Wait Response
        tunnel = createUnbindTunnel();
        channel = tunnel.getChannel();
        messageCaptor = captorAs(NetMessage.class);
        messages = new TestMessages(tunnel)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        times = messages.getMessageSize();
        TestMessages responses = new TestMessages(tunnel);
        messages.requestContextsForEach(RequestContext::willResponseFuture);
        mockChannelWrite(tunnel.getChannel());
        messages.sendAsyn(tunnel);
        verify(channel, times(times)).writeAndFlush(messageCaptor.capture());
        messageCaptor.getAllValues().forEach(m -> responses.addResponse("response", m.getId()));
        responses.receive(tunnel);
        assertWaitResponseOK(messages);

        // 发送 Wait Response Timeout
        tunnel = createUnbindTunnel();
        messages = new TestMessages(tunnel)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        messages.requestContextsForEach(RequestContext::willResponseFuture);
        mockChannelWrite(tunnel.getChannel());
        messages.sendAsyn(tunnel);
        assertWaitResponseException(messages, TimeoutException.class);

        // 发送 Wait Response  发送失败
        tunnel = createBindTunnel();
        messages = new TestMessages(tunnel)
                .addRequest("request 1")
                .addRequest("request 2")
                .addRequest("request 3");
        messages.requestContextsForEach(RequestContext::willResponseFuture);
        mockChannelWriteException(tunnel.getChannel(), new TimeoutException());
        messages.sendAsyn(tunnel);
        assertWaitResponseException(messages, TimeoutException.class);
    }

    // @Override
    // @Test
    // public void resend() throws NetException {
    //     T tunnel;
    //     TestMessages messages;
    //     NetSession<Long> session;
    //     Channel channel;
    //     int times;
    //     // tunnel = createUnloginTunnel();
    //
    //     // 接受ResendMessage
    //     tunnel = createBindTunnel();
    //     channel = tunnel.getChannel();
    //     session = tunnel.getBindSession().orElse(null);
    //     messages = new TestMessages(tunnel)
    //             .addRequest("request 1")
    //             .addRequest("request 2")
    //             .addRequest("request 3");
    //     messages.send(tunnel);
    //     mockChannelWrite(tunnel.getChannel());
    //     tunnel.resend(3L, 7L);
    //     times = messages.getMessageSize() * 2;
    //     verify(channel, times(times)).writeAndFlush(any());
    //     verify(session, times(messages.getMessageSize())).addSentMessage(any(NetMessage.class));
    //     verify(session, times(messages.getMessageSize())).createMessageId();
    //     verify(session, times(1)).getSentMessages(anyLong(), anyLong());
    //     verifyNoMoreInteractions(channel, session);
    //
    //     // 接受ResendMessage
    //     tunnel = createBindTunnel();
    //     channel = tunnel.getChannel();
    //     session = tunnel.getBindSession().orElse(null);
    //     messages = new TestMessages(tunnel)
    //             .addRequest("request 1")
    //             .addRequest("request 2")
    //             .addRequest("request 3");
    //     messages.send(tunnel);
    //     mockChannelWrite(tunnel.getChannel());
    //     NetTunnel<Long> noInRangeTunnel = tunnel;
    //     assertRunWithException(() -> noInRangeTunnel.resend(1L, 7L), NetException.class);
    //
    //     // 没有 session
    //     tunnel = createUnloginTunnel();
    //     messages = new TestMessages(tunnel)
    //             .addRequest("request 1")
    //             .addRequest("request 2")
    //             .addRequest("request 3");
    //     messages.send(tunnel);
    //     NetTunnel<Long> noSessionTunnel = tunnel;
    //     assertRunWithException(() -> noSessionTunnel.resend(3L, 7L), NetException.class);
    // }

    protected void mockChannelWrite(Channel channel) {
        when(channel.writeAndFlush(any())).thenAnswer(mk -> {
            ChannelPromise channelFuture = mock(ChannelPromise.class);
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

    protected void mockChannelWriteException(Channel channel, Throwable cause) {
        when(channel.writeAndFlush(any())).thenAnswer(mk -> {
            ChannelFuture channelFuture = mock(ChannelFuture.class);
            when(channelFuture.awaitUninterruptibly(anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(false);
            when(channelFuture.isSuccess()).thenReturn(false);
            when(channelFuture.cause()).thenReturn(cause);
            when(channelFuture.addListener(any())).thenAnswer((mkListener) -> {
                GenericFutureListener<Future<Void>> listener = as(mkListener.getArguments()[0]);
                listener.operationComplete(channelFuture);
                return null;
            });
            return channelFuture;
        });
    }

    protected Channel mockTunnelChannel(T tunnel) {
        return tunnel.getChannel();
    }

    protected NetEndpoint<Long> mockEndpoint(T tunnel) {
        return (NetEndpoint<Long>) tunnel.getBindEndpoint().orElse(null);
    }

    private void testPingPong(int times, Consumer<T> consumer, MessageMode mode) {
        T tunnel = createBindTunnel();
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
        verify(channel, times(times)).writeAndFlush(message.capture());
        assertEquals(times, message.getAllValues().size());
        for (DetectMessage<Long> value : message.getAllValues())
            assertEquals(mode, value.getMode());
    }

}