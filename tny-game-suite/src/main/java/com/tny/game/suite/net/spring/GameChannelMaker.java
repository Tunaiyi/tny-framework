package com.tny.game.suite.net.spring;

import com.tny.game.net.coder.ChannelMaker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SERVER, GAME})
public class GameChannelMaker<C extends Channel> extends ChannelMaker<C> {

    @Override
    protected void prepareAddCoder(ChannelPipeline channelPipeline) {
        channelPipeline.addLast(
                new WriteTimeoutHandler(5, TimeUnit.SECONDS),
                new ReadTimeoutHandler(600, TimeUnit.MINUTES)
        );
    }

}
