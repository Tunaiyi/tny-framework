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
package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import org.junit.jupiter.api.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2018/8/12.
 */
public abstract class ConnectorTest<C extends Connector<Long>> {

    protected static Long uid = 100L;

    private static final Long UNAUTHENTICATED_UID = null;

    private static ContactType contactType = NetContactType.DEFAULT_USER;

    protected static Long certificateId = System.currentTimeMillis();

    protected Certificate<Long> createUnLoginCert() {
        return Certificates.createUnauthenticated(UNAUTHENTICATED_UID);
    }

    protected Certificate<Long> createLoginCert() {
        return Certificates.createAuthenticated(certificateId, uid, uid, contactType, Instant.now());
    }

    protected Certificate<Long> createLoginCert(long certificateId, Long uid) {
        return Certificates.createAuthenticated(certificateId, uid, uid, contactType, Instant.now());
    }

    protected ConnectorTest() {
    }

    public abstract C createNetter(Certificate<Long> certificate);

    @Test
    public void getUserId() {
        C loginCommunicator = createNetter(createLoginCert());
        assertEquals(uid, loginCommunicator.getIdentify());
    }

    @Test
    public void getUserType() {
        C loginCommunicator = createNetter(createLoginCert());
        assertEquals(contactType.getGroup(), loginCommunicator.getGroup());
    }

    //	@Test
    //	public void isClosed() {
    //		C loginCommunicator = createNetter(createLoginCert());
    //		assertFalse(loginCommunicator.isClosed());
    //		loginCommunicator.close();
    //		assertTrue(loginCommunicator.isClosed());
    //		loginCommunicator.close();
    //		assertTrue(loginCommunicator.isClosed());
    //	}

}