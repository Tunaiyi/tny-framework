package com.tny.game.net.base;

import com.tny.game.net.coder.ChannelMaker;
import com.tny.game.net.coder.SimpleChannelMaker;
import com.tny.game.net.netty.NettySessionFactory;
import com.tny.game.net.message.defalut.SimpleSessionFactory;
import io.netty.channel.Channel;

public abstract class AbstractNetAppContext extends AbstractAppContext implements NetAppContext {

    private ChannelMaker<Channel> channelMaker;

    private NettySessionFactory sessionFactory;

    @Override
    public ChannelMaker<Channel> getChannelMaker() {
        if (this.channelMaker == null)
            this.channelMaker = new SimpleChannelMaker<>();
        return this.channelMaker;
    }

    @Override
    public NettySessionFactory getSessionFactory() {
        if (this.sessionFactory == null)
            this.sessionFactory = this.getDefaultSessionFactory();
        return this.sessionFactory;
    }

    protected NettySessionFactory getDefaultSessionFactory() {
        return new SimpleSessionFactory();
    }

    public void setChannelMaker(ChannelMaker<Channel> channelMaker) {
        this.channelMaker = channelMaker;
    }

    public void setSessionFactory(NettySessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
