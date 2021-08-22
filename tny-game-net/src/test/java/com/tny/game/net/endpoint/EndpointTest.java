package com.tny.game.net.endpoint;

import com.tny.game.common.context.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static com.tny.game.test.TestAide.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public abstract class EndpointTest<E extends NetEndpoint<Long>> extends CommunicatorTest<E> {

	protected abstract EndpointTestInstance<E> create(Certificate<Long> certificate);

	protected EndpointTestInstance<E> create() {
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
		EndpointTestInstance<E> object = create();
		E loginSession = object.getEndpoint();
		assertTrue(loginSession.getId() > 0);
		E session1 = create().getEndpoint();
		E session2 = create().getEndpoint();
		E session3 = create().getEndpoint();
		assertTrue(session2.getId() != session1.getId());
		assertTrue(session3.getId() != session2.getId());
		assertTrue(session3.getId() != session1.getId());
	}

	@Test
	public void isLogin() {
		E loginSession = create().getEndpoint();
		assertTrue(loginSession.isLogin());
	}

	@Test
	public void attributes() {
		E loginSession = create().getEndpoint();
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
		E loginSession = create().getEndpoint();
		assertTrue(loginSession.isOnline());
	}

	@Test
	public void getCertificate() {
		E loginSession = create().getEndpoint();
		assertNotNull(loginSession.getCertificate());
		assertTrue(loginSession.getCertificate().isAuthenticated());
	}

	@Test
	public void isOffline() {
		E loginSession = create().getEndpoint();
		assertFalse(loginSession.isOffline());
	}

	@Test
	public void getOfflineTime() {
		E loginSession = create().getEndpoint();
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