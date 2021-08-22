package com.tny.game.relay.transport;

import com.tny.game.relay.packet.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/4 8:17 下午
 */
public class CommonRelayLink<UID> extends BaseRelayLink<UID> {

	public static final Logger LOGGER = LoggerFactory.getLogger(CommonRelayLink.class);

	public CommonRelayLink(String id, RelayTransporter transporter) {
		super(id, transporter);
	}

	@Override
	public void receive(RelayPacket<?> packet) {

	}

}
