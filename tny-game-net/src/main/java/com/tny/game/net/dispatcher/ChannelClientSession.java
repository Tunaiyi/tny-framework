package com.tny.game.net.dispatcher;

import com.tny.game.log.CoreLogger;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Protocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ChannelClientSession extends AbstractClientSession {

    protected static final Logger LOG = LoggerFactory.getLogger(CoreLogger.SESSION);

    /**
     * ip地址
     */
    private String hostName;

    /**
     * 通道
     */
    private transient Channel channel;

    private AtomicInteger requestIDCreator = new AtomicInteger(1);

    public ChannelClientSession(Channel channel, LoginCertificate loginInfo) {
        super(loginInfo);
        if (channel != null)
            this.setChannel(channel);
    }

    public ChannelClientSession(Channel channel) {
        this(null, LoginCertificate.createUnLogin());
    }

    protected void setChannel(Channel channel) {
        if (this.channel == null && channel != null) {
            this.channel = channel;
            SocketAddress address = channel.remoteAddress();
            this.hostName = channel == null || address == null ? null : ((InetSocketAddress) address).getAddress().getHostAddress();
            channel.attr(NetAttributeKey.SEESSION).set(this);
            channel.attr(NetAttributeKey.CLIENT_SEESSION).set(this);
            this.messageBuilderFactory = channel.attr(NetAttributeKey.MSG_BUILDER_FACTORT).get();
        }
    }

    @Override
    public String getHostName() {
        return this.hostName;
    }

    @Override
    public void disconnect() {
        if (this.isConnect()) {
            if (ChannelClientSession.LOG.isInfoEnabled()) {
                LOG.info("Session主动断开##通道 {} ==> {}", new Object[]{this.channel.remoteAddress(), this.channel.localAddress(), new Date()});
            }
            this.channel.disconnect();
            this.clearFuture();
        }
    }

    @Override
    public boolean isConnect() {
        if (this.channel == null)
            return false;
        return this.channel.isActive();
    }

    protected ChannelFuture write(Object data) {
        try {
            if (this.channel.isActive()) {
                return this.channel.writeAndFlush(data);
            }
        } catch (Exception e) {
            ChannelClientSession.LOG.error("#Session#sendMessage 异常", e);
        }
        return null;
    }

    protected ChannelFuture writeRequset(Request request, final MessageFuture<?> future) {
        try {
            if (this.channel.isActive()) {
                if (future == null) {
                    return this.channel.writeAndFlush(request);
                } else {
                    return this.channel.writeAndFlush(request, new NetChannelPromise(this, future, channel));
                }
            }
        } catch (Exception e) {
            ChannelClientSession.LOG.error("#Session#sendMessage 异常", e);
        }
        return null;
    }

    @Override
    public ChannelFuture requset(Protocol protocol, Object... params) {
        MessageFuture<?> future = null;
        return this.requset(protocol, future, params);
    }

    @Override
    public <B> ChannelFuture requset(Protocol protocol, MessageFuture<B> future, Object... params) {
        Request request = this.getMessageBuilderFactory()
                .newRequestBuilder()
                .setID(this.requestIDCreator.getAndIncrement())
                .setProtocol(protocol)
                .addParameter(params)
                .build();
        CoreLogger.log(this, request);
        if (future != null) {
            future.setRequestID(request)
                    .setSession(this);
        }
        return this.writeRequset(request, future);
    }

    @Override
    public <B> ChannelFuture requset(Protocol protocol, MessageAction<B> action, Object... params) {
        MessageFuture<B> future = new MessageFuture<B>();
        future.setResponseAction(action);
        return this.requset(protocol, future, params);
    }

    @Override
    public boolean isOnline() {
        return this.isConnect();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.channel == null) ? 0 : this.channel.hashCode());
        result = prime * result + ((this.getGroup() == null) ? 0 : this.getGroup().hashCode());
        result = prime * result + (int) (this.getUID() ^ (this.getUID() >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        ChannelClientSession other = (ChannelClientSession) obj;
        if (this.channel == null) {
            if (other.channel != null)
                return false;
        } else if (!this.channel.equals(other.channel))
            return false;
        if (this.getGroup() == null) {
            if (other.getGroup() != null)
                return false;
        } else if (!this.getGroup().equals(other.getGroup()))
            return false;
        if (this.getUID() != other.getUID())
            return false;
        return true;
    }

}