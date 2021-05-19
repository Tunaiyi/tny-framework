package com.tny.game.net.passthrough;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.task.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.function.Supplier;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/3 12:02 下午
 */
public class BaseTubule<UID> extends GeneralTunnel<UID, NetEndpoint<UID>> implements NetTubule<UID> {

    private final NetPipe<UID> pipe;

    private final InetSocketAddress remoteAddress;

    protected BaseTubule(long id, TunnelMode mode, NetPipe<UID> pipe, InetSocketAddress remoteAddress, NetBootstrapContext<UID> bootstrapContext) {
        super(id, null, mode, bootstrapContext);
        this.remoteAddress = remoteAddress;
        this.pipe = pipe;
    }

    @Override
    protected boolean onOpen() {
        return true;
    }

    @Override
    protected void onClose() {
        this.pipe.destroyTubule(this);
    }

    @Override
    protected void onDisconnect() {
        this.pipe.destroyTubule(this);
    }

    @Override
    public boolean bind(NetEndpoint<UID> endpoint) {
        if (endpoint == null) {
            return false;
        }
        if (this.endpoint == endpoint) {
            return true;
        }
        synchronized (this) {
            if (this.endpoint == endpoint) {
                return true;
            }
            if (this.endpoint == null) {
                this.endpoint = endpoint;
            } else {
                Certificate<UID> certificate = this.getCertificate();
                if (!certificate.isAuthenticated()) {
                    CommandTaskBox oldEventBox = this.endpoint.getCommandTaskBox();
                    this.endpoint = endpoint;
                    this.endpoint.takeOver(oldEventBox);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean replayEndpoint(NetEndpoint<UID> endpoint) {
        return false;
    }

    @Override
    public WriteMessagePromise createWritePromise(long timeout) {
        return this.pipe.createWritePromise(timeout);
    }

    private void failAndThrow(WriteMessagePromise promise, NetException exception) throws NetException {
        promise.failed(exception);
        throw exception;
    }

    @Override
    public WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException {
        if (!this.isAvailable()) {
            //            this.onWriteUnavailable(message, promise);
            if (promise != null) {
                failAndThrow(promise, new TunnelDisconnectException(format("{} is disconnect {}", this)));
            }
        }
        this.pipe.write(this, message, promise);
        return promise;
    }

    @Override
    public WriteMessageFuture write(MessageMaker<UID> maker, MessageContext<UID> context) throws NetException {
        return null;
    }

    @Override
    public void write(Supplier<Collection<Message>> messageSupplier) {

    }

    @Override
    public SendContext<UID> send(MessageContext<UID> messageContext) {
        return this.endpoint.send(this, messageContext);
    }

    @Override
    public boolean isAvailable() {
        return this.getStatus() == TunnelStatus.ACTIVATED && this.pipe != null && this.pipe.isActive();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return this.pipe.getLocalAddress();
    }

}
