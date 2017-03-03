package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.ProtocolUtils;
import com.tny.game.net.dispatcher.exception.ValidatorFailException;
import com.tny.game.net.dispatcher.message.protoex.ProtoExMessageBuilderFactory;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.dispatcher.message.simple.SimpleMessageBuilderFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/application.xml"})
public class SessionHolderTest {

    @Autowired
    @Qualifier("testSessionHoslder")
    private NetSessionHolder sessionHolder;

    private static List<ServerSession> sessionList = new ArrayList<ServerSession>();

    private static List<Long> uidList = new ArrayList<Long>();

    private static List<Long> uidMoreList = new ArrayList<Long>();

    MessageBuilderFactory messageBuilderFactory = new SimpleMessageBuilderFactory();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        for (int index = 0; index < 100; index++) {
            ServerSession session = new TestSession(index);
            SessionHolderTest.sessionList.add(session);
            SessionHolderTest.uidList.add(new Long(index + ""));
            SessionHolderTest.uidMoreList.add(new Long(index + ""));
        }
        SessionHolderTest.uidMoreList.add(101L);
        SessionHolderTest.uidMoreList.add(102L);
        SessionHolderTest.uidMoreList.add(103L);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        this.sessionHolder.offlineAll(Session.DEFAULT_USER_GROUP);
        this.sessionHolder.removeAllChannel(Session.DEFAULT_USER_GROUP);
        for (ServerSession session : SessionHolderTest.sessionList) {
            this.sessionHolder.online(session, session.getCertificate());
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIsOnline() throws ValidatorFailException {
        for (ServerSession session : SessionHolderTest.sessionList)
            this.sessionHolder.online(session, session.getCertificate());
        Assert.assertTrue(this.sessionHolder.isOnline(Session.DEFAULT_USER_GROUP, 1L));
        Assert.assertFalse(this.sessionHolder.isOnline(Session.DEFAULT_USER_GROUP, 101L));
    }

    @Test
    public void testCreateGroup() {
        Assert.assertFalse(this.sessionHolder.isExistChannel(Session.DEFAULT_USER_GROUP, 1));
        Assert.assertTrue(this.sessionHolder.createChannel(Session.DEFAULT_USER_GROUP, 1));
        Assert.assertTrue(this.sessionHolder.isExistChannel(Session.DEFAULT_USER_GROUP, 1));
    }

    @Test
    public void testRemoveGroup() {
        Assert.assertFalse(this.sessionHolder.isExistChannel(Session.DEFAULT_USER_GROUP, 1));
        Assert.assertTrue(this.sessionHolder.createChannel(Session.DEFAULT_USER_GROUP, 1));
        Assert.assertTrue(this.sessionHolder.isExistChannel(Session.DEFAULT_USER_GROUP, 1));
        Assert.assertTrue(this.sessionHolder.removeChannel(Session.DEFAULT_USER_GROUP, 1));
        Assert.assertFalse(this.sessionHolder.isExistChannel(Session.DEFAULT_USER_GROUP, 1));
    }

    @Test
    public void testAddUserObjectObject() {
        Assert.assertFalse(this.sessionHolder.addChannelUser(Session.DEFAULT_USER_GROUP, 1, 1L));
        Assert.assertTrue(this.sessionHolder.createChannel(Session.DEFAULT_USER_GROUP, 1));
        Assert.assertTrue(this.sessionHolder.addChannelUser(Session.DEFAULT_USER_GROUP, 1, 1L));
    }

    @Test
    public void testGetGroupSize() {
        Assert.assertEquals(this.sessionHolder.getChannelSize(Session.DEFAULT_USER_GROUP, 1), 0);
        Assert.assertTrue(this.sessionHolder.createChannel(Session.DEFAULT_USER_GROUP, 1));
        Assert.assertTrue(this.sessionHolder.addChannelUser(Session.DEFAULT_USER_GROUP, 1, 1L));
        Assert.assertEquals(this.sessionHolder.getChannelSize(Session.DEFAULT_USER_GROUP, 1), 1);
        Assert.assertTrue(this.sessionHolder.addChannelUser(Session.DEFAULT_USER_GROUP, 1, 1L));
        Assert.assertEquals(this.sessionHolder.getChannelSize(Session.DEFAULT_USER_GROUP, 1), 1);
        Assert.assertTrue(this.sessionHolder.addChannelUser(Session.DEFAULT_USER_GROUP, 1, 2L));
        Assert.assertEquals(this.sessionHolder.getChannelSize(Session.DEFAULT_USER_GROUP, 1), 2);
    }

    @Test
    public void testAddUserObjectCollectionOfQ() {
        Assert.assertEquals(this.sessionHolder.addChannelUser(Session.DEFAULT_USER_GROUP, 1, SessionHolderTest.uidList), 0);
        Assert.assertTrue(this.sessionHolder.createChannel(Session.DEFAULT_USER_GROUP, 1));
        Assert.assertEquals(this.sessionHolder.addChannelUser(Session.DEFAULT_USER_GROUP, 1, SessionHolderTest.uidList), 100);
        Assert.assertTrue(this.sessionHolder.createChannel(Session.DEFAULT_USER_GROUP, 2));
        Assert.assertEquals(this.sessionHolder.addChannelUser(Session.DEFAULT_USER_GROUP, 2, SessionHolderTest.uidMoreList), 100);
    }

    @Test
    public void testIsInGroup() {
        Assert.assertFalse(this.sessionHolder.isInChannel(Session.DEFAULT_USER_GROUP, 1, 1L));
        Assert.assertTrue(this.sessionHolder.createChannel(Session.DEFAULT_USER_GROUP, 1));
        Assert.assertFalse(this.sessionHolder.isInChannel(Session.DEFAULT_USER_GROUP, 1, 1L));
        Assert.assertEquals(this.sessionHolder.addChannelUser(Session.DEFAULT_USER_GROUP, 1, SessionHolderTest.uidList), 100);
        Assert.assertTrue(this.sessionHolder.isInChannel(Session.DEFAULT_USER_GROUP, 1, 1L));
        Assert.assertFalse(this.sessionHolder.isInChannel(Session.DEFAULT_USER_GROUP, 1, 101L));
    }

    @Test
    public void testRemoveUserObjectObject() {
        Assert.assertTrue(this.sessionHolder.createChannel(Session.DEFAULT_USER_GROUP, 1));
        Assert.assertEquals(this.sessionHolder.addChannelUser(Session.DEFAULT_USER_GROUP, 1, SessionHolderTest.uidList), 100);
        Assert.assertTrue(this.sessionHolder.removeChannelUser(Session.DEFAULT_USER_GROUP, 1, 1L));
        Assert.assertFalse(this.sessionHolder.isInChannel(Session.DEFAULT_USER_GROUP, 1, 1L));
    }

    @Test
    public void testRemoveUserObjectCollectionOfQ() {
        Assert.assertTrue(this.sessionHolder.createChannel(Session.DEFAULT_USER_GROUP, 1));
        Assert.assertEquals(this.sessionHolder.addChannelUser(Session.DEFAULT_USER_GROUP, 1, SessionHolderTest.uidList), 100);
        Assert.assertEquals(this.sessionHolder.removeChannelUser(Session.DEFAULT_USER_GROUP, 1, SessionHolderTest.uidList), 100);
        Assert.assertEquals(this.sessionHolder.getChannelSize(Session.DEFAULT_USER_GROUP, 1), 0);
    }

    @Test
    public void testClearUser() {
        Assert.assertTrue(this.sessionHolder.createChannel(Session.DEFAULT_USER_GROUP, 1));
        Assert.assertEquals(this.sessionHolder.addChannelUser(Session.DEFAULT_USER_GROUP, 1, SessionHolderTest.uidList), 100);
        this.sessionHolder.clearChannelUser(Session.DEFAULT_USER_GROUP, 1);
        Assert.assertEquals(this.sessionHolder.getChannelSize(Session.DEFAULT_USER_GROUP, 1), 0);
    }

    private static Protocol protocol = ProtocolUtils.protocol(1000);
    private static ResultCode code = CoreResponseCode.WAIT_TIMEOUT;

    @Test
    public void testSend2UserObjectMessage() {
        Assert.assertTrue(this.sessionHolder.send2User(Session.DEFAULT_USER_GROUP, 1L, SessionHolderTest.protocol, SessionHolderTest.code, null));
        Assert.assertFalse(this.sessionHolder.send2User(Session.DEFAULT_USER_GROUP, 101L, SessionHolderTest.protocol, SessionHolderTest.code, null));
    }

    @Test
    public void testSend2UserCollectionOfQMessage() {
        Assert.assertEquals(this.sessionHolder.send2User(Session.DEFAULT_USER_GROUP, SessionHolderTest.uidList, SessionHolderTest.protocol, SessionHolderTest.code, null), 100);
        Assert.assertEquals(this.sessionHolder.send2User(Session.DEFAULT_USER_GROUP, SessionHolderTest.uidMoreList, SessionHolderTest.protocol, SessionHolderTest.code, null), 100);
    }

    @Test
    public void testSendGroup() {
        Assert.assertFalse(this.sessionHolder.send2Channel(Session.DEFAULT_USER_GROUP, 1, SessionHolderTest.protocol, SessionHolderTest.code, null));
        Assert.assertTrue(this.sessionHolder.createChannel(Session.DEFAULT_USER_GROUP, 1));
        Assert.assertTrue(this.sessionHolder.send2Channel(Session.DEFAULT_USER_GROUP, 1, SessionHolderTest.protocol, SessionHolderTest.code, null));
    }

    // @Test
    // public void testRemoveSession() {
    // Assert.assertTrue(sessionHolder.isOnline(1));
    // sessionHolder.removeSession(1);
    // Assert.assertFalse(sessionHolder.isOnline(1));
    // }

    private static class TestSession extends ChannelServerSession {

        private TestSession(long userId) {
            super(new TestChannel(), LoginCertificate.createLogin(System.currentTimeMillis(), userId, DEFAULT_USER_GROUP, false));
            this.messageBuilderFactory = new ProtoExMessageBuilderFactory();
        }

//		@Override
//		public boolean isAskerLogin() {
//			return false;
//		}

        @Override
        public void disconnect() {
        }

        @Override
        public String getHostName() {
            return null;
        }

        @Override
        public boolean isConnect() {
            return true;
        }

        @Override
        protected int createResponseNumber() {
            return 0;
        }

        @Override
        public void login(LoginCertificate loginInfo) {

        }

    }
}
