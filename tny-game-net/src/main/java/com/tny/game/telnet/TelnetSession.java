package com.tny.game.telnet;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.dispatcher.ChannelServerSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.Optional;

public class TelnetSession extends ChannelServerSession {

    private static final String TELNET_USER_GROUP = "TELNET";

    /**
     * ip地址
     */
    private String hostName;

    @Override
    public String getHostName() {
        return this.hostName;
    }

    @Override
    protected int createResponseNumber() {
        return 0;
    }

    public TelnetSession(int userId, Channel channel) {
        super(channel, LoginCertificate.createLogin(userId, TelnetSession.TELNET_USER_GROUP, false));
        this.hostName = channel == null || channel.remoteAddress() == null ? null : ((InetSocketAddress) channel
                .remoteAddress()).getAddress().getHostAddress();
    }

    @Override
    public Optional<ChannelFuture> response(Protocol protocol, ResultCode code, Object body) {
        return this.write(body + "\r\n");
    }

}
