package com.tny.game.net.netty;

import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageWriteFuture;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class NettyServerTunnel<UID> extends NettyTunnel<UID> {

    public NettyServerTunnel(Channel channel, AppConfiguration configuration) {
        super(channel, configuration);
    }

    @Override
    public void write(Message<UID> message, MessageWriteFuture<UID> writeFuture) {
        NetLogger.logSend(this.session, message);
        ChannelFuture future = channel.writeAndFlush(message);
        if (writeFuture != null && writeFuture.isHasFuture()) {
            future.addListener(f -> {
                if (f.isSuccess())
                    writeFuture.success(message);
                else
                    writeFuture.fail(f.cause());
            });
        }
    }

}
