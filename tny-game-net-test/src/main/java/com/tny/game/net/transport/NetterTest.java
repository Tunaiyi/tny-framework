package com.tny.game.net.transport;

import org.junit.*;

import java.time.Instant;

/**
 * Created by Kun Yang on 2018/8/12.
 */
public abstract class NetterTest<C extends Netter<Long>> {

    protected static Long uid = 100L;
    protected static Long unloginUid = null;
    protected static String userGroup = Certificates.DEFAULT_USER_TYPE;
    protected static Long certificateId = System.currentTimeMillis();

    protected Certificate<Long> createUnLoginCert() {
        return Certificates.createUnautherized(unloginUid);
    }

    protected Certificate<Long> createLoginCert() {
        return Certificates.createAutherized(certificateId, uid, Instant.now());
    }

    protected Certificate<Long> createLoginCert(long certificateId, Long uid) {
        return Certificates.createAutherized(certificateId, uid, Instant.now());
    }

    protected NetterTest() {
    }

    public abstract C communicator(Certificate<Long> certificate);

    @Test
    public void getUserId() {
        C loginCommunicator = communicator(createLoginCert());
        Assert.assertEquals(uid, loginCommunicator.getUserId());
    }

    @Test
    public void getUserType() {
        C loginCommunicator = communicator(createLoginCert());
        Assert.assertEquals(userGroup, loginCommunicator.getUserType());
    }

    @Test
    public void isClosed() {
        C loginCommunicator = communicator(createLoginCert());
        Assert.assertFalse(loginCommunicator.isClosed());
        loginCommunicator.close();
        Assert.assertTrue(loginCommunicator.isClosed());
        loginCommunicator.close();
        Assert.assertTrue(loginCommunicator.isClosed());
    }
}