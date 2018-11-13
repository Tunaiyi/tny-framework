package com.tny.game.net.endpoint;

import com.tny.game.lock.HashLock;
import com.tny.game.net.base.NetResultCode;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.transport.*;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static com.tny.game.common.utils.StringAide.*;

public class MultiTunnelSessionKeeper<UID> extends AbstractSessionKeeper<UID> implements SessionKeeper<UID> {

    private static HashLock<Lock> hashLock = new HashLock<>(3000);

    public MultiTunnelSessionKeeper(String userType, SessionKeeperSetting setting) {
        super(userType, setting);
    }

    @Override
    public Optional<Session<UID>> online(NetTunnel<UID> tunnel) throws ValidatorFailException {
        Certificate<UID> certificate = tunnel.getCertificate();
        if (!certificate.isAutherized())
            throw new ValidatorFailException(NetResultCode.VALIDATOR_FAIL, format("cert {} is unauthentic", certificate));
        if (!this.getUserType().equals(certificate.getUserType()))
            throw new ValidatorFailException(NetResultCode.VALIDATOR_FAIL, format("cert {} userType is {}, not {}", certificate, certificate.getUserType(), this.getUserType()));
        Lock lock = hashLock.getLock(certificate.getUserId());
        lock.lock();
        try {
            NetSession<UID> session;
            NetSession<UID> oldSession = this.endpointMap.get(certificate.getUserId());
            if (oldSession == null || oldSession.isClosed()) { // 如果旧 session 存在
                session = new MultiTunnelSession<>(certificate);
                this.endpointMap.put(session.getUserId(), session);
            } else {
                session = oldSession;
            }
            session.acceptTunnel(tunnel);
            return Optional.of(session);
        } finally {
            lock.unlock();
        }
    }

}
