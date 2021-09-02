package com.tny.game.net.relay.packet.arguments;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:10 下午
 */
public class LinkOpenArguments implements LinkPacketArguments {

	private final String cluster;

	private final long instance;

	private final String key;

	public LinkOpenArguments(String cluster, long instance, String key) {
		this.cluster = cluster;
		this.instance = instance;
		this.key = key;
	}

	public String getCluster() {
		return cluster;
	}

	public long getInstance() {
		return instance;
	}

	public String getKey() {
		return key;
	}

}
