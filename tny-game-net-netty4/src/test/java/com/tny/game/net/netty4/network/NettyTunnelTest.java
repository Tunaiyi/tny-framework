/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.netty4.network;

import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.*;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.function.Consumer;

import static com.tny.game.common.utils.ObjectAide.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class NettyTunnelTest<E extends NetSession, T extends TransportTunnel<NetSession, ?>, ME extends MockNetSession>
        extends NetTunnelTest<T, ME> {

    protected EmbeddedChannel mockChannel() {
        return new MockChannel(new InetSocketAddress(8080), new InetSocketAddress(8081));
    }

    @Test
    void remoteAddress() {
        T tunnel = createBindTunnel();
        assertNotNull(tunnel.getRemoteAddress());
    }

    @Test
    void localAddress() {
        T tunnel = createBindTunnel();
        assertNotNull(tunnel.getLocalAddress());
    }

    @Test
    void isClosed() {
        T tunnel = createBindTunnel();
        assertFalse(tunnel.isClosed());
        assertTrue(tunnel.isActive());
        assertFalse(tunnel.isClosed());
        tunnel.close();
        assertTrue(tunnel.isClosed());
        assertFalse(tunnel.isActive());
    }

    @Test
    void bind() throws AuthFailedException {
        NetTunnel tunnel;
        MockNetSession session;
        TunnelTestInstance<T, ME> object;
        // 正常绑定
        object = create(createUnLoginCert(), false);
        tunnel = object.getTunnel();
        if (tunnel.getAccessMode() == NetAccessMode.CLIENT) {
            session = object.getSession();
            session.online(createLoginCert(), tunnel);
        } else {
            session = createSession();
        }
        assertTrue(tunnel.bind(session));
        assertTrue(tunnel.isAuthenticated());

        // 重复绑定 同一会话
        assertTrue(tunnel.bind(session));
        assertTrue(tunnel.isAuthenticated());

        // 重复绑定 不同会话
        session = createSession();
        assertFalse(tunnel.bind(session));

        // 接受未认证凭证
        tunnel = createUnbindTunnel();
        session = createSession(createUnLoginCert());
        assertFalse(tunnel.bind(session));

    }

    @Test
    void open() {
        T tunnel;

        // 正常开启
        tunnel = createTunnel(createLoginCert(), false);
        assertFalse(tunnel.isActive());
        assertTrue(tunnel.open());
        assertTrue(tunnel.isActive());
        assertTrue(tunnel.isOpen());

        // 重复开启
        assertTrue(tunnel.open());
        assertTrue(tunnel.isActive());
        assertTrue(tunnel.isOpen());

        // 关闭再打开
        tunnel = createBindTunnel();
        tunnel.close();
        assertFalse(tunnel.open());
        assertTrue(tunnel.isClosed());
        assertFalse(tunnel.isActive());
        assertFalse(tunnel.isOpen());

        // 断开再打开
        tunnel = createTunnel(createLoginCert(), true);
        tunnel.disconnect();
        assertFalse(tunnel.open());
        assertTrue(tunnel.isClosed());
        assertFalse(tunnel.isActive());
        assertFalse(tunnel.isOpen());
    }

    @Test
    void disconnect() {
        T tunnel;

        // 正常开启
        tunnel = createBindTunnel();
        assertTrue(tunnel.isActive());
        assertTrue(tunnel.isOpen());
        tunnel.disconnect();
        assertFalse(tunnel.isActive());
        assertFalse(tunnel.isOpen());

        // 重复开启
        tunnel.disconnect();
        assertFalse(tunnel.isActive());
        assertFalse(tunnel.isOpen());

    }

    @Test
    void close() {
        T tunnel;
        tunnel = createBindTunnel();

        // 正常关闭
        assertFalse(tunnel.isClosed());
        assertTrue(tunnel.isActive());
        tunnel.close();
        assertTrue(tunnel.isClosed());
        assertFalse(tunnel.isActive());

        tunnel.close();
        assertTrue(tunnel.isClosed());
        assertFalse(tunnel.isActive());
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
        T tunnel;
        TestMessages messages;
        ME session;
        TunnelTestInstance<T, ME> object;

        // 接受Message
        object = create();
        tunnel = object.getTunnel();
        session = object.getSession();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.receive(tunnel);
        assertTrue(CollectionUtils.containsAll(messages.getMessages(), session.getReceiveQueue()));

        // 关闭 接受Message
        object = create();
        tunnel = object.getTunnel();
        session = object.getSession();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        tunnel.close();
        messages.receive(tunnel);
        assertTrue(CollectionUtils.containsAll(messages.getMessages(), session.getReceiveQueue()));

    }

    @Override
    @Test
    public void send() {
        T tunnel;
        TestMessages messages;
        ME session;
        TunnelTestInstance<T, ME> object;

        // 接受Message
        object = create();
        tunnel = object.getTunnel();
        session = object.getSession();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        messages.send(tunnel);
        assertTrue(CollectionUtils.containsAll(messages.getMessages(), session.getReceiveQueue()));

        // 关闭 接受Message
        object = create();
        tunnel = object.getTunnel();
        session = object.getSession();
        messages = new TestMessages(tunnel)
                .addPush("push")
                .addRequest("request")
                .addResponse("response", 1);
        tunnel.close();
        messages.send(tunnel);
        assertTrue(session.getReceiveQueue().isEmpty());
    }

    @Test
    void write() {
        T tunnel;
        EmbeddedChannel channel;
        TestMessages messages;

        // 写出 message 成功
        tunnel = createBindTunnel();
        channel = embeddedChannel(tunnel);
        messages = TestMessages.createMessages(tunnel);
        messages.write(tunnel, null);
        assertTrue(CollectionUtils.containsAll(messages.getMessages(), channel.outboundMessages()));

        // 写出 message 成功
        tunnel = createBindTunnel();
        channel = embeddedChannel(tunnel);
        messages = TestMessages.createMessages(tunnel);
        channel.close();
        messages.write(tunnel, null);
        assertTrue(channel.outboundMessages().isEmpty());

        // 写出 message 非 NettyWriteMessagePromise 异常
        tunnel = createBindTunnel();
        channel = embeddedChannel(tunnel);
        messages = TestMessages.createMessages(tunnel);
        T t1 = tunnel;
        messages.messagesForEach(m -> {
            try {
                t1.write(m, new MessageWriteFuture());
                assertTrue(true);
            } catch (Throwable e) {
                fail("write failed");
            }
        });
        assertTrue(CollectionUtils.containsAll(messages.getMessages(), channel.outboundMessages()));

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
        //     channel = mockTunnelChannel(tunnel);
        //     message = mockAs(Message.class);
        //     when(message.getMode()).thenReturn(MessageMode.PUSH);
        //     when(channel.writeAndFlush(message)).thenThrow(NullPointerException.class);
        //     Thread.sleep(sleepTime);
        //     tunnel.write(message, callback);
        //     verify(channel, times(1)).writeAndFlush(eq(message));
        //     verifyNoMoreInteractions(channel);
        //     assertFalse(writeResult.get());
    }

    protected abstract EmbeddedChannel embeddedChannel(T tunnel);

    private void testPingPong(int times, Consumer<T> consumer, MessageMode mode) {
        T activeTunnel = createBindTunnel();
        T closeTunnel = createBindTunnel();
        closeTunnel.close();
        for (int i = 0; i < times; i++) {
            consumer.accept(activeTunnel);
            consumer.accept(closeTunnel);
        }
        assertPingPong(times, activeTunnel, mode);
        assertPingPong(0, closeTunnel, mode);
    }

    private void assertPingPong(int times, T tunnel, MessageMode mode) {
        EmbeddedChannel channel = embeddedChannel(tunnel);
        assertEquals(times, channel.outboundMessages().size());
        for (Object value : channel.outboundMessages()) {
            assertInstanceOf(TickMessage.class, value);
            assertEquals(mode, Objects.requireNonNull(as(value, TickMessage.class)).getMode());
        }
    }

}