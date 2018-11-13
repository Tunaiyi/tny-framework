package com.tny.game.net.netty4;

import com.tny.game.net.message.common.CommonMessageFactory;
import com.tny.game.net.transport.Certificate;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public class NettyServerTunnelTest extends NettyTunnelTest<NettyServerTunnel<Long>> {

    @Override
    protected NettyServerTunnel<Long> newTunnel(Certificate<Long> certificate) {
        NettyServerTunnel<Long> tunnel = new NettyServerTunnel<>(mockChannel(), certificate, new CommonMessageFactory<>());
        tunnel.open();
        return tunnel;
    }

    // @Test
    // @Override
    // @SuppressWarnings("unchecked")
    // public void write() throws Exception {
    //     NettyServerTunnel<Long> tunnel;
    //     Channel channel;
    //     ChannelFuture future;
    //     Message<Long> message;
    //     long sleepTime = 10;
    //     MessageContext<Long> context;
    //     AtomicBoolean writeResult = new AtomicBoolean(false);
    //     ArgumentCaptor<GenericFutureListener<Future<Void>>> listener = captorAs(GenericFutureListener.class);
    //
    //     // 写出 message 成功
    //     tunnel = createLoginTunnel();
    //     channel = mockTunnelChannel(tunnel);
    //     message = mockAs(Message.class);
    //     context = mockAs(MessageContext.class);
    //     future = mockAs(ChannelFuture.class);
    //     when(message.getMode()).thenReturn(MessageMode.PUSH);
    //     when(channel.writeAndFlush(message)).thenReturn(future);
    //     Thread.sleep(sleepTime);
    //     tunnel.write(message, context);
    //     verify(channel, times(1)).writeAndFlush(eq(message));
    //     verifyNoMoreInteractions(channel);
    //
    //     // 写出 message 异常
    //     tunnel = createLoginTunnel();
    //     channel = mockTunnelChannel(tunnel);
    //     message = mockAs(Message.class);
    //     when(message.getMode()).thenReturn(MessageMode.PUSH);
    //     when(channel.writeAndFlush(message)).thenThrow(NullPointerException.class);
    //     Thread.sleep(sleepTime);
    //     tunnel.write(message);
    //     verify(channel, times(1)).writeAndFlush(eq(message));
    //     verifyNoMoreInteractions(channel);
    //
    //     // 写出 message callback 成功
    //     tunnel = createLoginTunnel();
    //     channel = mockTunnelChannel(tunnel);
    //     message = mockAs(Message.class);
    //     future = mockAs(ChannelFuture.class);
    //     when(message.getMode()).thenReturn(MessageMode.PUSH);
    //     when(channel.writeAndFlush(message)).thenReturn(future);
    //     when(future.isSuccess()).thenReturn(true);
    //     Thread.sleep(sleepTime);
    //     tunnel.write(message, callback);
    //     verify(channel, times(1)).writeAndFlush(eq(message));
    //     verify(future, times(1)).addListener(listener.capture());
    //     listener.getValue().operationComplete(future);
    //     verify(future, times(1)).isSuccess();
    //     assertTrue(writeResult.get());
    //     verifyNoMoreInteractions(channel, future);
    //     writeResult.set(false);
    //
    //     // 写出 message callback 失败
    //     writeResult.set(true);
    //     tunnel = createLoginTunnel();
    //     channel = mockTunnelChannel(tunnel);
    //     message = mockAs(Message.class);
    //     future = mockAs(ChannelFuture.class);
    //     when(message.getMode()).thenReturn(MessageMode.PUSH);
    //     when(channel.writeAndFlush(message)).thenReturn(future);
    //     when(future.isSuccess()).thenReturn(false);
    //     Thread.sleep(sleepTime);
    //     tunnel.write(message, callback);
    //     verify(channel, times(1)).writeAndFlush(eq(message));
    //     verify(future, times(1)).addListener(listener.capture());
    //     listener.getValue().operationComplete(future);
    //     verify(future, times(1)).isSuccess();
    //     verify(future, times(1)).cause();
    //     assertFalse(writeResult.get());
    //     verifyNoMoreInteractions(channel, future);
    //     writeResult.set(false);
    //
    //     // 写出 message callback channel 异常
    //     writeResult.set(true);
    //     tunnel = createLoginTunnel();
    //     channel = mockTunnelChannel(tunnel);
    //     message = mockAs(Message.class);
    //     when(message.getMode()).thenReturn(MessageMode.PUSH);
    //     when(channel.writeAndFlush(message)).thenThrow(NullPointerException.class);
    //     Thread.sleep(sleepTime);
    //     tunnel.write(message, callback);
    //     verify(channel, times(1)).writeAndFlush(eq(message));
    //     verifyNoMoreInteractions(channel);
    //     assertFalse(writeResult.get());
    // }

}