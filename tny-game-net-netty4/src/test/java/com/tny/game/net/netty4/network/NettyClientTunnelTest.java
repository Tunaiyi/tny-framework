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

import com.tny.game.common.url.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.transport.*;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * Created by Kun Yang on 2018/8/27.
 */
public class NettyClientTunnelTest extends NettyTunnelTest<MockNetSession, TestGeneralClientTunnel, MockNettyClient> {

    private static final NetIdGenerator ID_GENERATOR = new AutoIncrementIdGenerator();

    private static final int CONNECT_TIMEOUT = 140;

    private static final int SEND_TIMEOUT = 0;

    private static final int LOGIN_TIMEOUT = 100;

    private static final int RESEND_TIMES = 130;

    private final URL url = URL
            .valueOf(StringAide.format("proto://127.0.0.1:9900?connect_timeout={}&send_timeout={}&login_timeout={}&resend_times={}",
                    CONNECT_TIMEOUT, SEND_TIMEOUT, LOGIN_TIMEOUT, RESEND_TIMES));

    @Override
    protected TunnelTestInstance<TestGeneralClientTunnel, MockNettyClient> create(Certificate certificate, boolean open) {
        MockNettyClient client = this.createSession(certificate);
        TestGeneralClientTunnel tunnel = this.newTunnel(open, client);
        if (certificate.isAuthenticated()) {
            tunnel.bind(client);
        }
        return new TunnelTestInstance<>(tunnel, client);
    }

    @Override
    protected MockNettyClient createSession(Certificate certificate) {
        return new MockNettyClient(this.url, certificate);
    }

    private TestGeneralClientTunnel newTunnel(boolean open, MockNettyClient client) {
        TestGeneralClientTunnel tunnel = new TestGeneralClientTunnel(ID_GENERATOR.generate(), client.connect(), client,
                new NetBootstrapContext(null, null, null, null, new CommonMessageFactory(), null, new DefaultContactFactory(), null,
                        new RpcMonitor()));
        if (open) {
            tunnel.open();
        }
        return tunnel;
    }

