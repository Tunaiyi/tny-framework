package com.tny.game.net.netty4.relay.codec;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Kun Yang on 2018/8/13.
 */
public class RelayPacketCodecSetting {

	// 消息体编码器
	private String bodyCodec;

	// 错误时候是否关闭
	private boolean closeOnError = false;

	// 消息转发策略
	private String relayStrategy = null;

	public RelayPacketCodecSetting() {
	}

	public RelayPacketCodecSetting(boolean closeOnError) {
		this.closeOnError = closeOnError;
	}

	public boolean isHasRelayStrategy() {
		return StringUtils.isNoneBlank(relayStrategy);
	}

	public String getRelayStrategy() {
		return relayStrategy;
	}

	public RelayPacketCodecSetting setRelayStrategy(String relayStrategy) {
		this.relayStrategy = relayStrategy;
		return this;
	}

	public String getBodyCodec() {
		return bodyCodec;
	}

	public RelayPacketCodecSetting setBodyCodec(String bodyCodec) {
		this.bodyCodec = bodyCodec;
		return this;
	}

	public boolean isCloseOnError() {
		return closeOnError;
	}

	public RelayPacketCodecSetting setCloseOnError(boolean closeOnError) {
		this.closeOnError = closeOnError;
		return this;
	}

}
