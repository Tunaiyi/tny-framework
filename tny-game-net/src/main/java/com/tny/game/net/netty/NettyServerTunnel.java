package com.tny.game.net.netty;

import com.tny.game.net.base.NetLogger;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
import com.tny.game.net.tunnel.WriteCallback;
import io.netty.channel.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class NettyServerTunnel<UID> extends NettyTunnel<UID> {

    public NettyServerTunnel(Channel channel, SessionFactory<UID> sessionFactory, MessageBuilderFactory<UID> messageBuilderFactory) {
        super(channel, sessionFactory, messageBuilderFactory);
    }

    @Override
    public void doWrite(Message<UID> message, WriteCallback<UID> callback) {
        NetLogger.logSend(this.session, message);
        try {
            ChannelFuture future = channel.writeAndFlush(message);
            if (callback != null) {
                future.addListener(f -> {
                    if (f.isSuccess())
                        callback.onWrite(message, true, null);
                    else
                        callback.onWrite(message, false, f.cause());
                });
            }
        } catch (Exception e) {
            if (callback != null)
                callback.onWrite(message, false, e);
        }
    }

}
