package com.tny.game.net.relay.packet.arguments;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:10 下午
 */
public class LinkOpenArguments implements LinkPacketArguments {

	private final String service;

	private final long instance;

	private final String key;

	public LinkOpenArguments(String service, long instance, String key) {
		this.service = service;
		this.instance = instance;
		this.key = key;
	}

	public String getService() {
		return service;
	}

	public long getInstance() {
		return instance;
	}

	public String getKey() {
		return key;
	}

}
