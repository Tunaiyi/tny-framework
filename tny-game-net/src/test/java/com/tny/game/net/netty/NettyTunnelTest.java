package com.tny.game.net.netty;

import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
import com.tny.game.net.tunnel.*;
import io.netty.channel.*;
import io.netty.util.concurrent.*;
import org.junit.*;
import org.mockito.ArgumentCaptor;

import java.util.function.Consumer;

import static com.tny.game.common.utils.ObjectAide.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static test.MockAide.*;
import static test.MockAide.eq;
import static test.MockAide.never;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class NettyTunnelTest<T extends NettyTunnel<Long>> extends NetTunnelTest<T> {


    protected abstract T createUnloginTunnel(SessionFactory<Long> sessionFactory, MessageBuilderFactory<Long> messageBuilderFactory);

    @Override
    protected T createUnloginTunnel() {
        NetSession<Long> session = mockAs(NetSession.class);
        SessionFactory<Long> sessionFactory = mockAs(SessionFactory.class);
        MessageBuilderFactory<Long> messageBuilderFactory = mockAs(MessageBuilderFactory.class);
        when(sessionFactory.createSession(as(any(NetTunnel.class)))).thenReturn(session);
        T tunnel = createUnloginTunnel(sessionFactory, messageBuilderFactory);
        when(session.getCurrentTunnel()).thenReturn(tunnel);
        bindMockSessionWith(session, createUnLoginCert());
        return tunnel;
    }

    @Override
    @Test
    public void bind() {
        NetSession<Long> session;
        NetSession<Long> otherSession;
        T tunnel;
        T other;

        tunnel = createLoginTunnel();
        other = createLoginTunnel();
        session = mockTunnelSession(tunnel);
        otherSession = mockTunnelSession(other);

        assertTrue(tunnel.bind(session));
        assertFalse(tunnel.bind(otherSession));
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
        testPingPong(T::ping, MessageMode.PING);
    }


    @Override
    @Test
    public void pong() {
        testPingPong(T::pong, MessageMode.PONG);
    }

    @Override
    public void send() throws InterruptedException {
        T tunnel;
        Channel channel;
        NetSession<Long> session;
        MessageContent<Long> content;
        int sleepTime = 10;
        long lastWriteAt;

        tunnel = createLoginTunnel();
        lastWriteAt = tunnel.getLastWriteAt();
        session = mockTunnelSession(tunnel);
        channel = mockTunnelChannel(tunnel);
        content = mockAs(MessageContent.class);
        when(channel.isActive()).thenReturn(true);
        Thread.sleep(sleepTime);
        tunnel.send(content);
        verify(channel, times(1)).isActive();
        verify(session, times(1)).send(eq(tunnel), eq(content));
        verifyNoMoreInteractions(session, channel);
        assertTrue(tunnel.getLastWriteAt() >= lastWriteAt + sleepTime);


        tunnel = createLoginTunnel();
        lastWriteAt = tunnel.getLastWriteAt();
        session = mockTunnelSession(tunnel);
        content = mockAs(MessageContent.class);
        channel = mockTunnelChannel(tunnel);
        when(channel.isActive()).thenReturn(false);
        tunnel.send(content);
        verify(channel, times(1)).isActive();
        verify(session, times(1)).send(eq(tunnel), eq(content));
        verifyNoMoreInteractions(session, channel);
        assertEquals(lastWriteAt, tunnel.getLastWriteAt());
    }

    @Override
    public void receive() throws InterruptedException {
        T tunnel;
        Channel channel;
        NetSession<Long> session;
        Message<Long> message;
        int sleepTime = 10;
        long lastReadAt;

        tunnel = createLoginTunnel();
        lastReadAt = tunnel.getLastReadAt();
        session = mockTunnelSession(tunnel);
        channel = mockTunnelChannel(tunnel);
        message = mockAs(Message.class);
        when(channel.isActive()).thenReturn(true);
        Thread.sleep(sleepTime);
        tunnel.receive(message);
        verify(channel, times(1)).isActive();
        verify(session, times(1)).receive(eq(tunnel), eq(message));
        verifyNoMoreInteractions(session, channel);
        assertTrue(tunnel.getLastReadAt() >= lastReadAt + sleepTime);


        tunnel = createLoginTunnel();
        lastReadAt = tunnel.getLastReadAt();
        session = mockTunnelSession(tunnel);
        message = mockAs(Message.class);
        channel = mockTunnelChannel(tunnel);
        when(channel.isActive()).thenReturn(false);
        tunnel.receive(message);
        verify(channel, times(1)).isActive();
        verify(session, times(1)).receive(eq(tunnel), eq(message));
        verifyNoMoreInteractions(session, channel);
        assertEquals(lastReadAt, tunnel.getLastReadAt());

    }

    @Override
    public void resend() throws InterruptedException {
        T tunnel;
        Channel channel;
        NetSession<Long> session;
        ResendMessage<Long> message;
        int sleepTime = 10;
        long lastReadAt;

        tunnel = createLoginTunnel();
        lastReadAt = tunnel.getLastReadAt();
        session = mockTunnelSession(tunnel);
        channel = mockTunnelChannel(tunnel);
        message = mockAs(ResendMessage.class);
        when(channel.isActive()).thenReturn(true);
        Thread.sleep(sleepTime);
        tunnel.resend(message);
        verify(channel, times(1)).isActive();
        verify(session, times(1)).resend(eq(tunnel), eq(message));
        verifyNoMoreInteractions(session, channel);
        assertTrue(tunnel.getLastReadAt() >= lastReadAt + sleepTime);


        tunnel = createLoginTunnel();
        lastReadAt = tunnel.getLastReadAt();
        session = mockTunnelSession(tunnel);
        message = mockAs(ResendMessage.class);
        channel = mockTunnelChannel(tunnel);
        when(channel.isActive()).thenReturn(false);
        tunnel.resend(message);
        verify(channel, times(1)).isActive();
        verify(session, times(1)).resend(eq(tunnel), eq(message));
        verifyNoMoreInteractions(session, channel);
        assertEquals(lastReadAt, tunnel.getLastReadAt());
    }

    protected Channel mockTunnelChannel(T tunnel) {
        return tunnel.getChannel();
    }

    protected NetSession<Long> mockTunnelSession(T tunnel) {
        return as(tunnel.getSession());
    }

    private void testPingPong(Consumer<T> consumer, MessageMode mode) {
        T tunnel;
        Channel channel;
        tunnel = createLoginTunnel();
        ArgumentCaptor<DetectMessage<Long>> message = captorAs(DetectMessage.class);
        channel = mockTunnelChannel(tunnel);
        consumer.accept(tunnel);
        verify(channel, atLeastOnce()).isActive();
        verify(channel).writeAndFlush(message.capture());
        assertEquals(mode, message.getValue().getMode());
        verifyNoMoreInteractions(channel);

        tunnel = createLoginTunnel();
        channel = mockTunnelChannel(tunnel);
        when(channel.isActive()).thenReturn(false);
        consumer.accept(tunnel);
        verify(channel, times(1)).isActive();
        verify(channel, never()).writeAndFlush(message.capture());
        verifyNoMoreInteractions(channel);
    }


}