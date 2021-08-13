package com.tny.game.net.netty4.relay;

import com.tny.game.net.relay.*;
import io.netty.util.AttributeKey;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 6:01 下午
 */
public class NettyRelayAttributeKeys {

	public static final AttributeKey<RelayPipe<?>> RELAY_PIPE = AttributeKey.valueOf("RELAY_PIPE");

}
