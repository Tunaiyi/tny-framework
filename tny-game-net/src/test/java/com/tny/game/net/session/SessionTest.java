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

package com.tny.game.net.session;

import com.tny.game.common.context.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static com.tny.game.test.TestAide.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class SessionTest<E extends NetSession> extends ConnectorTest<E> {

    protected abstract SessionTestInstance<E> create(Certificate certificate);

    protected SessionTestInstance<E> create() {
        return create(createLoginCert());
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

    protected abstract void doOffline(E session);

    @Test
    public void getId() {
        SessionTestInstance<E> object = create();
        E loginSession = object.getSesison();
        assertTrue(loginSession.getId() > 0);
        E session1 = create().getSesison();
        E session2 = create().getSesison();
        E session3 = create().getSesison();
        assertTrue(session2.getId() != session1.getId());
        assertTrue(session3.getId() != session2.getId());
        assertTrue(session3.getId() != session1.getId());
    }

    @Test
    public void isLogin() {
        E loginSession = create().getSesison();
        assertTrue(loginSession.isAuthenticated());
    }

    @Test
    public void attributes() {
        E loginSession = create().getSesison();
        List<Attributes> attributesList = callParallel("attributes", 20, () -> {
            Attributes attributes = loginSession.attributes();
            assertNotNull(attributes);
            return attributes;
        });
        Attributes expected = null;
        assertTrue(attributesList.size() > 2);
        for (Attributes checkOne : attributesList) {
            assertNotNull(checkOne);
            if (expected == null) {
                expected = checkOne;
            }
            assertSame(expected, checkOne);
        }
    }

    @Test
    public void isOnline() {
        E loginSession = create().getSesison();
        assertTrue(loginSession.isOnline());
    }

    @Test
    public void getCertificate() {
        E loginSession = create().getSesison();
        assertNotNull(loginSession.getCertificate());
        assertTrue(loginSession.getCertificate().isAuthenticated());
    }

    @Test
    public void isOffline() {
        E loginSession = create().getSesison();
        assertFalse(loginSession.isOffline());
    }

    @Test
    public void getOfflineTime() {
        E loginSession = create().getSesison();
        assertEquals(loginSession.getOfflineTime(), 0L);
        long now = System.currentTimeMillis();
        doOffline(loginSession);
        assertTrue(loginSession.getOfflineTime() >= now);
    }

    @Test
    public abstract void receive();

    @Test
    public abstract void send();

    @Test
    public abstract void resend();

}