package com.tny.game.net.netty;

import com.tny.game.common.config.*;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.exception.TunnelWriteException;
import com.tny.game.net.message.*;
import com.tny.game.net.netty.NettyClientTunnelTest.TestNettyClientTunnel;
import com.tny.game.net.session.*;
import com.tny.game.net.tunnel.*;
import io.netty.channel.Channel;
import org.junit.*;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.function.BiFunction;

import static com.tny.game.common.utils.ObjectAide.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static test.MockAide.any;
import static test.MockAide.anyLong;
import static test.MockAide.eq;
import static test.MockAide.*;
import static test.TestAide.*;

/**
 * Created by Kun Yang on 2018/8/27.
 */
public class NettyClientTunnelTest extends NettyTunnelTest<TestNettyClientTunnel> {

    private static Config config = ConfigLib.newConfig(Collections.emptyMap());

    private static final int CONNECT_TIMEOUT = 140;
    private static final int SEND_TIMEOUT = 0;
    private static final int LOGIN_TIMEOUT = 100;
    private static final int RESEND_TIMES = 130;

    private URL url = URL.valueOf(StringAide.format("proto://127.0.0.1:9900?connect_timeout={}&send_timeout={}&login_timeout={}&resend_times={}",
            CONNECT_TIMEOUT, SEND_TIMEOUT, LOGIN_TIMEOUT, RESEND_TIMES));

    @Override
    protected TestNettyClientTunnel createUnloginTunnel(SessionFactory<Long> sessionFactory, MessageBuilderFactory<Long> messageBuilderFactory) {
        return createUnloginTunnel(sessionFactory, messageBuilderFactory, (r, t) -> {
            CommonStageableFuture<Message<Long>> future = new CommonStageableFuture<>();
            MessageContent<Long> content = mockAs(MessageContent.class);
            when(content.messageFuture(anyLong())).thenReturn(future);
            return content;
        });
    }

    protected TestNettyClientTunnel createLoginTunnel(BiFunction<Boolean, Tunnel<Long>, MessageContent<Long>> loginContentCreator) {
        TestNettyClientTunnel tunnel = createLoginTunnel();
        tunnel.setLoginContentCreator(loginContentCreator);
        mockBindLoginSession(tunnel);
        return tunnel;
    }

    protected TestNettyClientTunnel createUnloginTunnel(BiFunction<Boolean, Tunnel<Long>, MessageContent<Long>> loginContentCreator) {
        NetSession<Long> session = mockAs(NetSession.class);
        SessionFactory<Long> sessionFactory = mockAs(SessionFactory.class);
        MessageBuilderFactory<Long> messageBuilderFactory = mockAs(MessageBuilderFactory.class);
        when(sessionFactory.createSession(as(any(NetTunnel.class)))).thenReturn(session);
        return createUnloginTunnel(sessionFactory, messageBuilderFactory, loginContentCreator);
    }

