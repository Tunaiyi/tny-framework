package com.tny.game.net.netty;

import com.tny.game.common.ExceptionUtils;
import com.tny.game.net.common.session.CommonNetSession;
import com.tny.game.net.session.SessionInputEventHandler;
import com.tny.game.net.dispatcher.MessageSentHandler;
import com.tny.game.net.session.SessionOutputEventHandler;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.session.event.SessionSendEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.SocketAddress;
import java.util.Date;

/**
 * Created by Kun Yang on 2017/3/17.
 */
public class NettySession<UID> extends CommonNetSession<UID, NettySession<UID>> {

    private Channel channel;

    private MessageBuilderFactory messageBuilderFactory;

    public NettySession(Channel channel, MessageBuilderFactory messageBuilderFactory,
                        int sendNumPerTime, SessionOutputEventHandler writer,
                        int receiveNamPerTime, SessionInputEventHandler reader,
                        int cacheMessageSize) {
        super(sendNumPerTime, writer, receiveNamPerTime, reader, cacheMessageSize);
        ExceptionUtils.checkNotNull(channel, "channel is null");
        this.setChannel(channel);
        this.messageBuilderFactory = messageBuilderFactory;
    }

    private void setChannel(Channel channel) {
        Channel old = this.channel;
        if (old == null && channel != null) {
            this.channel = channel;
            channel.attr(NettyAttrKeys.SESSION).set(this);
            this.messageBuilderFactory = channel.attr(NettyAttrKeys.MSG_BUILDER_FACTOR).get();
        }
    }

    @Override
    public MessageBuilderFactory getMessageBuilderFactory() {
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

    public SocketAddress getSocketAddress() {
        return channel.remoteAddress();
    }

    @Override
    protected void postOffline() {
        this.disconnect("Offline");
    }

    @Override
    protected void postInvalid() {
        this.disconnect("invalid");
    }

    @Override
    protected void write(SessionSendEvent capsule) {
        try {
            Channel channel = this.channel;
            if (channel != null) {
                MessageSentHandler sentHandler = capsule.getSentHandler();
                ChannelFuture channelFuture = channel.writeAndFlush(capsule.getMessage());
                if (sentHandler != null) {
                    if (channelFuture != null)
                        channelFuture.addListener(future -> {
                            if (channelFuture.isSuccess())
                                sentHandler.onSent(capsule.getMessage());
                        });
                }
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

}
