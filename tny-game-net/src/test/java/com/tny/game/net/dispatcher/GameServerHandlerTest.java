package com.tny.game.net.dispatcher;

import com.tny.game.net.base.NetAppContext;
import com.tny.game.net.dispatcher.message.simple.SimpleMessageBuilderFactory;
import com.tny.game.net.dispatcher.message.simple.SimpleRequest;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.concurrent.EventExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.SocketAddress;
import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/application.xml"})
public class GameServerHandlerTest {

    @Autowired
    private NetAppContext appContext;

    private MessageHandler handler;

    @Before
    public void setUp() throws Exception {
        this.handler = new MessageHandler();
        this.handler.setAppContext(this.appContext);
    }

    @After
    public void tearDown() throws Exception {
    }

    private Channel channel = new TestChannel();

    private ChannelHandlerContext channelContext = new TestChannelHandlerContext();

    private static MessageBuilderFactory messageBuilderFactory = new SimpleMessageBuilderFactory();

    //	{
    //		channelContext.attr(NetAttributeKey.TELNET_SEESSION).set(channel);
    //	}

    // * id Long
    // * userType String
    // * group String
    // * ip String
    @Test
    public void testMessageReceivedChannelHandlerContextMessageEvent() throws Exception {
        SimpleRequest request = this.request(TestContorl.login, 171772272L, "User", "", "127.0.0.1");
        this.handler.channelRead0(this.channelContext, request);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMessageReceivedChannelHandlerContextMessageEventUnlogin() throws Exception {
        SimpleRequest request = this.request(TestContorl.login, 1L, "User", "", "127.0.0.1");
        this.handler.channelRead0(this.channelContext, request);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public SimpleRequest request(int protocol, Object... objects) {
        return this.request(protocol, null, objects);
    }

    public SimpleRequest request(int protocol, BaseSession session, Object... objects) {
        SimpleRequest request = (SimpleRequest) GameServerHandlerTest.messageBuilderFactory
                .newRequestBuilder(session)
                .setProtocol(protocol)
                .addParameter(Arrays.asList(objects))
                .build();
        if (session != null)
            request.owner(session);
        return request;
    }

    class TestChannelHandlerContext extends DefaultAttributeMap implements ChannelHandlerContext {

        //		private Object object;

        @Override
        public Channel channel() {
            return GameServerHandlerTest.this.channel;
        }

        @Override
        public EventExecutor executor() {
            return null;
        }

        @Override
        public String name() {
            return null;
        }

        @Override
        public ChannelHandler handler() {
            return null;
        }

        @Override
        public boolean isRemoved() {
            return false;
        }

        @Override
        public ChannelHandlerContext fireChannelRegistered() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelUnregistered() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelActive() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelInactive() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
            return null;
        }

        @Override
        public ChannelHandlerContext fireUserEventTriggered(Object event) {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelRead(Object msg) {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelReadComplete() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelWritabilityChanged() {
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
            return null;
        }

        @Override
        public ChannelFuture disconnect() {
            return null;
        }

        @Override
        public ChannelFuture close() {
            return null;
        }

        @Override
        public ChannelFuture deregister() {
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture disconnect(ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture close(ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture deregister(ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelHandlerContext read() {
            return null;
        }

        @Override
        public ChannelFuture write(Object msg) {
            return null;
        }

        @Override
        public ChannelFuture write(Object msg, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelHandlerContext flush() {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg) {
            return null;
        }

        @Override
        public ChannelPipeline pipeline() {
            return GameServerHandlerTest.this.channel.pipeline();
        }

        @Override
        public ByteBufAllocator alloc() {
            return null;
        }

        @Override
        public ChannelPromise newPromise() {
            return null;
        }

        @Override
        public ChannelProgressivePromise newProgressivePromise() {
            return null;
        }

        @Override
        public ChannelFuture newSucceededFuture() {
            return null;
        }

        @Override
        public ChannelFuture newFailedFuture(Throwable cause) {
            return null;
        }

        @Override
        public ChannelPromise voidPromise() {
            return null;
        }

    }

    class TestChannel implements Channel {

        @Override
        public int compareTo(Channel o) {

            return 0;
        }

        @Override
        public ChannelFuture write(Object message) {

            return null;
        }

        @Override
        public boolean isWritable() {
            return false;
        }

        @Override
        public long bytesBeforeUnwritable() {
            return 0;
        }

        @Override
        public long bytesBeforeWritable() {
            return 0;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public ChannelFuture disconnect() {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress) {

            return null;
        }

        @Override
        public ChannelFuture close() {

            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress) {

            return null;
        }

        @Override
        public <T> Attribute<T> attr(AttributeKey<T> key) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> boolean hasAttr(AttributeKey<T> attributeKey) {
            return false;
        }

        @Override
        public ChannelId id() {
            return null;
        }

        @Override
        public EventLoop eventLoop() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Channel parent() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelConfig config() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isRegistered() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isActive() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public ChannelMetadata metadata() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public SocketAddress localAddress() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public SocketAddress remoteAddress() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelFuture closeFuture() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Unsafe unsafe() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelPipeline pipeline() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ByteBufAllocator alloc() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelPromise newPromise() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelProgressivePromise newProgressivePromise() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelFuture newSucceededFuture() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelFuture newFailedFuture(Throwable cause) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelPromise voidPromise() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelFuture deregister() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelFuture disconnect(ChannelPromise promise) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelFuture close(ChannelPromise promise) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelFuture deregister(ChannelPromise promise) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Channel read() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelFuture write(Object msg, ChannelPromise promise) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Channel flush() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg) {
            // TODO Auto-generated method stub
            return null;
        }
    }

}
