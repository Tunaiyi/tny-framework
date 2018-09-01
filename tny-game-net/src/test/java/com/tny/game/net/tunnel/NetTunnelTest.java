package com.tny.game.net.tunnel;

import com.tny.game.net.session.*;
import org.junit.*;
import org.slf4j.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static test.MockAide.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class NetTunnelTest<T extends NetTunnel<Long>> extends TunnelTest<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetTunnelTest.class);

    @Override
    protected abstract T createUnloginTunnel();

    @Override
    protected T createLoginTunnel() {
        T tunnel = createUnloginTunnel();
        mockBindLoginSession(tunnel);
        return tunnel;
    }

    @Test
    public void bind() {
        NetTunnel<Long> tunnel;

        tunnel = createUnloginTunnel();
        assertFalse(tunnel.isLogin());

        mockBindLoginSession(tunnel);
        assertTrue(tunnel.isLogin());
    }

    protected NetSession<Long> mockBindLoginSession(NetTunnel<Long> tunnel) {
        NetSession<Long> session = mockLoginSession(tunnel);
        tunnel.bind(session);
        verify(session, times(1)).getCurrentTunnel();
        return session;
    }

    protected NetSession<Long> mockUnloginSession(NetTunnel<Long> tunnel) {
        NetSession<Long> session = mockAs(NetSession.class);
        when(session.getCurrentTunnel()).thenReturn(tunnel);
        bindMockSessionWith(session, createUnLoginCert());
        return session;
    }

    protected NetSession<Long> mockLoginSession(NetTunnel<Long> tunnel) {
        NetSession<Long> session = mockUnloginSession(tunnel);
        when(session.getCurrentTunnel()).thenReturn(tunnel);
        bindMockSessionWith(session, createLoginCert());
        return session;
    }

    protected void bindMockSessionWith(NetSession<Long> session, NetCertificate<Long> certificate) {
        when(session.getCertificate()).thenReturn(certificate);
        when(session.getUid()).thenReturn(certificate.getUserId());
        when(session.isLogin()).thenReturn(certificate.isLogin());
        when(session.getUserGroup()).thenReturn(certificate.getUserGroup());
    }

    public abstract void ping();

    public abstract void pong();

    public abstract void write() throws Exception;

    @Test
    public void getLastReadAt() {
        long now;
        long lastReadAt;
        TestMessages messages;
        NetTunnel<Long> tunnel;

        now = System.currentTimeMillis();
        tunnel = createLoginTunnel();
        lastReadAt = tunnel.getLastReadAt();
        assertTrue(lastReadAt >= now);

        now = System.currentTimeMillis();
        messages = new TestMessages(tunnel);
        tunnel.receive(messages.createPushMessage("1000"));
        lastReadAt = tunnel.getLastReadAt();
        assertTrue(lastReadAt >= now);
    }

    @Test
    public void getLastWriteAt() throws Exception {
        long now;
        long lastWriteAt;
        TestMessages messages;
        NetTunnel<Long> tunnel;

        now = System.currentTimeMillis();
        tunnel = createLoginTunnel();
        lastWriteAt = tunnel.getLastWriteAt();
        assertTrue(lastWriteAt >= now);

        now = System.currentTimeMillis();
        messages = new TestMessages(tunnel);
        tunnel.send(messages.createPushContent("1000"));
        lastWriteAt = tunnel.getLastWriteAt();
        assertTrue(lastWriteAt >= now);
    }

    @Test
    public void getMessageBuilderFactory() {
        NetTunnel<Long> tunnel;
        tunnel = createLoginTunnel();
        assertNotNull(tunnel.getMessageBuilderFactory());
    }

}