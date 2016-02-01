package com.tny.game.net.dispatcher;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttributeUtils;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.client.nio.NetClient;
import com.tny.game.net.coder.DataPacketDecoder;
import com.tny.game.net.coder.DataPacketEncoder;
import com.tny.game.net.dispatcher.command.UserCommandBox;
import com.tny.game.telnet.TelnetSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.concurrent.atomic.AtomicBoolean;

public class NetAttributeKey {

    public static final AttributeKey<NetSession> SEESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".SEESSION");

    public static final AttributeKey<ServerSession> SERVER_SEESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".SERVER_SEESSION");
    public static final AttributeKey<ClientSession> CLIENT_SEESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".CLIENT_SEESSION");

    public static final AttributeKey<AppContext> CONTEXT = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".CONTEXT");

    public static final AttributeKey<TelnetSession> TELNET_SEESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".TELNET_SEESSION");

    public static final AttributeKey<NetClient> NIO_CLIENT = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".NIO_CLIENT");

    public static final AttrKey<AtomicBoolean> CLEAR_KEY = AttributeUtils.key(NetClient.class.getName() + "CLEARFUTURE");

    public static final AttrKey<UserCommandBox> USER_COMMAND_BOX = AttributeUtils.key(Session.class.getName() + "USER_COMMAND_BOX");

    public static final AttributeKey<MessageBuilderFactory> MSG_BUILDER_FACTORT = AttributeKey.valueOf(NetAttributeKey.class + "MSG_BUILDER_FACTORT");
    public static final AttributeKey<RequestChecker> REQUSET_CHECKER = AttributeKey.valueOf(NetAttributeKey.class + "RequsetChecker");
    public static final AttributeKey<DataPacketEncoder> DATA_PACKET_ENCODER = AttributeKey.valueOf(NetAttributeKey.class + "DATA_PACKET_ENCODER");
    public static final AttributeKey<DataPacketDecoder> DATA_PACKET_DECODER = AttributeKey.valueOf(NetAttributeKey.class + "DATA_PACKET_DECODER");
    public static final AttributeKey<ResponseMonitorHolder> RESPONSE_HANDLER = AttributeKey.valueOf(NetAttributeKey.class + "RESPONSE_HANDLER");

}
