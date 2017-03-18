package com.tny.game.net.dispatcher;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.netty.NettyAttrKeys;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.dispatcher.exception.DispatchException;
import com.tny.game.net.dispatcher.listener.DispatchExceptionEvent;
import com.tny.game.net.dispatcher.listener.DispatchMessageErrorEvent;
import com.tny.game.net.dispatcher.listener.ExecuteMessageEvent;
import com.tny.game.net.dispatcher.listener.MessageDispatcherListener;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.message.MessageDispatcher;
import com.tny.game.net.message.simple.SimpleChannelServerSession;
import com.tny.game.net.message.simple.SimpleMessageBuilderFactory;
import com.tny.game.net.message.simple.SimpleRequest;
import com.tny.game.net.session.Session;
import io.netty.channel.Channel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/application.xml"})
public class SpringControllerDispatcherTest {

    @Autowired
    private MessageDispatcher dispatcher;

    @Autowired
    private ControllerChecker checker;

    @Autowired
    private AppContext context;

    private static int number = 1;

    private Channel channel = new TestChannel();

    private static MessageBuilderFactory messageBuilderFactory = new SimpleMessageBuilderFactory();


    private static MessageDispatcherListener listener = new MessageDispatcherListener() {

        @Override
        public void executeException(DispatchMessageErrorEvent errorEvent) {
            long num = errorEvent.getMessage().getParameter(0, Long.class);
            Assert.assertEquals(num, 171772272);
            System.out.println("Exception");
        }

        @Override
        public void execute(ExecuteMessageEvent event) {

        }

        @Override
        public void executeFinish(ExecuteMessageEvent event) {

        }

        @Override
        public void executeDispatchException(DispatchExceptionEvent event) {
            long num = event.getMessage().getParameter(0, Long.class);
            Assert.assertEquals(num, 171772272);
            int code = event.getException().getResultCode().getCode();
            System.out.println("DispacheException : " + event.getException().getResultCode());
        }
    };

    @PostConstruct
    public void beforClass() {
        this.dispatcher.addDispatcherRequestListener(SpringControllerDispatcherTest.listener);
    }

    @Before
    public void setUp() throws Exception {
        this.context.initContext(null);
        this.channel.attr(NettyAttrKeys.MSG_BUILDER_FACTOR).set(new SimpleMessageBuilderFactory());
        this.channel.attr(NettyAttrKeys.REQUEST_CHECKERS).set(Arrays.asList(this.checker));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDispatchUnlogin() throws DispatchException {
        ServerSession session = new SimpleChannelServerSession(this.channel, LoginCertificate.createUnLogin());
        SimpleRequest request = this.request(TestContorl.test1, session, "ddd", 171772272);
        CommandResult response = this.dispatcher.dispatch(request, session, this.context).invoke();
        Assert.assertEquals(response.getResultCode(), CoreResponseCode.UNLOGIN);
    }

    @Test
    public void testDispatchNoPermissions() throws DispatchException {
        ServerSession session = new SimpleChannelServerSession(this.channel, LoginCertificate.createLogin(171772272, "giftSystem"));
        SimpleRequest request = this.request(TestContorl.test2, session, "ddd", 171772272);
        Assert.assertEquals(this.dispatcher.dispatch(request, session, this.context).invoke().getResultCode(), CoreResponseCode.NO_PERMISSIONS);
    }

    @Test
    public void testDispatchFalsify() throws DispatchException {
        ServerSession session = new SimpleChannelServerSession(this.channel, LoginCertificate.createLogin(171772272, "loginSystem"));
        SimpleRequest request = this.request(TestContorl.test3, session, null, 171772272);
        // request.setKeyBytes(new byte [1]);
        Assert.assertEquals(this.dispatcher.dispatch(request, session, this.context).invoke().getResultCode(), CoreResponseCode.FALSIFY);
    }

    @Test
    public void testDispatchException() throws DispatchException {
        ServerSession session = new SimpleChannelServerSession(this.channel, LoginCertificate.createLogin(171772272, Session.DEFAULT_USER_GROUP));
        SimpleRequest request = this.request(TestContorl.test4, session, "ddd", 171772272);
        CommandResult response = this.dispatcher.dispatch(request, session, this.context).invoke();
        Assert.assertEquals(response.getResultCode(), CoreResponseCode.EXECUTE_EXCEPTION);
    }

    @Test
    public void testDispatchNomalUnlogin() throws DispatchException {
        ServerSession session = new SimpleChannelServerSession(this.channel, LoginCertificate.createUnLogin());
        SimpleRequest request = this.request(TestContorl.nomalUnlogin, session, "ddd", 171772272, SpringControllerDispatcherTest.number++);
        Assert.assertEquals(this.dispatcher.dispatch(request, session, this.context).invoke().getResultCode(), CoreResponseCode.UNLOGIN);
    }

    @Test
    public void testDispatchNomalLogin() throws DispatchException {
        ServerSession session = new SimpleChannelServerSession(this.channel, LoginCertificate.createLogin(171772272, Session.DEFAULT_USER_GROUP));
        SimpleRequest request = this.request(TestContorl.nomalLogin, session, "ddd", 171772272, SpringControllerDispatcherTest.number++);
        this.dispatcher.dispatch(request, session, this.context).invoke();
    }

    @Test
    public void testDispatchSystemLogin() throws DispatchException {
        ServerSession session = new SimpleChannelServerSession(this.channel, LoginCertificate.createLogin(171772272, "loginSystem"));
        SimpleRequest request = this.request(TestContorl.systemLogin, session, "ddd", 171772272, SpringControllerDispatcherTest.number++);
        this.dispatcher.dispatch(request, session, this.context).invoke();
    }

    public SimpleRequest request(int protocol, String key, Object... objects) {
        return this.request(protocol, null, key, objects);
    }

    public SimpleRequest request(int protocol, ServerSession session, String key, Object... objects) {
        SimpleRequest request = (SimpleRequest) SpringControllerDispatcherTest.messageBuilderFactory.newRequestBuilder(session)
                .setProtocol(protocol)
                .addParameter(Arrays.asList(objects))
                .setRequestVerifier(key != null ? (MessageSignGenerator) Request -> "ddd" : null).build();
        if (session != null)
            request.owner(session);
        return request;
    }

}
