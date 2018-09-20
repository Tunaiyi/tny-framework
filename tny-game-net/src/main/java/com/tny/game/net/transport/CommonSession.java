package com.tny.game.net.transport;

import com.google.common.base.MoreObjects;
import com.tny.game.common.concurrent.StageableFuture;
import com.tny.game.common.utils.Throws;
import com.tny.game.net.base.NetResultCode;
import com.tny.game.net.exception.*;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.transport.SessionEvents.*;
import static org.slf4j.LoggerFactory.*;

/**
 * 单个 Tunnel 的 Session
 * <p>
 * Created by Kun Yang on 2017/2/17.
 */
public class CommonSession<UID> extends AbstractSession<UID> implements NetSession<UID> {

    public static final Logger LOGGER = getLogger(CommonSession.class);

    private volatile long offlineTime;

    private volatile NetTunnel<UID> tunnel;

    private MessageIdCreator idCreator = new MessageIdCreator(MessageIdCreator.SESSION_MESSAGE_ID_MARK);

    private AtomicInteger sessionState = new AtomicInteger(SessionState.ONLINE.getId());

    @SuppressWarnings("unchecked")
    public CommonSession(Certificate<UID> certificate, int cacheMessageSize) {
        super(certificate, cacheMessageSize);
    }

    // @Override
    // public void authenticate(Certificate<UID> certificate) throws ValidatorFailException {
    //     int stateID = this.sessionState.get();
    //     if (stateID == SessionState.CLOSE.getId())
    //         throw new ValidatorFailException(NetResultCode.SESSION_LOSS);
    //     if (stateID == SessionState.OFFLINE.getId())
    //         throw new ValidatorFailException("session is offline");
    //     if (this.certificate.isAutherized())
    //         throw new ValidatorFailException("session is login");
    //     if (!certificate.isAutherized())
    //         throw new ValidatorFailException("Certificate unlogin");
    //     this.certificate = certificate;
    //     this.offlineTime = 0;
    //     ON_ONLINE.notify(this, this.tunnel);
    // }

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
        while (true) {
            int stateID = this.sessionState.get();
            if (stateID == SessionState.CLOSE.getId()) { // 判断 session 状态是否可以重登
                throw new ValidatorFailException(NetResultCode.SESSION_LOSS);
            }
            if (this.tunnel == tunnel)
                return;
            if (this.sessionState.compareAndSet(stateID, SessionState.ONLINE.getId())) { // 尝试执行重登切换
                NetTunnel<UID> current = doAcceptTunnel(newCertificate, tunnel);
                ON_ONLINE.notify(this, current);
                return;
            }
        }
    }


    NetTunnel<UID> doAcceptTunnel(Certificate<UID> newCertificate, NetTunnel<UID> newTunnel) {
        synchronized (this) {
            this.offlineTime = 0;
            this.certificate = newCertificate;
            NetTunnel<UID> oldTunnel = this.tunnel;
            this.tunnel = newTunnel;
            this.tunnel.bind(this); //新Tunnel绑定当前 session
            if (oldTunnel != null && newTunnel != oldTunnel)
                oldTunnel.close();  // 关闭旧 Tunnel
            return this.tunnel;
        }
    }

    @Override
    public boolean offlineIf(Tunnel<UID> tunnel) {
        if (this.tunnel != tunnel)
            return false;
        synchronized (this) {
            if (this.tunnel != tunnel)
                return false;
            if (this.sessionState.compareAndSet(SessionState.ONLINE.getId(), SessionState.OFFLINE.getId())) {
                doOffline(this.tunnel);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public long createMessageId() {
        return this.idCreator.createId();
    }

    void doOffline(Tunnel<UID> tunnel) {
        this.offlineTime = System.currentTimeMillis();
        tunnel.close();
        ON_OFFLINE.notify(this, tunnel);
    }

    @Override
    public void offline() {
        // this.sessionState
        if (this.sessionState.compareAndSet(SessionState.ONLINE.getId(), SessionState.OFFLINE.getId())) {
            doOffline(this.tunnel);
        }
    }

    @Override
    public StageableFuture<Void> close() {
        int state = this.sessionState.get();
        while (state != SessionState.CLOSE.getId()) {
            onTryClose();
            if (this.sessionState.compareAndSet(state, SessionState.CLOSE.getId())) {
                return doClose(state);
            } else {
                state = this.sessionState.get();
            }
        }
        return CommonStageableFuture.completedFuture(null);
    }

    void onTryClose() {
    }


    StageableFuture<Void> doClose(int state) {
        StageableFuture<Void> future = null;
        Tunnel<UID> tunnel = this.tunnel;
        if (tunnel != null)
            future = tunnel.close();
        if (state == SessionState.ONLINE.getId())
            ON_OFFLINE.notify(this, tunnel);
        this.destroyFutureHolder();
        ON_CLOSE.notify(this, tunnel);
        return future == null ? CommonStageableFuture.completedFuture(null) : future;
    }

    @Override
    public void send(MessageContext<UID> context) {
        try {
            int state = this.sessionState.get();
            if (state == SessionState.CLOSE.getId())
                throw new SessionException("Session [{}] 已经关闭, 无法发送", this);
            tunnel.send(context);
        } catch (NetException e) {
            LOGGER.warn("send exception: {} | message: {}", e.getClass(), e.getMessage());
            throw e;
        } catch (Throwable e) {
            LOGGER.warn("send exception: {} | message: {}", e.getClass(), e.getMessage());
            throw new NetException(format("{} send {} failed", this, context), e);
        }
    }

    @Override
    public void resend(long tunnelId, ResendMessage<UID> message) {
        if (tunnelId > 0 && this.tunnel.getId() != tunnelId)
            return;
        try {
            int state = this.sessionState.get();
            if (state == SessionState.CLOSE.getId())
                throw new SessionException("Session [{}] 已经关闭, 无法发送", this);
            tunnel.resend(message);
        } catch (NetException e) {
            LOGGER.warn("resend exception: {} | message: {}", e.getClass(), e.getMessage());
            throw e;
        } catch (Throwable e) {
            LOGGER.warn("resend exception: {} | message: {}", e.getClass(), e.getMessage());
            throw new NetException(format("{} send {} failed", this, message), e);
        }
    }

    @Override
    public boolean isOnline() {
        return this.sessionState.get() == SessionState.ONLINE.getId();
    }

    @Override
    public boolean isOffline() {
        return this.sessionState.get() == SessionState.OFFLINE.getId();
    }

    @Override
    public boolean isClosed() {
        return this.sessionState.get() == SessionState.CLOSE.getId();
    }

    @Override
    public long getOfflineTime() {
        return offlineTime;
    }

    NetTunnel<UID> getTunnel() {
        return tunnel;
    }
// @Override
    // public NetTunnel<UID> getTunnel() {
    //     return tunnel;
    // }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userGroup", this.getUserType())
                .add("userId", this.getUserId())
                .add("tunnel", this.tunnel)
                .toString();
    }

}
