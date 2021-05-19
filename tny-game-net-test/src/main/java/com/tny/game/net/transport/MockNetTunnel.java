package com.tny.game.net.transport;

import com.tny.game.common.context.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * <p>
 */
public class MockNetTunnel extends AttributesHolder implements NetTunnel<Long> {

    private long accessId;
    private TunnelMode mode;
    private TunnelStatus state;
    private NetEndpoint<Long> endpoint;
    private InetSocketAddress address = new InetSocketAddress(7100);
    private int pingTimes = 0;
    private int pongTimes = 0;
    private int writeTimes = 0;
    private boolean bindSuccess = true;
    private boolean writeSuccess = true;
    private NetBootstrapContext<Long> context;

    public MockNetTunnel(NetEndpoint<Long> endpoint, TunnelMode mode) {
        this.endpoint = endpoint;
        this.state = TunnelStatus.ACTIVATED;
        this.mode = mode;
        this.context = new NetBootstrapContext<>();
    }

    @Override
    public long getId() {
        return 1;
    }

    @Override
    public long getAccessId() {
        return this.accessId;
    }

    @Override
    public TunnelMode getMode() {
        return this.mode;
    }

    @Override
    public boolean isAvailable() {
        return this.state == TunnelStatus.ACTIVATED;
    }

    @Override
    public boolean isActive() {
        return this.state == TunnelStatus.ACTIVATED;
    }

    @Override
    public TunnelStatus getStatus() {
        return this.state;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.address;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return this.address;
    }

    @Override
    public NetEndpoint<Long> getEndpoint() {
        return this.endpoint;
    }

    @Override
    public void setAccessId(long accessId) {
        this.accessId = accessId;
    }

    @Override
    public void ping() {
        this.pingTimes++;
    }

    @Override
    public void pong() {
        this.pongTimes++;
    }

    @Override
    public boolean open() {
        this.state = TunnelStatus.ACTIVATED;
        return true;
    }

    @Override
    public void disconnect() {
        this.state = TunnelStatus.UNACTIVATED;
        this.endpoint.onUnactivated(this);
    }

    @Override
    public void reset() {
        this.state = TunnelStatus.INIT;

    }

    @Override
    public boolean bind(NetEndpoint<Long> endpoint) {
        if (this.bindSuccess) {
            this.endpoint = endpoint;
            return true;
        }
        return false;
    }

    @Override
    public WriteMessagePromise createWritePromise(long timeout) {
        return new MockWriteMessagePromise(timeout);
    }

    @Override
    public NetBootstrapContext<Long> getNetBootstrapContext() {
        return this.context;
    }

    @Override
    public WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException {
        if (promise == null) {
            promise = createWritePromise(-1);
        }
        if (this.writeSuccess) {
            this.writeTimes++;
            promise.success();
        } else {
            promise.failed(new RuntimeException());
        }
        return promise;
    }

    @Override
    public WriteMessageFuture write(MessageMaker<Long> maker, MessageContext<Long> context) throws NetException {
        return null;
    }

    @Override
    public void write(Supplier<Collection<Message>> messageSupplier) {

    }

    @Override
    public Long getUserId() {
        return this.endpoint.getUserId();
    }

    @Override
    public String getUserType() {
        return this.endpoint.getUserType();
    }

    @Override
    public Certificate<Long> getCertificate() {
        return this.endpoint.getCertificate();
    }

    @Override
    public boolean isLogin() {
        return this.endpoint.isLogin();
    }

    @Override
    public boolean isClosed() {
        return this.state == TunnelStatus.CLOSE;
    }

    @Override
    public void close() {
        this.disconnect();
        this.state = TunnelStatus.CLOSE;
    }

    @Override
    public boolean receive(Message message) {
        return this.endpoint.receive(this, message);
    }

    @Override
    public SendContext<Long> send(MessageContext<Long> messageContext) {
        return this.endpoint.send(this, messageContext);
    }

    public MockNetTunnel setBindSuccess(boolean bindSuccess) {
        this.bindSuccess = bindSuccess;
        return this;
    }

    public MockNetTunnel setWriteSuccess(boolean writeSuccess) {
        this.writeSuccess = writeSuccess;
        return this;
    }

    public int getWriteTimes() {
        return this.writeTimes;
    }

    public int getPingTimes() {
        return this.pingTimes;
    }

    public int getPongTimes() {
        return this.pongTimes;
    }

}
