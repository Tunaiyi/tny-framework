package com.tny.game.starter.net.netty4.configuration.executor;

import com.tny.game.net.command.executor.*;
import io.netty.channel.DefaultEventLoopGroup;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/18 3:37 上午
 */
public class EventLoopGroupCommandTaskProcessor extends EndpointCommandTaskProcessor {

    @Override
    public void prepareStart() {
        super.prepareStart();
        this.executorService = new DefaultEventLoopGroup(8);
    }

}
