package com.tny.game.net.session;

import com.tny.game.common.context.Attributes;
import org.junit.*;

import java.util.List;

import static org.junit.Assert.*;
import static test.TestAide.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class SessionTest<S extends Session<Long>> extends CommunicatorTest<S> {

    protected abstract S createUnloginSession();

    protected abstract S createLoginSession();

    @Override
    public S unloginCommunicator() {
        return createUnloginSession();
    }

    @Override
    public S loginCommunicator() {
        return createLoginSession();
    }

    protected abstract void doOffline(S session);

    @Test
    public void getId() {
        S loginSession = createLoginSession();
        S unloginSession = createUnloginSession();
        assertTrue(loginSession.getId() > 0);
        assertTrue(unloginSession.getId() > 0);
        S session1 = createUnloginSession();
        S session2 = createUnloginSession();
        S session3 = createUnloginSession();
        assertEquals(1, session2.getId() - session1.getId());
        assertEquals(1, session3.getId() - session2.getId());
    }

    @Test
    public void isLogin() {
        S loginSession = createLoginSession();
        S unloginSession = createUnloginSession();
        assertTrue(loginSession.isLogin());
        assertFalse(unloginSession.isLogin());

    }

    @Test
    public void attributes() {
        S loginSession = createLoginSession();
        List<Attributes> attributesList = callParallel("attributes", 20, () -> {
            Attributes attributes = loginSession.attributes();
            assertNotNull(attributes);
            return attributes;
        });
        Attributes expected = null;
        assertTrue(attributesList.size() > 2);
        for (Attributes checkOne : attributesList) {
            assertNotNull(checkOne);
            if (expected == null)
                expected = checkOne;
            assertSame(expected, checkOne);
        }
    }

    @Test
    public void isOnline() {
        S loginSession = createLoginSession();
        S unloginSession = createUnloginSession();
        assertTrue(loginSession.isOnline());
        assertTrue(unloginSession.isOnline());
    }

    @Test
    public void getCertificate() {
        S loginSession = createLoginSession();
        S unloginSession = createUnloginSession();
        assertNotNull(loginSession.getCertificate());
        assertNotNull(unloginSession.getCertificate());
        assertTrue(loginSession.getCertificate().isLogin());
        assertFalse(unloginSession.getCertificate().isLogin());
    }

    @Test
    public void isOffline() {
        S loginSession = createLoginSession();
        S unloginSession = createUnloginSession();
        assertFalse(loginSession.isOffline());
        assertFalse(unloginSession.isOffline());
    }

    @Test
    public void getOfflineTime() {
        S loginSession = createLoginSession();
        S unloginSession = createUnloginSession();
        assertEquals(loginSession.getOfflineTime(), 0L);
        assertEquals(unloginSession.getOfflineTime(), 0L);
        long now = System.currentTimeMillis();
        doOffline(loginSession);
        doOffline(unloginSession);
        assertTrue(loginSession.getOfflineTime() >= now);
        assertTrue(unloginSession.getOfflineTime() >= now);
    }

    @Test
    public void getCurrentTunnel() {
        S session = createLoginSession();
        assertNotNull(session.getCurrentTunnel());
        doOffline(session);
        assertNotNull(session.getCurrentTunnel());
        session.close();
        assertNotNull(session.getCurrentTunnel());
    }

    @Test
    public abstract void send() throws InterruptedException;

    @Test
    public abstract void receive() throws InterruptedException;

    @Test
    public abstract void resend() throws InterruptedException;

}