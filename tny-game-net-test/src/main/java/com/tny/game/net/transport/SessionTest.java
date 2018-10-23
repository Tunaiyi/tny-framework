package com.tny.game.net.transport;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.session.Session;
import org.junit.*;

import java.util.List;

import static com.tny.game.test.TestAide.*;
import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class SessionTest<S extends Session<Long>> extends NetterTest<S> {

    protected abstract S createSession(Certificate<Long> certificate);

    protected S createSession() {
        return createSession(createLoginCert());
    }

    @Override
    public S communicator(Certificate<Long> certificate) {
        return createSession(certificate);
    }

    // @Override
    // public S unloginCommunicator() {
    //     return createSession();
    // }
    //
    // @Override
    // public S loginCommunicator() {
    //     return createLoginSession();
    // }

    protected abstract void doOffline(S session);

    @Test
    public void getId() {
        S loginSession = createSession();
        assertTrue(loginSession.getId() > 0);
        S session1 = createSession();
        S session2 = createSession();
        S session3 = createSession();
        assertEquals(1, session2.getId() - session1.getId());
        assertEquals(1, session3.getId() - session2.getId());
    }

    @Test
    public void isLogin() {
        S loginSession = createSession();
        assertTrue(loginSession.isLogin());
    }

    @Test
    public void attributes() {
        S loginSession = createSession();
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
        S loginSession = createSession();
        assertTrue(loginSession.isOnline());
    }

    @Test
    public void getCertificate() {
        S loginSession = createSession();
        assertNotNull(loginSession.getCertificate());
        assertTrue(loginSession.getCertificate().isAutherized());
    }

    @Test
    public void isOffline() {
        S loginSession = createSession();
        assertFalse(loginSession.isOffline());
    }

    @Test
    public void getOfflineTime() {
        S loginSession = createSession();
        assertEquals(loginSession.getOfflineTime(), 0L);
        long now = System.currentTimeMillis();
        doOffline(loginSession);
        assertTrue(loginSession.getOfflineTime() >= now);
    }

    // @Test
    // public void receiveExcludes() {
    //     assertMessageMode(Tunnel::excludeReceiveModes, Tunnel::isExcludeReceiveMode);
    // }
    //
    // @Test
    // public void sendExcludes() {
    //     assertMessageMode(Tunnel::excludeSendModes, Tunnel::isExcludeSendMode);
    // }
    //
    // private void assertMessageMode(BiConsumer<T, MessageMode[]> setModes, BiPredicate<T, MessageMode> testMode) {
    //     T tunnel;
    //     tunnel = createLoginTunnel();
    //     assertMessageMode(tunnel, setModes, testMode, MessageMode.REQUEST);
    //     tunnel = createLoginTunnel();
    //     assertMessageMode(tunnel, setModes, testMode, MessageMode.RESPONSE);
    //     tunnel = createLoginTunnel();
    //     assertMessageMode(tunnel, setModes, testMode, MessageMode.PUSH);
    //     tunnel = createLoginTunnel();
    //     assertMessageMode(tunnel, setModes, testMode, MessageMode.PING);
    //     tunnel = createLoginTunnel();
    //     assertMessageMode(tunnel, setModes, testMode, MessageMode.PONG);
    //     tunnel = createLoginTunnel();
    //     assertMessageMode(tunnel, setModes, testMode, MessageMode.values());
    // }

    // @Test
    // public void getCurrentTunnel() {
    //     S session = createLoginSession();
    //     assertNotNull(session.getTunnel());
    //     doOffline(session);
    //     assertNotNull(session.getTunnel());
    //     session.close();
    //     assertNotNull(session.getTunnel());
    // }

    @Test
    public abstract void send() throws InterruptedException;

    // @Test
    // public abstract void receive() throws InterruptedException;

    // @Test
    // public abstract void resend() throws InterruptedException;

}