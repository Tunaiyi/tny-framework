package com.tny.game.net.passthrough;

import com.tny.game.net.base.*;
import com.tny.game.net.passthrough.exception.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.passthrough.PipeStatus.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/4 8:17 下午
 */
public abstract class BaseNetPipe<UID> implements NetPipe<UID> {

    private volatile PipeStatus status = INIT;

    private final Map<Long, NetTubule<UID>> tubuleMap = new ConcurrentHashMap<>();

    private NetBootstrapContext<UID> bootstrapContext;

    @Override
    public PipeStatus getStatus() {
        return this.status;
    }

    @Override
    public Optional<Tubule<UID>> getTubule(long tunnelId) {
        return Optional.ofNullable(this.tubuleMap.get(tunnelId));
    }

    @Override
    public Optional<Tubule<UID>> closeTubule(long tunnelId) {
        Tubule<UID> tubule = this.tubuleMap.get(tunnelId);
        if (tubule != null) {
            tubule.close();
        }
        return Optional.ofNullable(tubule);
    }

    @Override
    public Tubule<UID> connectTubule(long tunnelId, InetSocketAddress remoteAddress) throws PipeClosedException {
        if (!this.status.isCloseStatus()) {
            throw new PipeClosedException(format("pipe {} closed, connectTubule failed", this.getRemoteAddress()));
        }
        NetTubule<UID> tubule = createTubule(tunnelId, remoteAddress);
        synchronized (this) {
            if (this.status == CLOSED) {
                throw new PipeClosedException(format("pipe {} closed, connectTubule failed", this.getRemoteAddress()));
            }
            if (this.tubuleMap.putIfAbsent(tunnelId, tubule) == null) {
                tubule.open();
            }
        }
        return tubule;
    }

    protected NetTubule<UID> createTubule(long tunnelId, InetSocketAddress remoteAddress) {
        return new DefaultTubule<>(tunnelId, TunnelMode.SERVER, this, remoteAddress, this.bootstrapContext);
    }

    protected abstract void onClose();

    @Override
    public void close() {
        if (this.status.isCloseStatus()) {
            return;
        }
        synchronized (this) {
            if (this.status.isCloseStatus()) {
                return;
            }
            this.status = CLOSING;
            this.tubuleMap.forEach((id, tubule) -> tubule.close());
            this.status = CLOSED;
            this.onClose();
        }
    }

    @Override
    public void destroyTubule(NetTubule<UID> tubule) {
        if (this.tubuleMap.remove(tubule.getId(), tubule)) {
            if (!tubule.isClosed()) {
                tubule.close();
            }
        }
    }

    @Override
    public boolean isActive() {
        return this.status == ACTIVATED && this.isConnected();
    }

    protected abstract boolean isConnected();

}
