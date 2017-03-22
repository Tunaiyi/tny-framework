package com.tny.game.net.netty;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttrUtils;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.client.nio.NetClient;
import com.tny.game.net.coder.DataPacketDecoder;
import com.tny.game.net.coder.DataPacketEncoder;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.common.dispatcher.MessageCommandBox;
import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.Session;
import com.tny.game.telnet.TelnetSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface NettyAttrKeys {

    AttributeKey<NetSession<?>> SESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".SESSION");

    AttributeKey<AppContext> CONTEXT = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".CONTEXT");

    // public static final AttributeKey<NetSession<?>> SERVER_SESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".SERVER_SESSION");
    // public static final AttributeKey<NetSession<?>> CLIENT_SESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".CLIENT_SESSION");


    AttributeKey<TelnetSession> TELNET_SESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".TELNET_SESSION");

    AttributeKey<NetClient> NIO_CLIENT = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".NIO_CLIENT");

    AttrKey<AtomicBoolean> CLEAR_KEY = AttrUtils.key(NetClient.class.getName() + "CLEAR_KEY");

    AttrKey<MessageCommandBox> USER_COMMAND_BOX = AttrUtils.key(Session.class.getName() + "USER_COMMAND_BOX");

    AttributeKey<MessageBuilderFactory> MSG_BUILDER_FACTOR = AttributeKey.valueOf(NettyAttrKeys.class + "MSG_BUILDER_FACTOR");
    AttributeKey<List<ControllerChecker>> REQUEST_CHECKERS = AttributeKey.valueOf(NettyAttrKeys.class + "REQUEST_CHECKERS");
    AttributeKey<DataPacketEncoder> DATA_PACKET_ENCODER = AttributeKey.valueOf(NettyAttrKeys.class + "DATA_PACKET_ENCODER");
    AttributeKey<DataPacketDecoder> DATA_PACKET_DECODER = AttributeKey.valueOf(NettyAttrKeys.class + "DATA_PACKET_DECODER");
    // public static final AttributeKey<ResponseHandlerHolder> RESPONSE_HANDLER = AttributeKey.valueOf(NettyAttrKeys.class + "RESPONSE_HANDLER");

}
