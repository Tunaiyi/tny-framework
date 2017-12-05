package com.tny.game.suite.net.spring;

import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.netty.coder.ChannelMaker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.TimeUnit;

import static com.tny.game.suite.SuiteProfiles.*;

@Unit("ReadTimeoutChannelMaker")
@Profile({SERVER, CLIENT, GAME})
public class ReadTimeoutChannelMaker<C extends Channel> extends ChannelMaker<C> {

    @Override
    protected void prepareAddCoder(ChannelPipeline channelPipeline) {
        channelPipeline.addLast(new ReadTimeoutHandler(3, TimeUnit.MINUTES));
    }

}
