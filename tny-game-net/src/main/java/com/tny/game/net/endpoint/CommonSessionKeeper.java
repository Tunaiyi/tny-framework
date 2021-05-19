package com.tny.game.net.endpoint;

import com.tny.game.common.concurrent.lock.locker.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static com.tny.game.common.utils.StringAide.*;

public class CommonSessionKeeper<UID> extends AbstractSessionKeeper<UID> implements SessionKeeper<UID> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    private static final ObjectLocker<Object> locker = new ObjectLocker<>();

    public CommonSessionKeeper(String userType, SessionFactory<UID, ? extends NetSession<UID>, ? extends SessionSetting> factory,
            SessionKeeperSetting setting) {
        super(userType, factory, setting);
    }

    @Override
    public Optional<Session<UID>> online(Certificate<UID> certificate, NetTunnel<UID> tunnel) throws ValidatorFailException {
        if (!certificate.isAuthenticated()) {
            throw new ValidatorFailException(NetResultCode.VALIDATOR_FAIL, format("cert {} is unauthentic", certificate));
        }
        if (!this.getUserType().equals(certificate.getUserType())) {
            throw new ValidatorFailException(NetResultCode.VALIDATOR_FAIL,
                    format("cert {} userType is {}, not {}", certificate, certificate.getUserType(), this.getUserType()));
        }
        UID uid = certificate.getUserId();
        Lock lock = locker.lock(uid);
        try {
            if (certificate.getStatus() == CertificateStatus.AUTHENTICATED) { // 登录创建 Session
                return Optional.of(doNewSession(certificate, tunnel));
            } else {  // 原有 Session 接受新 Tunnel
                return Optional.of(doAcceptTunnel(certificate, tunnel));
            }
        } finally {
            locker.unlock(uid, lock);
        }
    }

    private Session<UID> doNewSession(Certificate<UID> certificate, NetTunnel<UID> newTunnel) throws ValidatorFailException {
        Session<UID> oldSession = this.endpointMap.get(certificate.getUserId());
        if (oldSession != null) { // 如果旧 session 存在
            Certificate<UID> oldCert = oldSession.getCertificate();
            // 判断新授权是否比原有授权时间早, 如果是则无法登录
            if (certificate.getId() != oldCert.getId() && certificate.isOlderThan(oldCert)) {
                LOG.warn("认证已过 {}", certificate);
                throw new ValidatorFailException(NetResultCode.INVALID_CERTIFICATE);
            }
        }
        NetEndpoint<UID> endpoint = newTunnel.getEndpoint();
        NetSession<UID> session = this.factory.create(this.setting.getSession(), endpoint.getContext());
        if (oldSession != null) {
            this.endpointMap.remove(oldSession.getUserId(), oldSession);
            if (!oldSession.isClosed()) {
                oldSession.close();
            }
            onRemoveSession.notify(this, oldSession);
        }
        session.online(certificate, newTunnel);
        this.endpointMap.put(session.getUserId(), session);
        onAddSession.notify(this, session);
        this.debugSessionSize();
        return session;
    }

    private Session<UID> doAcceptTunnel(Certificate<UID> certificate, NetTunnel<UID> newTunnel) throws ValidatorFailException {
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
        existSession.online(certificate, newTunnel);
        return existSession;
    }

}
