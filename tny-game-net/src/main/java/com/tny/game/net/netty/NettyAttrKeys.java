package com.tny.game.net.netty;

import com.tny.game.common.context.*;
import com.tny.game.net.command.checker.ControllerChecker;
import com.tny.game.net.command.dispatcher.MessageCommandBox;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.netty.coder.*;
import com.tny.game.net.session.*;
import io.netty.util.AttributeKey;

import java.util.List;

public interface NettyAttrKeys {

    // AttributeKey<NetSession> SESSION = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".SESSION");
    AttributeKey<NettyTunnel> TUNNEL = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".TUNNEL");
    AttributeKey<NetSession> SESION = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".TUNNEL");
    AttributeKey<NettyClient> CLIENT = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".CLIENT");

    // public static final AttributeKey<NetSession<?>> SERVER_SESSION = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".SERVER_SESSION");
    // public static final AttributeKey<NetSession<?>> CLIENT_SESSION = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".CLIENT_SESSION");

    AttributeKey<MessageBuilderFactory> MSG_BUILDER_FACTOR = AttributeKey.valueOf(NettyAttrKeys.class + "MSG_BUILDER_FACTOR");
    AttributeKey<List<ControllerChecker>> REQUEST_CHECKERS = AttributeKey.valueOf(NettyAttrKeys.class + "REQUEST_CHECKERS");
    AttributeKey<DataPacketEncoder> DATA_PACKET_ENCODER = AttributeKey.valueOf(NettyAttrKeys.class + "DATA_PACKET_ENCODER");
    AttributeKey<DataPacketDecoder> DATA_PACKET_DECODER = AttributeKey.valueOf(NettyAttrKeys.class + "DATA_PACKET_DECODER");
    // public static final AttributeKey<ResponseHandlerHolder> RESPONSE_HANDLER = AttributeKey.valueOf(NettyAttrKeys.class + "RESPONSE_HANDLER");

    AttrKey<MessageCommandBox> USER_COMMAND_BOX = AttrKeys.key(Session.class.getName() + "USER_COMMAND_BOX");

}
