/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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