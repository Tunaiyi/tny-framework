package com.tny.game.net.netty4;

import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.transport.*;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public class NettyServerTunnelTest extends NettyTunnelTest<NetSession<Long>, TestGeneralServerTunnel, MockNetEndpoint> {

	public static final NetIdGenerator ID_GENERATOR = new SimpleNetIdGenerator();

	@Override
	protected TunnelTestInstance<TestGeneralServerTunnel, MockNetEndpoint> create(Certificate<Long> certificate, boolean open) {
		MockNetEndpoint endpoint = createEndpoint(certificate);
		TestGeneralServerTunnel tunnel = this.newTunnel(open);
		tunnel.bind(endpoint);
		//        if (certificate.isAuthenticated()) {
		//            tunnel.bind(endpoint);
		//        }
		return new TunnelTestInstance<>(tunnel, endpoint);
	}

	@Override
	protected MockNetEndpoint createEndpoint(Certificate<Long> certificate) {
		return new MockNetEndpoint(certificate);
	}

	private TestGeneralServerTunnel newTunnel(boolean open) {
		TestGeneralServerTunnel tunnel = new TestGeneralServerTunnel(ID_GENERATOR.generate(), new NettyChannelMessageTransporter<>(mockChannel()),
				new NetBootstrapContext<>(null, null, new CommonMessageFactory(), null));
		if (open) {
			tunnel.open();
		}
		return tunnel;
	}

	@Override
	protected EmbeddedChannel embeddedChannel(TestGeneralServerTunnel tunnel) {
		return (EmbeddedChannel)((NettyChannelMessageTransporter<?>)tunnel.getTransporter()).getChannel();
	}

}