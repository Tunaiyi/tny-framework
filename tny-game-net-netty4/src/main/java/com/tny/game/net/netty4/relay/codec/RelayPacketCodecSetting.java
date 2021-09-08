package com.tny.game.net.netty4.relay.codec;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Kun Yang on 2018/8/13.
 */
public class RelayPacketCodecSetting {

	// 消息体编码器
	private String messageBodyCodec;

	// 错误时候是否关闭
	private boolean closeOnError = false;

	// 消息转发策略
	private String messageRelayStrategy = null;

	public RelayPacketCodecSetting() {
	}

	public RelayPacketCodecSetting(boolean closeOnError) {
		this.closeOnError = closeOnError;
	}

	public boolean isHasRelayStrategy() {
		return StringUtils.isNoneBlank(messageRelayStrategy);
	}

	public String getMessageRelayStrategy() {
		return messageRelayStrategy;
	}

	public RelayPacketCodecSetting setMessageRelayStrategy(String messageRelayStrategy) {
		this.messageRelayStrategy = messageRelayStrategy;
		return this;
	}

	public String getMessageBodyCodec() {
		return messageBodyCodec;
	}

	public RelayPacketCodecSetting setMessageBodyCodec(String messageBodyCodec) {
		this.messageBodyCodec = messageBodyCodec;
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
