package com.tny.game.net.netty4.telnet;

public class TelnetSession {//extends NetSession<Integer> {
    //
    // private static final String TELNET_USER_GROUP = "TELNET";
    //
    // /**
    //  * ip地址
    //  */
    // private String hostName;
    //
    // @Override
    // public String getHostName() {
    //     return this.hostName;
    // }
    //
    // @Override
    // protected int createResponseNumber() {
    //     return 0;
    // }
    //
    // public TelnetSession(int userId, Channel channel) {
    //     super(channel, LoginCertificate.createLogin(System.currentTimeMillis(), userId, TelnetSession.TELNET_USER_GROUP, false));
    //     this.hostName = channel == null || channel.remoteAddress() == null ? null : ((InetSocketAddress) channel
    //             .remoteAddress()).getAddress().getHostAddress();
    // }
    //
    // @Override
    // public Optional<MessageSendFuture> response(Protocol protocol, ResultCode code, Object body) {
    //     return this.write(body + "\r\n");
    // }

}
