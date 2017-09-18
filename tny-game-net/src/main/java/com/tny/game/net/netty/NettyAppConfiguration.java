package com.tny.game.net.netty;

import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.coder.ChannelMaker;
import io.netty.channel.Channel;

public interface NettyAppConfiguration extends AppConfiguration {

    ChannelMaker<Channel> getChannelMaker();

}
