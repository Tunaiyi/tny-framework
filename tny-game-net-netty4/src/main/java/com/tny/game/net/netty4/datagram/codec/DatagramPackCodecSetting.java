package com.tny.game.net.netty4.datagram.codec;

import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;

import static com.tny.game.net.base.configuration.NetUnitNames.*;

/**
 * Created by Kun Yang on 2018/8/13.
 */
public class DatagramPackCodecSetting extends DataPackCodecOptions {

	// 消息体编码器
	private String bodyCodec = null;

	// 消息转发策略
	private String relayStrategy = null;

	// 消息体验证器
	private String verifier = lowerCamelName(CRC64CodecVerifier.class);

	// 消息体加密器
	private String crypto = lowerCamelName(XOrCodecCrypto.class);

	private boolean closeOnError = false;

	public DatagramPackCodecSetting() {
	}

	public String getBodyCodec() {
		return this.bodyCodec;
	}

	public String getRelayStrategy() {
		return this.relayStrategy;
	}

	public String getVerifier() {
		return this.verifier;
	}

	public String getCrypto() {
		return this.crypto;
	}

	public boolean isCloseOnError() {
		return closeOnError;
	}

	public DatagramPackCodecSetting setVerifier(String verifier) {
		this.verifier = verifier;
		return this;
	}

	public DatagramPackCodecSetting setCrypto(String crypto) {
		this.crypto = crypto;
		return this;
	}

	public DatagramPackCodecSetting setBodyCodec(String bodyCodec) {
		this.bodyCodec = bodyCodec;
		return this;
	}

	public DatagramPackCodecSetting setRelayStrategy(String relayStrategy) {
		this.relayStrategy = relayStrategy;
		return this;
	}

	public DatagramPackCodecSetting setCloseOnError(boolean closeOnError) {
		this.closeOnError = closeOnError;
		return this;
	}

}
