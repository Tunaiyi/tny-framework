package com.tny.game.net.session;

import com.tny.game.net.utils.SessionConstants;
import org.junit.*;

/**
 * Created by Kun Yang on 2018/8/12.
 */
public abstract class CommunicatorTest<C extends Communicator<Long>> {

    protected static Long uid = 100L;
    protected static Long unloginUid = 0L;
    protected static String userGroup = SessionConstants.DEFAULT_USER_GROUP;
    protected static Long certificateId = System.currentTimeMillis();

    protected NetCertificate<Long> createUnLoginCert() {
        return NetCertificate.createUnLogin(unloginUid);
    }

    protected NetCertificate<Long> createLoginCert() {
        return NetCertificate.createLogin(certificateId, uid);
    }

    protected NetCertificate<Long> createLoginCert(long certificateId, Long uid) {
        return NetCertificate.createLogin(certificateId, uid);
    }

    protected CommunicatorTest() {
    }

    public abstract C unloginCommunicator();

    public abstract C loginCommunicator();

    @Test
    public void getUID() {
        C unloginCommunicator = unloginCommunicator();
        Assert.assertEquals(unloginUid, unloginCommunicator.getUid());
        C loginCommunicator = loginCommunicator();
        Assert.assertEquals(uid, loginCommunicator.getUid());
    }

    @Test
    public void getUserGroup() {
        C unloginCommunicator = unloginCommunicator();
        Assert.assertEquals(SessionConstants.UNLOGIN_USER_GROUP, unloginCommunicator.getUserGroup());
        C loginCommunicator = loginCommunicator();
        Assert.assertEquals(userGroup, loginCommunicator.getUserGroup());
    }

    @Test
    public abstract void send() throws InterruptedException;

    @Test
    public abstract void receive() throws InterruptedException;

    @Test
    public abstract void resend() throws InterruptedException;

    @Test
    public void isClosed() {
        C unloginCommunicator = unloginCommunicator();
        Assert.assertFalse(unloginCommunicator.isClosed());
        Assert.assertNotNull(unloginCommunicator.close());
        Assert.assertTrue(unloginCommunicator.isClosed());
        C loginCommunicator = loginCommunicator();
        Assert.assertFalse(loginCommunicator.isClosed());
        Assert.assertNotNull(loginCommunicator.close());
        Assert.assertTrue(loginCommunicator.isClosed());
        Assert.assertNotNull(loginCommunicator.close());
        Assert.assertTrue(loginCommunicator.isClosed());
    }

}