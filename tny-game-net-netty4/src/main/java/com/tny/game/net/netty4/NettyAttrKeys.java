package com.tny.game.net.netty4;

import com.tny.game.net.codec.*;
import com.tny.game.net.netty4.codec.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.transport.*;
import io.netty.util.AttributeKey;

public interface NettyAttrKeys {

    AttributeKey<NetTunnel<?>> TUNNEL = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".TUNNEL");
    AttributeKey<NetPipe<?>> PIPE = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".PIPE");

    AttributeKey<DataPacketMarker> DATA_PACKET_MARKER = AttributeKey.valueOf(NettyAttrKeys.class, "DATA_PACKET_MARKER");

    AttributeKey<DataPackageContext> WRITE_PACKAGER = AttributeKey.valueOf(NettyAttrKeys.class, "WRITE_PACKAGER");
    AttributeKey<DataPackageContext> READ_PACKAGER = AttributeKey.valueOf(NettyAttrKeys.class, "READ_PACKAGER");

}
