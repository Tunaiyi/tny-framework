package com.tny.game.net.netty4.network;

import com.tny.game.net.codec.*;
import com.tny.game.net.transport.*;
import io.netty.util.AttributeKey;

public interface NettyNetAttrKeys {

	AttributeKey<NetTunnel<?>> TUNNEL = AttributeKey.valueOf(NettyNetAttrKeys.class.getName() + ".TUNNEL");

	AttributeKey<DataPackageContext> WRITE_PACKAGER = AttributeKey.valueOf(NettyNetAttrKeys.class, "WRITE_PACKAGER");
	AttributeKey<DataPackageContext> READ_PACKAGER = AttributeKey.valueOf(NettyNetAttrKeys.class, "READ_PACKAGER");

}
