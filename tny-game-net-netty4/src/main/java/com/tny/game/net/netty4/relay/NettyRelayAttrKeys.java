package com.tny.game.net.netty4.relay;

import com.tny.game.net.relay.link.*;
import io.netty.util.AttributeKey;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 6:01 下午
 */
public class NettyRelayAttrKeys {

    public static final AttributeKey<NetRelayLink> RELAY_LINK = AttributeKey.valueOf(NettyRelayAttrKeys.class + ".RELAY_LINK");

    public static final AttributeKey<RelayTransporter> RELAY_TRANSPORTER = AttributeKey.valueOf(NettyRelayAttrKeys.class + ".RELAY_TRANSPORTER");

}