    protected TestNettyClientTunnel createUnloginTunnel(SessionFactory<Long> sessionFactory, MessageBuilderFactory<Long> messageBuilderFactory, BiFunction<Boolean, Tunnel<Long>, MessageContent<Long>> loginContentCreator) {
        Channel channel = mock(Channel.class);
        TestNettyClientTunnel tunnel = new TestNettyClientTunnel(url, channel, config, sessionFactory, messageBuilderFactory, loginContentCreator);
        when(channel.isActive()).thenReturn(true);
        when(channel.remoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 1000));
        when(channel.localAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 9000));
        return tunnel;
    }

    @Test
    public void getLoginContentCreator() {
        assertNotNull(createUnloginTunnel().getLoginContentCreator());
    }

    @Test
    public void getUrl() {
        assertEquals(url, createUnloginTunnel().getUrl());
    }

    @Test
    public void getLoginTimeout() {
        assertEquals(LOGIN_TIMEOUT, createUnloginTunnel().getLoginTimeout());
    }

    @Test
    public void getSendTimeout() {
        assertEquals(SEND_TIMEOUT, createUnloginTunnel().getSendTimeout());
    }

    @Test
    public void getResendTimes() {
        assertEquals(RESEND_TIMES, createUnloginTunnel().getResendTimes());
    }

    @Test
    public void resetChannel() {
        TestNettyClientTunnel tunnel = createUnloginTunnel();
        Channel channel = mockTunnelChannel(tunnel);
        Channel newChannel = mock(Channel.class);

        assertFalse(tunnel.resetChannel(channel));
        assertFalse(tunnel.resetChannel(newChannel));

        when(channel.isActive()).thenReturn(false);
        when(newChannel.isActive()).thenReturn(false);
        assertFalse(tunnel.resetChannel(newChannel));
        when(newChannel.isActive()).thenReturn(true);
        assertTrue(tunnel.resetChannel(newChannel));
    }

    @Test
    public void login() throws InterruptedException, ExecutionException, TimeoutException, TunnelWriteException {
        MessageContent<Long> content = mockAs(MessageContent.class);
        CommonStageableFuture<Message<Long>> future;
        TestNettyClientTunnel tunnel;
        Message<Long> message;
        BiFunction<Boolean, Tunnel<Long>, MessageContent<Long>> loginContentCreator = (r, t) -> content;

        // 正常登录
        future = mockAs(CommonStageableFuture.class);
        message = mockAs(Message.class);
        when(content.messageFuture(anyLong())).thenReturn(future);
        when(future.get(anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(message);
        when(future.get()).thenReturn(message);
        when(message.getCode()).thenReturn(ResultCode.SUCCESS_CODE);
        loginContentCreator = (r, t) -> content;
        tunnel = createUnloginTunnel(loginContentCreator);
        tunnel.login();

        // 登录失败
        future = mockAs(CommonStageableFuture.class);
        message = mockAs(Message.class);
        when(content.messageFuture(anyLong())).thenReturn(future);
        when(future.get(anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(message);
        when(future.get()).thenReturn(message);
        when(message.getCode()).thenReturn(ResultCode.FAILURE_CODE);
        TestNettyClientTunnel loginFailTunnel = createUnloginTunnel(loginContentCreator);
        assertRunWithException("login exception", loginFailTunnel::login, TunnelWriteException.class);

        // forkjoin 登录
        future = mockAs(CommonStageableFuture.class);
        message = mockAs(Message.class);
        when(content.messageFuture(anyLong())).thenReturn(future);
        when(future.isDone()).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(true);
        when(future.get(anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(message);
        when(future.get()).thenReturn(message);
        when(message.getCode()).thenReturn(ResultCode.FAILURE_CODE);
        TestNettyClientTunnel forkjoinTunnel = createUnloginTunnel(loginContentCreator);
        runParallel("forkjoinTunnel.login", forkjoinTunnel::login);

    }

    @Override
    public void write() throws Exception {

    }

    public static class TestNettyClientTunnel extends NettyClientTunnel<Long> {

        public TestNettyClientTunnel(URL url, Channel channel, AppConfiguration configuration, BiFunction<Boolean, Tunnel<Long>, MessageContent<Long>> loginContentCreator) {
            super(url, channel, configuration, loginContentCreator);
        }

        public TestNettyClientTunnel(URL url, Channel channel, Config config, SessionFactory<Long> sessionFactory, MessageBuilderFactory<Long> messageBuilderFactory, BiFunction<Boolean, Tunnel<Long>, MessageContent<Long>> loginContentCreator) {
            super(url, channel, config, sessionFactory, messageBuilderFactory, loginContentCreator);
        }


        public TestNettyClientTunnel setLoginContentCreator(BiFunction<Boolean, Tunnel<Long>, MessageContent<Long>> loginContentCreator) {
            this.loginContentCreator = loginContentCreator;
            return this;
        }

    }
}