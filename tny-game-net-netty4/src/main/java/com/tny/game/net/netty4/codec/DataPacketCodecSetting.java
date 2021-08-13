package com.tny.game.net.netty4.codec;

import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;

/**
 * Created by Kun Yang on 2018/8/13.
 */
public class DataPacketCodecSetting extends DataPackCodecOptions {

	// 消息体编码器
	private String bodyCodec = MessageBodyCodec.class.getSimpleName();

	// 消息体解码测刘娥
	private String relayStrategy = null;

	// 消息体验证器
	private String verifier = CRC64CodecVerifier.class.getSimpleName();

	// 消息体加密器
	private String crypto = XOrCodecCrypto.class.getSimpleName();

	public DataPacketCodecSetting() {
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

	//	public CommonDataPackCodecOptions getOptions() {
	//		return options;
	//	}

	public DataPacketCodecSetting setVerifier(String verifier) {
		this.verifier = verifier;
		return this;
	}

	public DataPacketCodecSetting setCrypto(String crypto) {
		this.crypto = crypto;
		return this;
	}

	public DataPacketCodecSetting setBodyCodec(String bodyCodec) {
		this.bodyCodec = bodyCodec;
		return this;
	}
	//    public DataPacketV1Config setTailCodec(String tailCodec) {
	//        this.tailCodec = tailCodec;
	//        return this;
	//    }

	public DataPacketCodecSetting setRelayStrategy(String relayStrategy) {
		this.relayStrategy = relayStrategy;
		return this;
	}

}
