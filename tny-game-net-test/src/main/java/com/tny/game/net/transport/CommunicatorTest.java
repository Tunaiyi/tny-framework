package com.tny.game.net.transport;

import com.tny.game.net.command.*;
import org.junit.jupiter.api.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2018/8/12.
 */
public abstract class CommunicatorTest<C extends Communicator<Long>> {

	protected static Long uid = 100L;

	private static final Long UNAUTHENTICATED_UID = null;

	protected static String userGroup = Certificates.DEFAULT_USER_TYPE;

	protected static Long certificateId = System.currentTimeMillis();

	protected Certificate<Long> createUnLoginCert() {
		return Certificates.createUnauthenticated(UNAUTHENTICATED_UID);
	}

	protected Certificate<Long> createLoginCert() {
		return Certificates.createAuthenticated(certificateId, uid, Instant.now());
	}

	protected Certificate<Long> createLoginCert(long certificateId, Long uid) {
		return Certificates.createAuthenticated(certificateId, uid, Instant.now());
	}

	protected CommunicatorTest() {
	}

	public abstract C createNetter(Certificate<Long> certificate);

	@Test
	public void getUserId() {
		C loginCommunicator = createNetter(createLoginCert());
		assertEquals(uid, loginCommunicator.getUserId());
	}

	@Test
	public void getUserType() {
		C loginCommunicator = createNetter(createLoginCert());
		assertEquals(userGroup, loginCommunicator.getUserType());
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