package com.tny.game.suite.net.spring;

import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.coder.ChannelMaker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Profile({"suite.server", "suite.all"})
public class GameChannelMaker<C extends Channel> extends ChannelMaker<C> {

    @Autowired
    public GameChannelMaker(RequestChecker checker) {
        super(checker);
    }

    @Override
    protected void prepareAddCoder(ChannelPipeline channelPipeline) {
        channelPipeline.addLast(
                new WriteTimeoutHandler(5, TimeUnit.SECONDS),
                new ReadTimeoutHandler(600, TimeUnit.MINUTES)
        );
    }

}
