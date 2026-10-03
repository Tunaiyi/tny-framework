/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.session;

import com.tny.game.common.concurrent.lock.locker.*;
import com.tny.game.net.application.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

public class CommonSessionKeeper extends AutoCloseableSessionKeeper {

    private static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    private static final MapperLocker<Long> locker = MapperLocker.common();

    public CommonSessionKeeper(ContactType contactType, SessionKeeperSetting setting) {
        super(contactType, setting);
    }

    @Override
    public Optional<Session> online(Certificate certificate, NetTunnel tunnel) throws AuthFailedException {
        if (!certificate.isAuthenticated()) {
            throw new AuthFailedException(NetResultCode.AUTH_FAIL_ERROR, "cert {} is unauthentic", certificate);
        }
        if (!this.getContactType().equals(certificate.getContactType())) {
            throw new AuthFailedException(NetResultCode.AUTH_FAIL_ERROR,
                    "cert {} userType is {}, not {}", certificate, certificate.getContactType(), this.getContactType());
        }
        long identify = certificate.getIdentify();
        Lock identifyLock = locker.lock(identify);
        try {
            if (certificate.getStatus() == CertificateStatus.AUTHENTICATED) { // 登录创建 Session
                return Optional.of(doAuth(certificate, tunnel));
            } else {  // 原有 Session 接受新 Tunnel
                return Optional.of(doReAuth(certificate, tunnel));
            }
        } finally {
            locker.unlock(identify, identifyLock);
        }
    }

    private Session doAuth(Certificate certificate, NetTunnel newTunnel) throws AuthFailedException {
        NetSession oldSession = findSession(certificate.getIdentify());
        if (oldSession != null) { // 如果旧 session 存在
            Certificate oldCert = oldSession.getCertificate();
            // 判断新授权是否比原有授权时间早, 如果是则无法登录
            if (certificate.getId() != oldCert.getId() && certificate.isOlderThan(oldCert)) {
                LOG.warn("认证已过 {}", certificate);
                throw new AuthFailedException(NetResultCode.INVALID_CERTIFICATE_ERROR);
            }
        }
        NetSession session = newTunnel.getSession();
        // 先接管后终结：新会话 online 失败（已被销毁/凭证被拒）时旧会话原样存活，
        // 调用方收到明确失败；接管成功才关闭旧会话（net-session"顶号先接管后终结"契约）
        session.online(certificate);
        session.setSendMessageCachedSize(setting.getSession().getSendMessageCachedSize());
        if (oldSession != null) {
            if (!oldSession.isClosed()) {
                oldSession.close();
            }
        }
        resetSession(session.getIdentify(), session);
        this.monitorSession();
        return session;
    }

    private Session doReAuth(Certificate certificate, NetTunnel newTunnel) throws AuthFailedException {
        NetSession existSession = this.findSession(certificate.getIdentify());
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
