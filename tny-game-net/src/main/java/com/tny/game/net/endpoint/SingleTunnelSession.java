package com.tny.game.net.endpoint;

import com.google.common.base.MoreObjects;
import com.tny.game.lock.HashLock;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.transport.*;
import org.slf4j.Logger;

import java.util.concurrent.locks.Lock;

import static com.tny.game.common.utils.StringAide.*;
import static org.slf4j.LoggerFactory.*;

/**
 * 单个 Tunnel 的 Session
 * <p>
 * Created by Kun Yang on 2017/2/17.
 */
public class SingleTunnelSession<UID> extends AbstractSession<UID> implements NetSession<UID> {

    public static final Logger LOGGER = getLogger(SingleTunnelSession.class);
    private volatile NetTunnel<UID> tunnel;

    @SuppressWarnings("unchecked")
    public SingleTunnelSession(Certificate<UID> certificate) {
        super(certificate);
    }

    @Override
    protected NetTunnel<UID> selectTunnel(MessageContext<UID> messageContext) {
        return this.tunnel;
    }

    @Override
    protected NetTunnel<UID> doAcceptTunnel(NetTunnel<UID> newTunnel) throws ValidatorFailException {
        if (this.tunnel == newTunnel)
            return null;
        if (newTunnel.bind(this)) {
            NetTunnel<UID> oldTunnel = this.tunnel;
            this.tunnel = newTunnel;
            this.offlineTime = 0;
            if (oldTunnel != null && newTunnel != oldTunnel)
                oldTunnel.close();  // 关闭旧 Tunnel
            return newTunnel;
        } else {
            throw new ValidatorFailException(format("{} tunnel is bound session", newTunnel));
        }
    }

    @Override
    public void onUnactivated(NetTunnel<UID> tunnel) {
        if (this.state == SessionState.OFFLINE)
            return;
        synchronized (this) {
            if (this.state == SessionState.OFFLINE)
                return;
            if (!this.tunnel.isClosed())
                return;
            setOffline();
        }
    }

    @Override
    public void offline() {
        synchronized (this) {
            if (!this.tunnel.isClosed())
                this.tunnel.close();
            setOffline();
        }
    }

    @Override
    public void heartbeat() {
        this.tunnel.ping();
    }

    @Override
    public void close() {
        if (state == SessionState.CLOSE)
            return;
        synchronized (this) {
            if (this.state == SessionState.CLOSE)
                return;
            this.offline();
            this.setClose();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userGroup", this.getUserType())
                .add("userId", this.getUserId())
                .add("tunnel", this.tunnel)
                .toString();
    }

}
