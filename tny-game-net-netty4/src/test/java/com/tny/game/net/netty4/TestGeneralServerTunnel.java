package com.tny.game.net.netty4;

import com.tny.game.net.base.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/19 4:30 下午
 */
public class TestGeneralServerTunnel extends GeneralServerTunnel<Long> {

	public TestGeneralServerTunnel(long id, Transporter<Long> transport, NetBootstrapContext<Long> bootstrapContext) {
		super(id, transport, bootstrapContext);
	}

	@Override
	protected Transporter<Long> getTransporter() {
		return super.getTransporter();
	}

}
