package com.tny.game.net.transport;

import com.tny.game.net.base.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class GeneralClientTunnel<UID, E extends NetTerminal<UID>> extends BaseClientTunnel<UID, E, MessageTransporter<UID>> {

	public GeneralClientTunnel(long id, NetworkContext<UID> context) {
		super(id, context);
	}

}