    @Override
    protected EmbeddedChannel embeddedChannel(TestGeneralClientTunnel tunnel) {
        return (EmbeddedChannel) ((NettyChannelMessageTransport) tunnel.getTransport()).getChannel();
    }
    // @Override
    // protected NettyClientTunnel createUnloginTunnel(SessionFactory sessionFactory, MessageBuilderFactory
    // messageBuilderFactory) {
    //     return createUnloginTunnel(sessionFactory, messageBuilderFactory, (r, t) -> {
    //         CommonStageableFuture<Message> future = new CommonStageableFuture<>();
    //         MessageContext content = mockAs(MessageContext.class);
    //         when(content.willResponseFuture(anyLong())).thenReturn(future);
    //         return content;
    //     });
    // }
    //
    // protected NettyClientTunnel createLoginTunnel(BiFunction<Boolean, Tunnel, MessageContext> loginContentCreator) {
    //     NettyClientTunnel tunnel = createLoginTunnel();
    //     tunnel.setLoginContentCreator(loginContentCreator);
    //     mockBindLoginSession(tunnel);
    //     return tunnel;
    // }
    //
    // protected NettyClientTunnel createUnloginTunnel(BiFunction<Boolean, Tunnel, MessageContext> loginContentCreator) {
    //     NetSession session = mockAs(NetSession.class);
    //     SessionFactory sessionFactory = mockAs(SessionFactory.class);
    //     MessageBuilderFactory messageBuilderFactory = mockAs(MessageBuilderFactory.class);
    //     when(sessionFactory.createSession(as(any(NetTunnel.class)))).thenReturn(session);
    //     return createUnloginTunnel(sessionFactory, messageBuilderFactory, loginContentCreator);
    // }
    //
    // protected NettyClientTunnel createUnloginTunnel(SessionFactory sessionFactory, MessageBuilderFactory messageBuilderFactory,
    // BiFunction<Boolean, Tunnel, MessageContext> loginContentCreator) {
    //     Channel channel = mock(Channel.class);
    //     NettyClientTunnel tunnel = new NettyClientTunnel(url, channel, config, sessionFactory, messageBuilderFactory,
    //     loginContentCreator);
    //     when(channel.isActive()).thenReturn(true);
    //     when(channel.remoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 1000));
    //     when(channel.localAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 9000));
    //     return tunnel;
    // }
    //
    // @Test
    // public void getLoginContentCreator() {
    //     assertNotNull(createUnloginTunnel().getLoginContentCreator());
    // }
    //
    // @Test
    // public void getUrl() {
    //     assertEquals(url, createUnloginTunnel().getUrl());
    // }
    //
    // @Test
    // public void getLoginTimeout() {
    //     assertEquals(LOGIN_TIMEOUT, createUnloginTunnel().getLoginTimeout());
    // }
    //
    // @Test
    // public void getSendTimeout() {
    //     assertEquals(SEND_TIMEOUT, createUnloginTunnel().getSendTimeout());
    // }
    //
    // @Test
    // public void getResendTimes() {
    //     assertEquals(RESEND_TIMES, createUnloginTunnel().getResendTimes());
    // }
    // @Test
    // public void resetChannel() {
    //     NettyClientTunnel tunnel = createUnloginTunnel();
    //     Channel channel = mockTunnelChannel(tunnel);
    //     Channel newChannel = mock(Channel.class);
    //
    //     assertFalse(tunnel.resetChannel(channel));
    //     assertFalse(tunnel.resetChannel(newChannel));
    //
    //     when(channel.isActive()).thenReturn(false);
    //     when(newChannel.isActive()).thenReturn(false);
    //     assertFalse(tunnel.resetChannel(newChannel));
    //     when(newChannel.isActive()).thenReturn(true);
    //     assertTrue(tunnel.resetChannel(newChannel));
    // }
    // @Test
    // public void login() throws InterruptedException, ExecutionException, TimeoutException, TunnelWriteException {
    //     MessageContext content = mockAs(MessageContext.class);
    //     CommonStageableFuture<Message> future;
    //     NettyClientTunnel tunnel;
    //     Message message;
    //     BiFunction<Boolean, Tunnel, MessageContext> loginContentCreator = (r, t) -> content;
    //
    //     // 正常登录
    //     future = mockAs(CommonStageableFuture.class);
    //     message = mockAs(Message.class);
    //     when(content.willResponseFuture(anyLong())).thenReturn(future);
    //     when(future.get(anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(message);
    //     when(future.get()).thenReturn(message);
    //     when(message.getCode()).thenReturn(ResultCode.SUCCESS_CODE);
    //     loginContentCreator = (r, t) -> content;
    //     tunnel = createUnloginTunnel(loginContentCreator);
    //     tunnel.login();
    //
    //     // 登录失败
    //     future = mockAs(CommonStageableFuture.class);
    //     message = mockAs(Message.class);
    //     when(content.willResponseFuture(anyLong())).thenReturn(future);
    //     when(future.get(anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(message);
    //     when(future.get()).thenReturn(message);
    //     when(message.getCode()).thenReturn(ResultCode.FAILURE_CODE);
    //     NettyClientTunnel loginFailTunnel = createUnloginTunnel(loginContentCreator);
    //     assertRunWithException("login exception", loginFailTunnel::login, TunnelWriteException.class);
    //
    //     // forkjoin 登录
    //     future = mockAs(CommonStageableFuture.class);
    //     message = mockAs(Message.class);
    //     when(content.willResponseFuture(anyLong())).thenReturn(future);
    //     when(future.isDone()).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(true);
    //     when(future.get(anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(message);
    //     when(future.get()).thenReturn(message);
    //     when(message.getCode()).thenReturn(ResultCode.FAILURE_CODE);
    //     NettyClientTunnel forkjoinTunnel = createUnloginTunnel(loginContentCreator);
    //     runParallel("forkjoinTunnel.login", forkjoinTunnel::login);
    //
    // }

}