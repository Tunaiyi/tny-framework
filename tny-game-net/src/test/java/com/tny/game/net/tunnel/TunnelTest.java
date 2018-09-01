package com.tny.game.net.tunnel;

import com.tny.game.net.message.MessageMode;
import com.tny.game.net.session.CommunicatorTest;
import org.junit.*;

import java.util.*;
import java.util.function.*;

import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class TunnelTest<T extends Tunnel<Long>> extends CommunicatorTest<T> {

    protected abstract T createLoginTunnel();

    protected abstract T createUnloginTunnel();

    @Override
    public T unloginCommunicator() {
        return createUnloginTunnel();
    }

    @Override
    public T loginCommunicator() {
        return createLoginTunnel();
    }

    @Test
    public void getId() {
        T tunnel1 = createLoginTunnel();
        T tunnel2 = createLoginTunnel();
        T tunnel3 = createLoginTunnel();
        assertTrue(tunnel1.getId() != tunnel2.getId());
        assertTrue(tunnel2.getId() != tunnel3.getId());
        assertTrue(tunnel3.getId() != tunnel1.getId());
    }

    @Test
    public void attributes() {
        T tunnel = createLoginTunnel();
        assertNotNull(tunnel.attributes());
        assertNotNull(tunnel.attributes());
    }

    @Test
    public void getSession() {
        T tunnel = createLoginTunnel();
        assertNotNull(tunnel.getSession());
    }

    @Test
    public void remoteAddress() {
        T tunnel = createLoginTunnel();
        assertNotNull(tunnel.remoteAddress());
    }

    @Test
    public void localAddress() {
        T tunnel = createLoginTunnel();
        assertNotNull(tunnel.localAddress());
    }

    @Test
    public void isLogin() {
        T loginTunnel = createLoginTunnel();
        assertTrue(loginTunnel.isLogin());
        T unloginTunnel = createUnloginTunnel();
        assertFalse(unloginTunnel.isLogin());
    }

    @Test
    public void receiveExcludes() {
        assertMessageMode(Tunnel::receiveExcludes, Tunnel::isReceiveExclude);
    }

    @Test
    public void sendExcludes() {
        assertMessageMode(Tunnel::sendExcludes, Tunnel::isSendExclude);
    }

    private void assertMessageMode(BiConsumer<T, MessageMode[]> setModes, BiPredicate<T, MessageMode> testMode) {
        T tunnel;
        tunnel = createLoginTunnel();
        assertMessageMode(tunnel, setModes, testMode, MessageMode.REQUEST);
        tunnel = createLoginTunnel();
        assertMessageMode(tunnel, setModes, testMode, MessageMode.RESPONSE);
        tunnel = createLoginTunnel();
        assertMessageMode(tunnel, setModes, testMode, MessageMode.PUSH);
        tunnel = createLoginTunnel();
        assertMessageMode(tunnel, setModes, testMode, MessageMode.PING);
        tunnel = createLoginTunnel();
        assertMessageMode(tunnel, setModes, testMode, MessageMode.PONG);
        tunnel = createLoginTunnel();
        assertMessageMode(tunnel, setModes, testMode, MessageMode.values());
    }


    private void assertMessageMode(T tunnel, BiConsumer<T, MessageMode[]> setModes,
                                   BiPredicate<T, MessageMode> testMode, MessageMode... modes) {
        setModes.accept(tunnel, modes);
        List<MessageMode> expected = Arrays.asList(modes);
        for (MessageMode mode : modes) {
            if (expected.contains(mode))
                assertTrue(testMode.test(tunnel, mode));
            else
                assertFalse(testMode.test(tunnel, mode));
        }
    }

}