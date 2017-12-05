package com.tny.game.net.netty;

import com.tny.game.suite.app.AppConfiguration;
import com.tny.game.net.netty.coder.ChannelMaker;
import io.netty.channel.Channel;

public interface NettyAppConfiguration extends AppConfiguration {

    ChannelMaker<Channel> getChannelMaker();

}
