package com.tny.game.net.transport;

import com.tny.game.common.unit.annotation.*;

import java.time.Instant;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/4/30 5:57 下午
 */
@UnitInterface
public interface CertificateFactory<UID> {

    Certificate<UID> anonymous();

    Certificate<UID> certificate(long id, UID userID, String userType, Instant authenticateAt);

    Certificate<UID> renewCertificate(long id, UID userID, String userType, Instant authenticateAt);

}
