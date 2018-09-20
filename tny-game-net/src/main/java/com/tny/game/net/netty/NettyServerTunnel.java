package com.tny.game.net.netty;

import com.tny.game.net.base.NetLogger;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.*;
import io.netty.channel.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class NettyServerTunnel<UID> extends NettyTunnel<UID> {

    public NettyServerTunnel(Channel channel, Certificate<UID> certificate) {
        super(channel, certificate);
    }

    @Override
    protected void doWrite(Message<UID> message, WriteCallback<UID> callback) {
        NetLogger.logSend(this, message);
        try {
            synchronized (this) {
                if (message.getId() <= 0) {
                    if (message instanceof NetMessage)
                        as(message, NetMessage.class).setId(createMessageID());
                    this.getSession().ifPresent(session -> session.addSentMessage(message));
                }
                ChannelFuture future = channel.writeAndFlush(message);
                if (callback != null) {
                    future.addListener(f -> {
                        if (f.isSuccess())
                            callback.onWrite(message, true, null);
                        else
                            callback.onWrite(message, false, f.cause());
                    });
                }
            }
        } catch (Exception e) {
            if (callback != null)
                callback.onWrite(message, false, e);
        }
    }

}
