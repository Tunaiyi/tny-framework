package com.tny.game.net.transport;

import com.tny.game.common.context.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;

import java.net.InetSocketAddress;

/**
 * <p>
 */
public class MockNetTunnel extends AttributesHolder implements NetTunnel<Long> {

    private long accessId;
    private TunnelMode mode;
    private TunnelState state;
    private NetEndpoint<Long> endpoint;
    private InetSocketAddress address = new InetSocketAddress(7100);
    private int pingTimes = 0;
    private int pongTimes = 0;
    private int writeTimes = 0;
    private boolean bindSuccess = true;
    private boolean writeSuccess = true;
    private MessageFactory<Long> factory = new CommonMessageFactory<>();

    public MockNetTunnel(NetEndpoint<Long> endpoint, TunnelMode mode) {
        this.endpoint = endpoint;
        this.state = TunnelState.ACTIVATE;
        this.mode = mode;
    }

    @Override
    public long getId() {
        return 1;
    }

    @Override
    public long getAccessId() {
        return accessId;
    }

    @Override
    public TunnelMode getMode() {
        return mode;
    }

    @Override
    public boolean isAvailable() {
        return this.state == TunnelState.ACTIVATE;
    }

    @Override
    public boolean isAlive() {
        return this.state == TunnelState.ACTIVATE;
    }

    @Override
    public TunnelState getState() {
        return state;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return address;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return address;
    }

    @Override
    public NetEndpoint<Long> getEndpoint() {
        return endpoint;
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
        this.state = TunnelState.ACTIVATE;
        return true;
    }

    @Override
    public void disconnect() {
        this.state = TunnelState.UNACTIVATED;
        this.endpoint.onUnactivated(this);
    }

    @Override
    public boolean bind(NetEndpoint<Long> endpoint) {
        if (bindSuccess) {
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
    public MessageFactory<Long> getMessageFactory() {
        return factory;
    }

    @Override
    public WriteMessageFuture write(Message<Long> message, WriteMessagePromise promise) throws NetException {
        if (promise == null)
            promise = createWritePromise(-1);
        if (writeSuccess) {
            this.writeTimes++;
            promise.success();
        } else {
            promise.failed(new RuntimeException());
        }
        return promise;
    }

    @Override
    public Long getUserId() {
        return endpoint.getUserId();
    }

    @Override
    public String getUserType() {
        return endpoint.getUserType();
    }

    @Override
    public Certificate<Long> getCertificate() {
        return endpoint.getCertificate();
    }

    @Override
    public boolean isLogin() {
        return endpoint.isLogin();
    }

    @Override
    public boolean isClosed() {
        return state == TunnelState.CLOSE;
    }

    @Override
    public void close() {
        this.disconnect();
        this.state = TunnelState.CLOSE;
    }

    @Override
    public boolean receive(Message<Long> message) {
        return endpoint.receive(this, message);
    }

    @Override
    public SendContext<Long> send(MessageContext<Long> messageContext) {
        return endpoint.send(this, messageContext);
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
        return writeTimes;
    }

    public int getPingTimes() {
        return pingTimes;
    }

    public int getPongTimes() {
        return pongTimes;
    }
}
