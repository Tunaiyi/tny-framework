package com.tny.game.net.netty4.relay;

import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 8:50 下午
 */
public class NettyLocalServeInstance extends BaseLocalServeInstance {

	public static final Logger LOGGER = LoggerFactory.getLogger(NettyLocalServeInstance.class);

	/**
	 * 连接任务
	 */
	private final NettyServeInstanceConnector connector;

	public NettyLocalServeInstance(NetLocalServeCluster cluster, ServeNode node, NettyServeInstanceConnector connector) {
		super(cluster, node);
		this.connector = connector;
	}

	@Override
	protected void prepareClose() {
		connector.stop();
	}

}