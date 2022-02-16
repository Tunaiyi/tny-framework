package com.tny.game.net.transport;

import com.tny.game.net.base.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class GeneralClientTunnel<UID, E extends NetTerminal<UID>> extends ClientTunnel<UID, E, MessageTransporter> {

	public GeneralClientTunnel(long id, NetworkContext context) {
		super(id, context);
	}

}
