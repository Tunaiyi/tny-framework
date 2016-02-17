package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

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

    /**
     * 推送ID创建器
     */
    private AtomicInteger pushIDCreator = new AtomicInteger(0);

    public ChannelServerSession(Channel channel, LoginCertificate loginInfo) {
        super(loginInfo);
        if (channel != null) {
            this.setChannel(channel);
            SocketAddress address = channel.remoteAddress();
            this.hostName = channel == null || address == null ? null : ((InetSocketAddress) address).getAddress().getHostAddress();
        }
    }

    public ChannelServerSession(Channel channel) {
        super(LoginCertificate.createUnLogin());
        if (channel != null) {
            this.setChannel(channel);
            SocketAddress address = channel.remoteAddress();
            this.hostName = channel == null || address == null ? null : ((InetSocketAddress) address).getAddress().getHostAddress();
        }
    }

    protected void setChannel(Channel channel) {
        if (this.channel == null && channel != null) {
            this.channel = channel;
            channel.attr(NetAttributeKey.SEESSION).set(this);
            channel.attr(NetAttributeKey.SERVER_SEESSION).set(this);
            this.encoder = channel.attr(NetAttributeKey.DATA_PACKET_ENCODER).get();
            this.checker = channel.attr(NetAttributeKey.REQUSET_CHECKER).get();
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
            if (ChannelServerSession.LOG.isInfoEnabled()) {
                LOG.info("Session主动断开##通道 {} ==> {}", this.channel.remoteAddress(), this.channel.localAddress(), new Date());
            }
            this.channel.disconnect();
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
            ChannelServerSession.LOG.error("#Session#sendMessage 异常", e);
        }
        return null;
    }

    @Override
    public ChannelFuture response(Protocol protocol, Object body) {
        return this.response(protocol, ResultCode.SUCCESS, body);
    }

    @Override
    public ChannelFuture response(Protocol protocol, ResultCode code, Object body) {
        Object data;
        if (body instanceof ByteBuf || body instanceof byte[]) {
            data = body;
        } else {
            Response response = this.getMessageBuilderFactory()
                    .newResponseBuilder()
                    .setID(protocol.isPush() ? pushIDCreator.incrementAndGet() : Session.DEFAULT_RESPONSE_ID)
                    .setProtocol(protocol)
                    .setResult(code)
                    .setBody(body)
                    .build();
            CoreLogger.log(this, response);
            data = response;
        }
        this.prepareWriteResponse(protocol, code, body);
        ChannelFuture future = this.write(data);
        if (future != null)
            this.postWriteResponse(protocol, code, body, future);
        return future;
    }

    protected void prepareWriteResponse(Protocol protocol, ResultCode code, Object body) {

    }

    protected void postWriteResponse(Protocol protocol, ResultCode code, Object body, ChannelFuture future) {

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
        ChannelServerSession other = (ChannelServerSession) obj;
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