package com.tny.game.net.transport;

import com.tny.game.net.message.*;
import org.junit.*;

import java.util.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class TunnelTest<T extends Tunnel<Long>> extends CommunicatorTest<T> {

    protected T createBindTunnel() {
        return this.createTunnel(createLoginCert());
    }

    protected T createUnbindTunnel() {
        return this.createTunnel(createUnLoginCert());
    }

    protected abstract T createTunnel(Certificate<Long> certificate);

    @Override
    public T createNetter(Certificate<Long> certificate) {
        return createTunnel(certificate);
    }

    @Test
    public void getId() {
        T tunnel1 = createBindTunnel();
        T tunnel2 = createBindTunnel();
        T tunnel3 = createBindTunnel();
        assertTrue(tunnel1.getId() != tunnel2.getId());
        assertTrue(tunnel2.getId() != tunnel3.getId());
        assertTrue(tunnel3.getId() != tunnel1.getId());
    }

    @Test
    public void attributes() {
        T tunnel = createBindTunnel();
        assertNotNull(tunnel.attributes());
        assertNotNull(tunnel.attributes());
    }

    @Test
    public void isLogin() {
        T loginTunnel = createBindTunnel();
        assertTrue(loginTunnel.isLogin());
        T unloginTunnel = createUnbindTunnel();
        assertFalse(unloginTunnel.isLogin());
    }

    private void assertMessageMode(T tunnel, BiConsumer<T, MessageMode[]> setModes,
            BiPredicate<T, MessageMode> testMode, MessageMode... modes) {
        setModes.accept(tunnel, modes);
        List<MessageMode> expected = Arrays.asList(modes);
        for (MessageMode mode : modes) {
            if (expected.contains(mode)) {
                assertTrue(testMode.test(tunnel, mode));
            } else {
                assertFalse(testMode.test(tunnel, mode));
            }
        }
    }

}