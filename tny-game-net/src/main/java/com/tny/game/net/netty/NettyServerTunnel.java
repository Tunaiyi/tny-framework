package com.tny.game.net.netty;

import com.tny.game.net.transport.*;
import io.netty.channel.Channel;
import org.slf4j.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class NettyServerTunnel<UID> extends NettyTunnel<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyServerTunnel.class);

    public NettyServerTunnel(Channel channel, Certificate<UID> certificate) {
        super(channel, certificate, TunnelMode.SERVER);
    }

}
