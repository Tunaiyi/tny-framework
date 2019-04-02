package com.tny.game.net.netty4;

import com.tny.game.common.utils.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.transport.*;

/**
 * Created by Kun Yang on 2018/8/27.
 */
public class NettyTerminalTunnelTest extends NettyTunnelTest<NettyTerminal<Long>, NettyTerminalTunnel<Long>, MockNettyClient> {

    private static final int CONNECT_TIMEOUT = 140;
    private static final int SEND_TIMEOUT = 0;
    private static final int LOGIN_TIMEOUT = 100;
    private static final int RESEND_TIMES = 130;

    private URL url = URL.valueOf(StringAide.format("proto://127.0.0.1:9900?connect_timeout={}&send_timeout={}&login_timeout={}&resend_times={}",
            CONNECT_TIMEOUT, SEND_TIMEOUT, LOGIN_TIMEOUT, RESEND_TIMES));

    @Override
    protected TunnelTestInstance<NettyTerminalTunnel<Long>, MockNettyClient> create(Certificate<Long> certificate, boolean open) {
        MockNettyClient client = this.createEndpoint(certificate);
        NettyTerminalTunnel<Long> tunnel = this.newTunnel(client, open);
        if (certificate.isAutherized()) {
            tunnel.bind(client);
        }
        return new TunnelTestInstance<>(tunnel, client);
    }

    @Override
    protected MockNettyClient createEndpoint(Certificate<Long> certificate) {
        return new MockNettyClient(url, certificate);
    }

    private NettyTerminalTunnel<Long> newTunnel(MockNettyClient client, boolean open) {
        NettyTerminalTunnel<Long> tunnel =  new NettyTerminalTunnel<>(client, new CommonMessageFactory<>());
        if (open)
            tunnel.open();
        return tunnel;
    }

    // @Override
    // protected NettyClientTunnel<Long> createUnloginTunnel(SessionFactory<Long> sessionFactory, MessageBuilderFactory<Long> messageBuilderFactory) {
    //     return createUnloginTunnel(sessionFactory, messageBuilderFactory, (r, t) -> {
    //         CommonStageableFuture<Message<Long>> future = new CommonStageableFuture<>();
    //         MessageContext<Long> content = mockAs(MessageContext.class);
    //         when(content.willResponseFuture(anyLong())).thenReturn(future);
    //         return content;
    //     });
    // }
    //
    // protected NettyClientTunnel<Long> createLoginTunnel(BiFunction<Boolean, Tunnel<Long>, MessageContext<Long>> loginContentCreator) {
    //     NettyClientTunnel<Long> tunnel = createLoginTunnel();
    //     tunnel.setLoginContentCreator(loginContentCreator);
    //     mockBindLoginSession(tunnel);
    //     return tunnel;
    // }
    //
    // protected NettyClientTunnel<Long> createUnloginTunnel(BiFunction<Boolean, Tunnel<Long>, MessageContext<Long>> loginContentCreator) {
    //     NetSession<Long> session = mockAs(NetSession.class);
    //     SessionFactory<Long> sessionFactory = mockAs(SessionFactory.class);
    //     MessageBuilderFactory<Long> messageBuilderFactory = mockAs(MessageBuilderFactory.class);
    //     when(sessionFactory.createSession(as(any(NetTunnel.class)))).thenReturn(session);
    //     return createUnloginTunnel(sessionFactory, messageBuilderFactory, loginContentCreator);
    // }
    //
    // protected NettyClientTunnel<Long> createUnloginTunnel(SessionFactory<Long> sessionFactory, MessageBuilderFactory<Long> messageBuilderFactory, BiFunction<Boolean, Tunnel<Long>, MessageContext<Long>> loginContentCreator) {
    //     Channel channel = mock(Channel.class);
    //     NettyClientTunnel<Long> tunnel = new NettyClientTunnel<Long>(url, channel, config, sessionFactory, messageBuilderFactory, loginContentCreator);
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
    //     NettyClientTunnel<Long> tunnel = createUnloginTunnel();
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
    //     MessageContext<Long> content = mockAs(MessageContext.class);
    //     CommonStageableFuture<Message<Long>> future;
    //     NettyClientTunnel<Long> tunnel;
    //     Message<Long> message;
    //     BiFunction<Boolean, Tunnel<Long>, MessageContext<Long>> loginContentCreator = (r, t) -> content;
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
    //     NettyClientTunnel<Long> loginFailTunnel = createUnloginTunnel(loginContentCreator);
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
    //     NettyClientTunnel<Long> forkjoinTunnel = createUnloginTunnel(loginContentCreator);
    //     runParallel("forkjoinTunnel.login", forkjoinTunnel::login);
    //
    // }

}