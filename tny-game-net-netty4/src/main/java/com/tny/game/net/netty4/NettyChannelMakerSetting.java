package com.tny.game.net.netty4;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/14 10:59 下午
 */
public class NettyChannelMakerSetting {

	private String makerClass = ReadTimeoutChannelMaker.class.getName();

	private Map<String, Object> properties = ImmutableMap.of();

	private Map<String, String> references = ImmutableMap.of();

	public String getMakerClass() {
		return this.makerClass;
	}

	public NettyChannelMakerSetting setMakerClass(String makerClass) {
		this.makerClass = makerClass;
		return this;
	}

	public Map<String, Object> getProperties() {
		return this.properties;
	}

	public NettyChannelMakerSetting setProperties(Map<String, Object> properties) {
		if (properties != null) {
			this.properties = properties;
		}
		return this;
	}

	public Map<String, String> getReferences() {
		return this.references;
	}

	public NettyChannelMakerSetting setReferences(Map<String, String> references) {
		if (references != null) {
			this.references = references;
		}
		return this;
	}

}
