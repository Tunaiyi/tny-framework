package com.tny.game.net.netty4.relay;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import org.slf4j.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 8:50 下午
 */
public class NettyServeInstance extends BaseLocalServeInstance {

	public static final Logger LOGGER = LoggerFactory.getLogger(NettyServeInstance.class);

	/**
	 * 本地转发上下文
	 */
	private final NettyLocalServeCluster cluster;

	/**
	 * 连接任务
	 */
	private Map<String, NettyRelayLinkConnector> connectorMap = new CopyOnWriteMap<>();

	private final int maxConnectorSize;

	public NettyServeInstance(NettyLocalServeCluster cluster, ServeNode target, int maxConnectorSize) {
		super(cluster, target);
		this.cluster = cluster;
		this.maxConnectorSize = maxConnectorSize;
	}

	public synchronized void connect() {
		if (!this.isClose()) {
			int size = Math.max(maxConnectorSize, connectorMap.size());
			int index = connectorMap.size();
			while (index < size) {
				NettyRelayLinkConnector task = new NettyRelayLinkConnector(cluster.createId(), this, cluster);
				connectorMap.put(task.getKey(), task);
				task.connect();
				index++;
			}
		}
	}

	@Override
	protected void prepareClose() {
		Map<String, NettyRelayLinkConnector> clearMap = this.connectorMap;
		this.connectorMap = new HashMap<>();
		clearMap.forEach((k, c) -> c.close());
	}

}