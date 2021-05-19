package com.tny.game.net.netty4;

import com.tny.game.net.codec.*;
import com.tny.game.net.transport.*;
import io.netty.util.AttributeKey;

public interface NettyAttrKeys {

    AttributeKey<NetTunnel> TUNNEL = AttributeKey.valueOf(NettyAttrKeys.class.getName() + ".TUNNEL");

    AttributeKey<DataPackager> WRITE_PACKAGER = AttributeKey.valueOf(NettyAttrKeys.class, "WRITE_PACKAGER");
    AttributeKey<DataPackager> READ_PACKAGER = AttributeKey.valueOf(NettyAttrKeys.class, "READ_PACKAGER");

}
