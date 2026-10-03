/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.net.transport;

import com.tny.game.net.message.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class TunnelTest<T extends Tunnel> extends ConnectorTest<T> {

    protected T createBindTunnel() {
        return this.createTunnel(createLoginCert());
    }

    protected T createUnbindTunnel() {
        return this.createTunnel(createUnLoginCert());
    }

    protected abstract T createTunnel(Certificate certificate);

    @Override
    public T createNetter(Certificate certificate) {
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
        assertTrue(loginTunnel.isAuthenticated());
        T unloginTunnel = createUnbindTunnel();
        assertFalse(unloginTunnel.isAuthenticated());
    }

    private void assertMessageMode(T tunnel, BiConsumer<T, MessageMode[]> setModes, BiPredicate<T, MessageMode> testMode, MessageMode... modes) {
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