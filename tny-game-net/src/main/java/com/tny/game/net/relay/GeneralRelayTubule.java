package com.tny.game.net.relay;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/3 12:02 下午
 */
public class GeneralRelayTubule<UID> extends BaseServerTunnel<UID, NetSession<UID>, RelayTubuleTransporter<UID>> implements NetRelayTubule<UID> {

	private final long createTime;

	private final InetSocketAddress remoteAddress;

	protected GeneralRelayTubule(long id, RelayTubuleTransporter<UID> transporter, InetSocketAddress remoteAddress,
			NetBootstrapContext<UID> bootstrapContext) {
		super(id, transporter, bootstrapContext);
		this.remoteAddress = remoteAddress;
		this.createTime = System.currentTimeMillis();
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return this.remoteAddress;
	}

	@Override
	public long getCreateTime() {
		return createTime;
	}

	@Override
	public void pong() {
		this.transporter.pong();
	}

}
