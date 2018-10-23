package com.tny.game.net.session;

import com.tny.game.common.utils.Throws;
import com.tny.game.net.base.NetResultCode;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.transport.*;
import org.slf4j.Logger;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.session.SessionEvents.*;
import static org.slf4j.LoggerFactory.*;

/**
 * 抽象Session
 * <p>
 * Created by Kun Yang on 2017/2/17.
 */
public abstract class AbstractSession<UID> extends AbstractEndpoint<UID> implements NetSession<UID> {

    public static final Logger LOGGER = getLogger(AbstractSession.class);

    /* 身份凭证 */
    protected Certificate<UID> certificate;

    /* 状态 */
    protected volatile SessionState state = SessionState.OFFLINE;

    /* 离线时间 */
    protected volatile long offlineTime;

    public AbstractSession(Certificate<UID> certificate) {
        super(certificate, MessageIdCreator.SESSION_MESSAGE_ID_MARK);
    }

    @Override
    public SessionState getState() {
        return state;
    }

    @Override
    public boolean isOnline() {
        return this.state == SessionState.ONLINE;
    }

    @Override
    public boolean isOffline() {
        return this.state == SessionState.OFFLINE;
    }

    @Override
    public boolean isClosed() {
        return this.state == SessionState.CLOSE;
    }

    @Override
    public void close() {
        if (state == SessionState.CLOSE)
            return;
        synchronized (this) {
            if (state == SessionState.CLOSE)
                return;
            this.offline();
            this.setClose();
        }
    }

    @Override
    public void acceptTunnel(NetTunnel<UID> tunnel) throws ValidatorFailException {
        Throws.checkNotNull(tunnel, "newSession is null");
        Certificate<UID> newCertificate = tunnel.getCertificate();
        if (!newCertificate.isAutherized()) {
            throw new ValidatorFailException(NetResultCode.UNLOGIN);
        }
        if (!certificate.isSameCertificate(newCertificate)) { // 是否是同一个授权
            throw new ValidatorFailException(format("Certificate new [{}] 与 old [{}] 不同", newCertificate, this.certificate));
        }
        if (this.state == SessionState.CLOSE) // 判断 session 状态是否可以重登
            throw new ValidatorFailException(NetResultCode.SESSION_LOSS);
        synchronized (this) {
            if (this.state == SessionState.CLOSE) // 判断 session 状态是否可以重登
                throw new ValidatorFailException(NetResultCode.SESSION_LOSS);
            NetTunnel<UID> current = doAcceptTunnel(tunnel);
            if (current != null) {
                this.offlineTime = 0;
                this.state = SessionState.ONLINE;
                ON_ACCEPT.notify(this, current);
            }
        }
    }

    @Override
    public long getOfflineTime() {
        return offlineTime;
    }

    protected abstract NetTunnel<UID> doAcceptTunnel(NetTunnel<UID> newTunnel) throws ValidatorFailException;

    protected void setOffline() {
        this.offlineTime = System.currentTimeMillis();
        this.state = SessionState.OFFLINE;
        ON_OFFLINE.notify(this);
    }

    protected void setClose() {
        this.state = SessionState.CLOSE;
        this.destroyFutureHolder();
        ON_CLOSE.notify(this);
    }

}
