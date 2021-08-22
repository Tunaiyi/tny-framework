package com.tny.game.relay.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/16 8:00 下午
 */
public class CommonAccessorRelayTunnel<UID> extends BaseServerTunnel<UID, NetSession<UID>, MessageTransporter<UID>> implements AccessorRelayTunnel<UID> {

	private RelayLink link;

	public CommonAccessorRelayTunnel(long id, MessageTransporter<UID> transporter, NetBootstrapContext<UID> bootstrapContext) {
		super(id, transporter, bootstrapContext);
	}

	@Override
	public void link(RelayLink link) {
		this.link = link;
	}

	@Override
	public WriteMessageFuture relay(Message message, boolean promise) {
		return link.relay(this.getId(), message, promise ? this.createWritePromise() : null);
	}

	@Override
	public WriteMessageFuture relay(MessageAllocator allocator, MessageFactory factory, MessageContext context) {
		return link.relay(this.getId(), allocator, factory, context);
	}

}
