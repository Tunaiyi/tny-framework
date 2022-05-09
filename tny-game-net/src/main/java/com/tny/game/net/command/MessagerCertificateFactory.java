package com.tny.game.net.command;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;

import java.time.Instant;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/4/30 5:57 下午
 */
@UnitInterface
public interface MessagerCertificateFactory<UID extends MessagerIdentify> extends CertificateFactory<UID> {

    default Certificate<UID> certificate(long id, UID userId, MessagerType messagerType, Instant authenticateAt) {
        return certificate(id, userId, userId.getMessagerId(), messagerType, authenticateAt);
    }

    default Certificate<UID> renewCertificate(long id, UID userId, MessagerType messagerType, Instant authenticateAt) {
        return renewCertificate(id, userId, userId.getMessagerId(), messagerType, authenticateAt);
    }

}
