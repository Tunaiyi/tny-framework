package com.tny.game.net.netty4;

import com.tny.game.net.codec.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.codec.*;
import io.netty.util.AttributeKey;

public interface NettyAttrKeys {

    AttributeKey<NetEndpoint> ENDPOINT = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".ENDPOINT");
    AttributeKey<NettyTunnel> TUNNEL = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".TUNNEL");
    // AttributeKey<NetSession<?>> SESSION = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".TUNNEL");
    // AttributeKey<NettyConnector> CLIENT = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".CLIENT");

    // public static final AttributeKey<NetSession<?>> SERVER_SESSION = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".SERVER_SESSION");
    // public static final AttributeKey<NetSession<?>> CLIENT_SESSION = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".CLIENT_SESSION");

    AttributeKey<MessageFactory> MSG_BUILDER_FACTOR = AttributeKey.valueOf(NettyAttrKeys.class + "MSG_BUILDER_FACTOR");
    // AttributeKey<List<ControllerChecker>> REQUEST_CHECKERS = AttributeKey.valueOf(NettyAttrKeys.class + "REQUEST_CHECKERS");
    AttributeKey<DataPacketEncoder> DATA_PACKET_ENCODER = AttributeKey.valueOf(NettyAttrKeys.class + "DATA_PACKET_ENCODER");
    AttributeKey<DataPacketDecoder> DATA_PACKET_DECODER = AttributeKey.valueOf(NettyAttrKeys.class + "DATA_PACKET_DECODER");
    // public static final AttributeKey<ResponseHandlerHolder> RESPONSE_HANDLER = AttributeKey.valueOf(NettyAttrKeys.class + "RESPONSE_HANDLER");

    AttributeKey<DataPackager> WRITE_PACKAGER = AttributeKey.valueOf(NettyAttrKeys.class, "WRITE_PACKAGER");
    AttributeKey<DataPackager> READ_PACKAGER = AttributeKey.valueOf(NettyAttrKeys.class, "READ_PACKAGER");
    AttributeKey<Long> ACCESS_ID = AttributeKey.valueOf(NettyAttrKeys.class, "ACCESS_ID");
}
