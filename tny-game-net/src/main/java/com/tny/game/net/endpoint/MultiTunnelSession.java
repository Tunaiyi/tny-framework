package com.tny.game.net.endpoint;

import com.google.common.base.MoreObjects;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

import static com.tny.game.common.utils.StringAide.*;
import static org.slf4j.LoggerFactory.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-08 16:02
 */
public class MultiTunnelSession<UID> extends AbstractSession<UID> implements NetSession<UID> {

    public static final Logger LOGGER = getLogger(SingleTunnelSession.class);

    private List<NetTunnel<UID>> tunnels = new CopyOnWriteArrayList<>();

    private AtomicInteger indexCounter = new AtomicInteger(0);

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock writeLock = lock.writeLock();
    private Lock readLock = lock.readLock();

    @SuppressWarnings("unchecked")
    public MultiTunnelSession(Certificate<UID> certificate) {
        super(certificate);
        // Throws.checkArgument(maxTunnelSize > 0, "maxTunnelSize {} <= 0", maxTunnelSize);
    }

    @Override
    protected NetTunnel<UID> selectTunnel(MessageContext<UID> messageContext) {
        int startIndex = indexCounter.incrementAndGet();
        readLock.lock();
        try {
            int size = tunnels.size();
            if (size == 0)
                throw new SessionException(format("{} no tunnels exist", this));
            for (int index = startIndex; index < startIndex + size; index++) {
                NetTunnel<UID> tunnel = tunnels.get(index % size);
                if (tunnel.isAvailable())
                    return tunnel;
            }
            throw new SessionException(format("select tunnel {} is close", this));
        } finally {
            readLock.unlock();
        }
    }

    @Override
    protected NetTunnel<UID> doAcceptTunnel(NetTunnel<UID> newTunnel) throws ValidatorFailException {
        writeLock.lock();
        try {
            // if (tunnels.size() >= maxTunnelSize)
            //     throw new ValidatorFailException(format("session {} is max tunnel", this));
            if (this.tunnels.contains(newTunnel))
                return null;
            if (newTunnel.bind(this)) {
                this.tunnels.add(newTunnel);
                return newTunnel;
            } else {
                throw new ValidatorFailException(format("{} tunnel is bound session", newTunnel));
            }
        } finally {
            writeLock.unlock();
        }
    }


    @Override
    public void onUnactivated(NetTunnel<UID> tunnel) {
        if (this.state == SessionState.OFFLINE)
            return;
        synchronized (this) {
            if (this.state == SessionState.OFFLINE)
                return;
            writeLock.lock();
            try {
                if (!this.tunnels.remove(tunnel))
                    return;
                if (!tunnel.isClosed())
                    tunnel.close();
                for (NetTunnel<UID> netTunnel : this.tunnels) {
                    if (!netTunnel.isClosed())
                        return;
                }
                this.tunnels.clear();
                setOffline();
            } finally {
                writeLock.unlock();
            }
        }
    }

    @Override
    public void offline() {
        synchronized (this) {
            writeLock.lock();
            try {
                for (NetTunnel<UID> tunnel : this.tunnels) {
                    if (!tunnel.isClosed())
                        tunnel.close();
                }
                this.tunnels.clear();
                setOffline();
            } finally {
                writeLock.unlock();
            }
        }
    }

    @Override
    public void heartbeat() {
        readLock.lock();
        try {
            for (NetTunnel<UID> tunnel : tunnels)
                tunnel.ping();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userGroup", this.getUserType())
                .add("userId", this.getUserId())
                .toString();
    }

}