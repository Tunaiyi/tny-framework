package com.tny.game.net.netty;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttrKeys;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.coder.DataPacketDecoder;
import com.tny.game.net.coder.DataPacketEncoder;
import com.tny.game.net.common.dispatcher.MessageCommandBox;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.session.Session;
import com.tny.game.net.tunnel.Tunnel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.List;

public interface NettyAttrKeys {

    // AttributeKey<NetSession> SESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".SESSION");
    AttributeKey<Tunnel> TUNNEL = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".TUNNEL");

    AttributeKey<AppContext> CONTEXT = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".CONTEXT");

    // public static final AttributeKey<NetSession<?>> SERVER_SESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".SERVER_SESSION");
    // public static final AttributeKey<NetSession<?>> CLIENT_SESSION = AttributeKey.valueOf(ChannelHandlerContext.class.getName() + ".CLIENT_SESSION");


    AttrKey<MessageCommandBox> USER_COMMAND_BOX = AttrKeys.key(Session.class.getName() + "USER_COMMAND_BOX");

    AttributeKey<MessageBuilderFactory> MSG_BUILDER_FACTOR = AttributeKey.valueOf(NettyAttrKeys.class + "MSG_BUILDER_FACTOR");
    AttributeKey<List<ControllerChecker>> REQUEST_CHECKERS = AttributeKey.valueOf(NettyAttrKeys.class + "REQUEST_CHECKERS");
    AttributeKey<DataPacketEncoder> DATA_PACKET_ENCODER = AttributeKey.valueOf(NettyAttrKeys.class + "DATA_PACKET_ENCODER");
    AttributeKey<DataPacketDecoder> DATA_PACKET_DECODER = AttributeKey.valueOf(NettyAttrKeys.class + "DATA_PACKET_DECODER");
    // public static final AttributeKey<ResponseHandlerHolder> RESPONSE_HANDLER = AttributeKey.valueOf(NettyAttrKeys.class + "RESPONSE_HANDLER");

}
