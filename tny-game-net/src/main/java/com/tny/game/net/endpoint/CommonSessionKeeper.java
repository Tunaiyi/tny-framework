/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.endpoint;

import com.tny.game.common.concurrent.lock.locker.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

public class CommonSessionKeeper<UID> extends AbstractSessionKeeper<UID> implements SessionKeeper<UID> {

    private static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    private static final MapObjectLocker<Object> locker = new MapObjectLocker<>();

    public CommonSessionKeeper(ContactType contactType, SessionFactory<UID, ? extends NetSession<UID>, ? extends SessionSetting> factory,
            SessionKeeperSetting setting) {
        super(contactType, factory, setting);
    }

    @Override
    public Optional<Session<UID>> online(Certificate<UID> certificate, NetTunnel<UID> tunnel) throws AuthFailedException {
        if (!certificate.isAuthenticated()) {
            throw new AuthFailedException(NetResultCode.AUTH_FAIL_ERROR, "cert {} is unauthentic", certificate);
        }
        if (!this.getContactType().equals(certificate.contactType())) {
            throw new AuthFailedException(NetResultCode.AUTH_FAIL_ERROR,
                    "cert {} userType is {}, not {}", certificate, certificate.contactType(), this.getContactType());
        }
        UID uid = certificate.getIdentify();
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

    private Session<UID> doNewSession(Certificate<UID> certificate, NetTunnel<UID> newTunnel) throws AuthFailedException {
        NetSession<UID> oldSession = findEndpoint(certificate.getIdentify());
        if (oldSession != null) { // 如果旧 session 存在
            Certificate<UID> oldCert = oldSession.getCertificate();
            // 判断新授权是否比原有授权时间早, 如果是则无法登录
            if (certificate.getId() != oldCert.getId() && certificate.isOlderThan(oldCert)) {
                LOG.warn("认证已过 {}", certificate);
                throw new AuthFailedException(NetResultCode.INVALID_CERTIFICATE_ERROR);
            }
        }
        NetEndpoint<UID> endpoint = newTunnel.getEndpoint();
        var networkContext = newTunnel.getContext();
        NetSession<UID> session = this.factory.create(this.setting.getSession(), endpoint.getContext(), networkContext.getCertificateFactory());
        if (oldSession != null) {
            if (!oldSession.isClosed()) {
                oldSession.close();
            }
        }
        session.online(certificate, newTunnel);
        replaceEndpoint(session.getIdentify(), session);
        this.monitorEndpoint();
        return session;
    }

    private Session<UID> doAcceptTunnel(Certificate<UID> certificate, NetTunnel<UID> newTunnel) throws AuthFailedException {
        NetSession<UID> existSession = this.findEndpoint(certificate.getIdentify());
        if (existSession == null) { // 旧 session 失效
            LOG.warn("旧session {} 已经丢失", newTunnel.getIdentify());
            throw new AuthFailedException(NetResultCode.SESSION_LOSS_ERROR);
        }
        if (existSession.isClosed()) { // 旧 session 已经关闭(失效)
            LOG.warn("旧session {} 已经关闭", existSession);
            throw new AuthFailedException(NetResultCode.SESSION_LOSS_ERROR);
        }
        // existSession.offline(); // 将旧 session 的 Tunnel T 下线
        existSession.online(certificate, newTunnel);
        return existSession;
    }

}
