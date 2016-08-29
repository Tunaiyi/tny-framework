package com.tny.game.net.dispatcher;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttributeUtils;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.coder.DataPacketDecoder;
import com.tny.game.net.coder.DataPacketEncoder;
import com.tny.game.net.dispatcher.command.DispatcherCommandBox;
import com.tny.game.net.client.nio.NetClient;
import com.tny.game.telnet.TelnetSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetAttributeKey {

    public static final AttributeKey<NetSession> SESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".SESSION");

    public static final AttributeKey<ServerSession> SERVER_SESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".SERVER_SESSION");
    public static final AttributeKey<ClientSession> CLIENT_SESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".CLIENT_SESSION");

    public static final AttributeKey<AppContext> CONTEXT = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".CONTEXT");

    public static final AttributeKey<TelnetSession> TELNET_SESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".TELNET_SESSION");

    public static final AttributeKey<NetClient> NIO_CLIENT = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".NIO_CLIENT");

    public static final AttrKey<AtomicBoolean> CLEAR_KEY = AttributeUtils.key(NetClient.class.getName() + "CLEAR_KEY");

    public static final AttrKey<DispatcherCommandBox> USER_COMMAND_BOX = AttributeUtils.key(Session.class.getName() + "USER_COMMAND_BOX");

    public static final AttributeKey<MessageBuilderFactory> MSG_BUILDER_FACTOR = AttributeKey.valueOf(NetAttributeKey.class + "MSG_BUILDER_FACTOR");
    public static final AttributeKey<List<RequestChecker>> REQUEST_CHECKERS = AttributeKey.valueOf(NetAttributeKey.class + "REQUEST_CHECKERS");
    public static final AttributeKey<DataPacketEncoder> DATA_PACKET_ENCODER = AttributeKey.valueOf(NetAttributeKey.class + "DATA_PACKET_ENCODER");
    public static final AttributeKey<DataPacketDecoder> DATA_PACKET_DECODER = AttributeKey.valueOf(NetAttributeKey.class + "DATA_PACKET_DECODER");
    public static final AttributeKey<ResponseHandlerHolder> RESPONSE_HANDLER = AttributeKey.valueOf(NetAttributeKey.class + "RESPONSE_HANDLER");

}
