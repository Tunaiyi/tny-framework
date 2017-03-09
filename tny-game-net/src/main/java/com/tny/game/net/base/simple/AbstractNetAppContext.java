package com.tny.game.net.base.simple;

import com.tny.game.net.base.NetAppContext;
import com.tny.game.net.coder.ChannelMaker;
import com.tny.game.net.coder.SimpleChannelMaker;
import com.tny.game.net.dispatcher.ChannelServerSessionFactory;
import com.tny.game.net.dispatcher.message.simple.SimpleSessionFactory;
import io.netty.channel.Channel;

public abstract class AbstractNetAppContext extends AbstractAppContext implements NetAppContext {

    private ChannelMaker<Channel> channelMaker;

    private ChannelServerSessionFactory sessionFactory;

    @Override
    public ChannelMaker<Channel> getChannelMaker() {
        if (this.channelMaker == null)
            this.channelMaker = new SimpleChannelMaker<>();
        return this.channelMaker;
    }

    @Override
    public ChannelServerSessionFactory getSessionFactory() {
        if (this.sessionFactory == null)
            this.sessionFactory = this.getDefaultSessionFactory();
        return this.sessionFactory;
    }

    protected ChannelServerSessionFactory getDefaultSessionFactory() {
        return new SimpleSessionFactory();
    }

    public void setChannelMaker(ChannelMaker<Channel> channelMaker) {
        this.channelMaker = channelMaker;
    }

    public void setSessionFactory(ChannelServerSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
