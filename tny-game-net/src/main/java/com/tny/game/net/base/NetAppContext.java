package com.tny.game.net.base;

import com.tny.game.net.coder.ChannelMaker;
import com.tny.game.net.netty.NettySessionFactory;
import io.netty.channel.Channel;

public interface NetAppContext extends AppContext {

    ChannelMaker<Channel> getChannelMaker();

    NettySessionFactory getSessionFactory();

}
