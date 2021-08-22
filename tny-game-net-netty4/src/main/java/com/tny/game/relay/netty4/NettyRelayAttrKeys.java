package com.tny.game.relay.netty4;

import com.tny.game.relay.transport.*;
import io.netty.util.AttributeKey;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 6:01 下午
 */
public class NettyRelayAttrKeys {

	public static final AttributeKey<RelayTunnel<?>> RELAY_TUNNEL = AttributeKey.valueOf(NettyRelayAttrKeys.class + ".RELAY_TUNNEL");

	public static final AttributeKey<NetRelayLink> RELAY_LINK = AttributeKey.valueOf(NettyRelayAttrKeys.class + ".RELAY_LINK");

}
