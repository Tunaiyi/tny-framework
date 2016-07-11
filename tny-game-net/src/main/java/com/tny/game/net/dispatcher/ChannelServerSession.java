package com.tny.game.net.dispatcher;

import com.tny.game.LogUtils;
import com.tny.game.common.result.ResultCode;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.dispatcher.exception.SessionException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ChannelServerSession extends AbstractServerSession {

    protected static final Logger LOG = LoggerFactory.getLogger(CoreLogger.SESSION);

    /**
     * ip地址
     */
    private String hostName;

    /**
     * 通道
     */
    private transient Channel channel;

    private AtomicBoolean lock = new AtomicBoolean(false);

    public ChannelServerSession(Channel channel, LoginCertificate loginInfo) {
        super(loginInfo);
        if (channel != null) {
            this.setChannel(channel);
            SocketAddress address = channel.remoteAddress();
            this.hostName = address == null ? null : ((InetSocketAddress) address).getAddress().getHostAddress();
        }
    }

    public ChannelServerSession(Channel channel) {
        super(LoginCertificate.createUnLogin());
        if (channel != null) {
            this.setChannel(channel);
            SocketAddress address = channel.remoteAddress();
            this.hostName = address == null ? null : ((InetSocketAddress) address).getAddress().getHostAddress();
        }
    }

    protected void setChannel(Channel channel) {
        Channel old = this.channel;
        if (old == null && channel != null) {
            this.channel = channel;
            channel.attr(NetAttributeKey.SESSION).set(this);
            channel.attr(NetAttributeKey.SERVER_SESSION).set(this);
            this.encoder = channel.attr(NetAttributeKey.DATA_PACKET_ENCODER).get();
            this.checkers = channel.attr(NetAttributeKey.REQUEST_CHECKERS).get();
            this.messageBuilderFactory = channel.attr(NetAttributeKey.MSG_BUILDER_FACTOR).get();
        }
    }

    @Override
    public String getHostName() {
        return this.hostName;
    }

    @Override
    public void disconnect() {
        if (this.isConnect()) {
            Channel channel = this.channel;
            if (channel == null)
                return;
            if (ChannelServerSession.LOG.isInfoEnabled()) {
                LOG.info("Session主动断开##通道 {} ==> {}", channel.remoteAddress(), channel.localAddress(), new Date());
            }
            channel.flush();
            channel.disconnect();
            this.channel = null;
        }
    }

    @Override
    public boolean isConnect() {
        Channel channel = this.channel;
        return channel != null && channel.isActive();
    }

    protected Optional<ChannelFuture> write(Object data) {
        try {
            Channel channel = this.channel;
            if (channel != null && channel.isActive())
                return Optional.ofNullable(channel.writeAndFlush(data));
            return Optional.empty();
        } catch (Exception e) {
            ChannelServerSession.LOG.error("#Session#sendMessage 异常", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ChannelFuture> response(Protocol protocol, Object body) {
        return this.response(protocol, ResultCode.SUCCESS, body);
    }

    protected abstract int createResponseNumber();

    @Override
    public Optional<ChannelFuture> response(Protocol protocol, ResultCode code, Object body) {
        if (protocol.isPush()) {
            SessionPushOption option = this.attributes().getAttribute(SessionPushOption.SESSION_PUSH_OPTION, SessionPushOption.PUSH);
            if (!option.isPush()) {
                if (option.isThrowable())
                    throw new SessionException(LogUtils.format("Session {} [{}] 无法推送", this.getCertificate(), this.channel.remoteAddress()));
                return Optional.empty();
            }
        }
        Object data;
        if (body instanceof ByteBuf || body instanceof byte[] || body instanceof Response) {
            data = body;
            return this.write(data);
        } else {
            Optional<ChannelFuture> optional = Optional.empty();
            NetResponse response = (NetResponse) this.getMessageBuilderFactory()
                    .newResponseBuilder()
                    .setID(protocol.isPush() ? 0 : Session.DEFAULT_RESPONSE_ID)
                    .setProtocol(protocol)
                    .setResult(code)
                    .setBody(body)
                    .build();
            CoreLogger.log(this, response);
            while (lock.compareAndSet(false, true)) {
                try {
                    response.setNumber(createResponseNumber());
                    data = response;
                    this.prepareWriteResponse(response);
                    optional = this.write(data);
                    if (optional.isPresent())
                        this.postWriteResponse(response, optional.get());
                    break;
                } finally {
                    lock.set(false);
                }
            }
            return optional;
        }
    }

    protected void prepareWriteResponse(Response response) {

    }

    protected void postWriteResponse(Response response, ChannelFuture future) {

    }

    @Override
    public boolean isOnline() {
        return this.isConnect();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        // result = prime * result + ((this.channel == null) ? 0 : this.channel.hashCode());
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
        ChannelServerSession other = (ChannelServerSession) obj;
        Channel channel = this.channel;
        Channel otherChannel = other.channel;
        if (channel == null) {
            if (otherChannel != null)
                return false;
        } else if (!channel.equals(otherChannel))
            return false;
        if (this.getGroup() == null) {
            if (other.getGroup() != null)
                return false;
        } else if (!this.getGroup().equals(other.getGroup()))
            return false;
        return this.getUID() == other.getUID();
    }

}