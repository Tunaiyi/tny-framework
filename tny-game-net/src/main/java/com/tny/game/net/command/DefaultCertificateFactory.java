package com.tny.game.net.command;

import com.tny.game.common.lifecycle.unit.annotation.*;

import java.time.Instant;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/6 10:29 上午
 */
@Unit
public class DefaultCertificateFactory<UID> implements CertificateFactory<UID> {

	@Override
	public Certificate<UID> anonymous() {
		return Certificates.createUnauthenticated();
	}

	@Override
	public Certificate<UID> certificate(long id, UID userID, String userType, Instant authenticateAt) {
		return Certificates.createAuthenticated(id, userID, userType, authenticateAt, false);
	}

	@Override
	public Certificate<UID> renewCertificate(long id, UID userID, String userType, Instant authenticateAt) {
		return Certificates.createAuthenticated(id, userID, userType, authenticateAt, true);
	}

}
