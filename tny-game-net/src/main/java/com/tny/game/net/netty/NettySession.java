package com.tny.game.net.netty;

import com.tny.game.common.ExceptionUtils;
import com.tny.game.net.common.session.CommonSession;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.message.MessageSentHandler;
import com.tny.game.net.session.SessionInputEventHandler;
import com.tny.game.net.session.SessionOutputEventHandler;
import com.tny.game.net.session.event.SessionSendEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.Date;

/**
 * Netty Session 实现
 * Created by Kun Yang on 2017/3/17.
 */
public class NettySession<UID> extends CommonSession<UID, NettySession<UID>> {

    private Channel channel;

    private MessageBuilderFactory<UID> messageBuilderFactory;

    public NettySession(Channel channel, MessageBuilderFactory<UID> messageBuilderFactory,
                        SessionOutputEventHandler<UID, NettySession<UID>> outputHandler,
                        SessionInputEventHandler<UID, NettySession<UID>> inputHandler,
                        int cacheMessageSize) {
        super(outputHandler, inputHandler, cacheMessageSize);
        ExceptionUtils.checkNotNull(channel, "channel is null");
        this.setChannel(channel);
        this.messageBuilderFactory = messageBuilderFactory;
    }

    @SuppressWarnings("unchecked")
    private void setChannel(Channel channel) {
        Channel old = this.channel;
        if (old == null && channel != null) {
            this.channel = channel;
        }
    }

    @Override
    public MessageBuilderFactory<UID> getMessageBuilderFactory() {
        return this.messageBuilderFactory;
    }

    @Override
    protected void postReline(NettySession<UID> newSession) {
        synchronized (this) {
            this.certificate = newSession.getCertificate();
            this.messageCheckGenerator = newSession.getMessageCheckGenerator();
            this.channel = newSession.getChannel();
            this.channel.attr(NettyAttrKeys.SESSION).set(this);
        }
    }

    @Override
    protected void postOffline() {
        this.disconnect("Offline");
    }

    @Override
    protected void postInvalid() {
        this.disconnect("invalid");
    }

    @SuppressWarnings("unchecked")
    public void write(SessionSendEvent event) {
        try {
            Channel channel = this.channel;
            if (channel != null) {
                Message<?> message = event.getMessage();
                MessageSentHandler handler = event.getSentHandler();
                ChannelFuture future = channel.writeAndFlush(message);
                if (handler != null)
                    future.addListener(f -> {
                        if (f.isSuccess())
                            handler.onSent(this, message);
                    });
            }
        } catch (Throwable e) {
            LOGGER.error("#Session#sendMessage 异常", e);
        }
    }

    private void disconnect(String reason) {
        Channel channel = this.channel;
        if (channel == null)
            return;
        if (channel.isActive()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Session主动断开## {} ##通道 {} ==> {}", reason, channel.remoteAddress(), channel.localAddress(), new Date());
            channel.flush();
            channel.disconnect();
        }
        this.channel = null;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return "Session [" + this.getGroup() + "." + this.getUID() + " | " + this.channel + "]";
    }
}
