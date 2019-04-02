package com.tny.game.net.netty4;

import com.tny.game.net.endpoint.*;
import io.netty.channel.Channel;

/**
 * Created by Kun Yang on 2018/8/28.
 */
public interface NettyTerminal<UID> extends NetEndpoint<UID>, Terminal<UID> {

    long getConnectTimeout();

    boolean getInitConnectAsyn();

    Channel connect();

    void connectSuccess(NettyTerminalTunnel<UID> tunnel);

}
