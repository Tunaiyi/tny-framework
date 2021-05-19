package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.function.Supplier;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public abstract class GeneralTunnel<UID, E extends NetEndpoint<UID>> extends AbstractTunnel<UID, E> {

    protected volatile NetTransport<UID> transport;

    protected GeneralTunnel(NetTransport<UID> transport, TunnelMode mode, NetBootstrapContext<UID> bootstrapContext) {
        this(NetAide.newTunnelId(), transport, mode, bootstrapContext);
    }

    protected GeneralTunnel(long id, NetTransport<UID> transport, TunnelMode mode, NetBootstrapContext<UID> bootstrapContext) {
        super(id, mode, bootstrapContext);
        if (transport != null) {
            this.transport = transport;
            this.transport.bind(this);
        }
    }

    protected NetTransport<UID> getTransport() {
        return this.transport;
    }

    @Override
    public void reset() {
        if (this.status == TunnelStatus.INIT) {
            return;
        }
        synchronized (this) {
            if (this.status == TunnelStatus.INIT) {
                return;
            }
            if (!this.isAvailable()) {
                this.disconnect();
            }
            this.status = TunnelStatus.INIT;
        }
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.transport.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return this.transport.getLocalAddress();
    }

    @Override
    public boolean isAvailable() {
        return this.getStatus() == TunnelStatus.ACTIVATED && this.transport != null && this.transport.isActive();
    }

    NetTransport<UID> getNetTransport() {
        return this.transport;
    }

    protected void disconnectChannel() {
        NetTransport<UID> transport = this.transport;
        if (transport != null && transport.isActive()) {
            try {
                transport.close();
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    @Override
    protected void onClose() {
        this.disconnectChannel();
    }

    @Override
    public WriteMessageFuture write(MessageMaker<UID> maker, MessageContext<UID> context) throws NetException {
        WriteMessagePromise promise = as(context.getWriteMessageFuture());
        this.checkAvailable(promise);
        return this.transport.write(maker, context);
    }

    @Override
    public WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException {
        this.checkAvailable(promise);
        return this.transport.write(message, promise);
    }

    @Override
    public void write(Supplier<Collection<Message>> messageSupplier) {
        this.checkAvailable(null);
        this.transport.write(messageSupplier);
    }

    protected void onWriteUnavailable() {
    }

    private void checkAvailable(WriteMessagePromise promise) {
        if (!this.isAvailable()) {
            this.onWriteUnavailable();
            if (promise != null) {
                promise.failedAndThrow(new TunnelDisconnectException(format("{} is disconnect {}", this)));
            }
        }
    }

    @Override
    public WriteMessagePromise createWritePromise(long sendTimeout) {
        return this.transport.createWritePromise(sendTimeout);
    }

    protected GeneralTunnel<UID, E> setNetTransport(NetTransport<UID> transport) {
        this.transport = transport;
        return this;
    }

    protected AbstractTunnel<UID, E> setEndpoint(E endpoint) {
        this.endpoint = endpoint;
        return this;
    }

}
