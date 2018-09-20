package com.tny.game.net.netty;

import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.*;
import io.netty.channel.*;
import io.netty.util.concurrent.*;
import org.junit.*;
import org.mockito.ArgumentCaptor;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static test.MockAide.*;
import static test.MockAide.mock;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public class NettyServerTunnelTest extends NettyTunnelTest<NettyServerTunnel<Long>> {

    @Override
    protected NettyServerTunnel<Long> newTunnel(Certificate<Long> certificate) {
        Channel channel = mock(Channel.class);
        NettyServerTunnel<Long> tunnel = new NettyServerTunnel<>(channel, certificate);
        when(channel.isActive()).thenReturn(true);
        when(channel.remoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 1000));
        when(channel.localAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 9000));
        return tunnel;
    }

    @Test
    @Override
    @SuppressWarnings("unchecked")
    public void write() throws Exception {
        NettyServerTunnel<Long> tunnel;
        Channel channel;
        ChannelFuture future;
        Message<Long> message;
        long sleepTime = 10;
        AtomicBoolean writeResult = new AtomicBoolean(false);
        ArgumentCaptor<GenericFutureListener<Future<Void>>> listener = captorAs(GenericFutureListener.class);
        WriteCallback<Long> callback = (m, result, cause) -> writeResult.set(result);

        // 写出 message 成功
        tunnel = createLoginTunnel();
        channel = mockTunnelChannel(tunnel);
        message = mockAs(Message.class);
        future = mockAs(ChannelFuture.class);
        when(message.getMode()).thenReturn(MessageMode.PUSH);
        when(channel.writeAndFlush(message)).thenReturn(future);
        Thread.sleep(sleepTime);
        tunnel.write(message);
        verify(channel, times(1)).writeAndFlush(eq(message));
        verifyNoMoreInteractions(channel);

        // 写出 message 异常
        tunnel = createLoginTunnel();
        channel = mockTunnelChannel(tunnel);
        message = mockAs(Message.class);
        when(message.getMode()).thenReturn(MessageMode.PUSH);
        when(channel.writeAndFlush(message)).thenThrow(NullPointerException.class);
        Thread.sleep(sleepTime);
        tunnel.write(message);
        verify(channel, times(1)).writeAndFlush(eq(message));
        verifyNoMoreInteractions(channel);

        // 写出 message callback 成功
        tunnel = createLoginTunnel();
        channel = mockTunnelChannel(tunnel);
        message = mockAs(Message.class);
        future = mockAs(ChannelFuture.class);
        when(message.getMode()).thenReturn(MessageMode.PUSH);
        when(channel.writeAndFlush(message)).thenReturn(future);
        when(future.isSuccess()).thenReturn(true);
        Thread.sleep(sleepTime);
        tunnel.write(message, callback);
        verify(channel, times(1)).writeAndFlush(eq(message));
        verify(future, times(1)).addListener(listener.capture());
        listener.getValue().operationComplete(future);
        verify(future, times(1)).isSuccess();
        assertTrue(writeResult.get());
        verifyNoMoreInteractions(channel, future);
        writeResult.set(false);

        // 写出 message callback 失败
        writeResult.set(true);
        tunnel = createLoginTunnel();
        channel = mockTunnelChannel(tunnel);
        message = mockAs(Message.class);
        future = mockAs(ChannelFuture.class);
        when(message.getMode()).thenReturn(MessageMode.PUSH);
        when(channel.writeAndFlush(message)).thenReturn(future);
        when(future.isSuccess()).thenReturn(false);
        Thread.sleep(sleepTime);
        tunnel.write(message, callback);
        verify(channel, times(1)).writeAndFlush(eq(message));
        verify(future, times(1)).addListener(listener.capture());
        listener.getValue().operationComplete(future);
        verify(future, times(1)).isSuccess();
        verify(future, times(1)).cause();
        assertFalse(writeResult.get());
        verifyNoMoreInteractions(channel, future);
        writeResult.set(false);

        // 写出 message callback channel 异常
        writeResult.set(true);
        tunnel = createLoginTunnel();
        channel = mockTunnelChannel(tunnel);
        message = mockAs(Message.class);
        when(message.getMode()).thenReturn(MessageMode.PUSH);
        when(channel.writeAndFlush(message)).thenThrow(NullPointerException.class);
        Thread.sleep(sleepTime);
        tunnel.write(message, callback);
        verify(channel, times(1)).writeAndFlush(eq(message));
        verifyNoMoreInteractions(channel);
        assertFalse(writeResult.get());
    }

}