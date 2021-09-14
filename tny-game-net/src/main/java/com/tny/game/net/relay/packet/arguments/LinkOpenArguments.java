package com.tny.game.net.relay.packet.arguments;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:10 下午
 */
public class LinkOpenArguments implements LinkPacketArguments {

	private final String serveName;

	private final long instance;

	private final String key;

	public LinkOpenArguments(String serveName, long instance, String key) {
		this.serveName = serveName;
		this.instance = instance;
		this.key = key;
	}

	public String getServeName() {
		return serveName;
	}

	public long getInstance() {
		return instance;
	}

	public String getKey() {
		return key;
	}

}
