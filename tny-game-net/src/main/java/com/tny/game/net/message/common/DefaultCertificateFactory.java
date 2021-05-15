package com.tny.game.net.message.common;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.transport.*;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/6 10:29 上午
 */
@Unit
@Component
public class DefaultCertificateFactory<UID> implements CertificateFactory<UID> {

    @Override
    public Certificate<UID> unauthenticate() {
        return Certificates.createUnauthenticated();
    }

    @Override
    public Certificate<UID> authenticate(long id, UID userID, String userType, Instant authenticateAt) {
        return Certificates.createAuthenticated(id, userID, userType, authenticateAt, false);
    }

    @Override
    public Certificate<UID> reauthenticate(long id, UID userID, String userType, Instant authenticateAt) {
        return Certificates.createAuthenticated(id, userID, userType, authenticateAt, true);
    }

}
