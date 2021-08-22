package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class GeneralServerTunnel<UID> extends BaseServerTunnel<UID, NetSession<UID>, MessageTransporter<UID>> {

	public GeneralServerTunnel(long id, MessageTransporter<UID> transport, NetBootstrapContext<UID> bootstrapContext) {
		super(id, transport, bootstrapContext);
	}

}
