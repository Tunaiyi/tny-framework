package com.tny.game.net.dispatcher;

import io.netty.channel.*;

import java.net.SocketAddress;

public class TestChannel extends AbstractChannel {

    public TestChannel() {
        super(null);
    }

    @Override
    public ChannelConfig config() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isOpen() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isActive() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public ChannelMetadata metadata() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AbstractUnsafe newUnsafe() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected SocketAddress localAddress0() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected void doDisconnect() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected void doClose() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected void doBeginRead() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public ChannelFuture write(Object msg) {
        return new DefaultChannelPromise(this);
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        return new DefaultChannelPromise(this);
    }

}
