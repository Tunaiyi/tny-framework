package com.tny.game.net.endpoint;

import com.tny.game.lock.HashLock;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static com.tny.game.common.utils.StringAide.*;

public class SingleTunnelSessionKeeper<UID> extends AbstractSessionKeeper<UID> implements SessionKeeper<UID> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    private static HashLock<Lock> hashLock = new HashLock<>(3000);

    public SingleTunnelSessionKeeper(String userType, SessionKeeperSetting setting) {
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
            if (certificate.getStatus() == CertificateStatus.AUTHERIZED) { // 登录创建 Session
                return Optional.ofNullable(doNewSession(tunnel));
            } else {  // 原有 Session 接受新 Tunnel
                return Optional.ofNullable(doAcceptTunnel(tunnel));
            }
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private NetSession<UID> doNewSession(NetTunnel<UID> newTunnel) throws ValidatorFailException {
        Certificate<UID> certificate = newTunnel.getCertificate();
        NetSession<UID> session = new SingleTunnelSession<>(certificate);

        Session<UID> oldSession = this.endpointMap.get(certificate.getUserId());
        if (oldSession != null) { // 如果旧 session 存在
            Certificate<UID> oldCert = oldSession.getCertificate();
            // 判断新授权是否比原有授权时间早, 如果是则无法登录
            if (certificate.getId() != oldCert.getId() && certificate.isOlderThan(oldCert)) {
                LOG.warn("认证已过 {}", certificate);
                throw new ValidatorFailException(NetResultCode.INVALID_CERTIFICATE);
            }
        }
        if (oldSession != null) {
            this.endpointMap.remove(session.getUserId(), oldSession);
            if (!oldSession.isClosed())
                oldSession.close();
            onRemoveSession.notify(this, oldSession);
        }
        this.endpointMap.put(session.getUserId(), session);
        session.acceptTunnel(newTunnel);
        onAddSession.notify(this, session);
        this.debugSessionSize();
        return session;
    }

    private NetSession<UID> doAcceptTunnel(NetTunnel<UID> newTunnel) throws ValidatorFailException {
        Certificate<UID> certificate = newTunnel.getCertificate();
        NetSession<UID> existSession = this.endpointMap.get(certificate.getUserId());
        if (existSession == null) { // 旧 session 失效
            LOG.warn("旧session {} 已经丢失", newTunnel.getUserId());
            throw new ValidatorFailException(NetResultCode.SESSION_LOSS);
        }
        if (existSession.isClosed()) { // 旧 session 已经关闭(失效)
            LOG.warn("旧session {} 已经关闭", existSession);
            throw new ValidatorFailException(NetResultCode.SESSION_LOSS);
        }
        // existSession.offline(); // 将旧 session 的 Tunnel T 下线
        existSession.acceptTunnel(newTunnel);
        return existSession;
    }

}
