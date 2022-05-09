package com.tny.game.net.command;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;

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
    public Certificate<UID> certificate(long id, UID userId, long messagerId, MessagerType messagerType, Instant authenticateAt) {
        return Certificates.createAuthenticated(id, userId, messagerId, messagerType, authenticateAt, false);
    }

    @Override
    public Certificate<UID> renewCertificate(long id, UID userId, long messagerId, MessagerType messagerType, Instant authenticateAt) {
        return Certificates.createAuthenticated(id, userId, messagerId, messagerType, authenticateAt, true);
    }

}
