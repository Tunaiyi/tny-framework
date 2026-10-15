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

import com.tny.game.net.application.*;
import org.junit.jupiter.api.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2018/8/12.
 */
public abstract class ConnectorTest<C extends Communicator> {

    protected static Long uid = 100L;

    private static final Long UNAUTHENTICATED_UID = null;

    private static final ContactType contactType = DefaultContactType.DEFAULT_USER;

    protected static Long certificateId = System.currentTimeMillis();

    protected Certificate createUnLoginCert() {
        return Certificates.anonymous();
    }

    protected Certificate createLoginCert() {
        return Certificates.createAuthenticated(certificateId, uid, uid, contactType, Instant.now());
    }

    protected Certificate createLoginCert(long certificateId, Long uid) {
        return Certificates.createAuthenticated(certificateId, uid, uid, contactType, Instant.now());
    }

    protected ConnectorTest() {
    }

    public abstract C createNetter(Certificate certificate);

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