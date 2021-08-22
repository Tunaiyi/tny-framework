package com.tny.game.net.netty4.codec;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.net.codec.*;
import org.apache.commons.lang3.StringUtils;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class NetPackV1Codec implements AppPrepareStart {

	public NettyMessageCodec messageCodec;

	public CodecVerifier verifier;

	public CodecCrypto crypto;

	public DataPacketCodecSetting config;

	public NetPackV1Codec() {
	}

	public NetPackV1Codec(DataPacketCodecSetting config) {
		super();
		this.config = config;
	}

	@Override
	public PrepareStarter getPrepareStarter() {
		return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_6);
	}

	@Override
	public void prepareStart() {
		MessageBodyCodec<Object> bodyCoder = as(UnitLoader.getLoader(MessageBodyCodec.class).checkUnit(this.config.getBodyCodec()));
		RelayStrategy relayStrategy;
		if (StringUtils.isBlank(this.config.getRelayStrategy())) {
			relayStrategy = RelayStrategy.NO_RELAY_STRATEGY;
		} else {
			relayStrategy = as(UnitLoader.getLoader(RelayStrategy.class).checkUnit(this.config.getRelayStrategy()));
		}
		this.messageCodec = new DefaultNettyMessageCodec(bodyCoder, relayStrategy);
		this.verifier = UnitLoader.getLoader(CodecVerifier.class).checkUnit(this.config.getVerifier());
		this.crypto = UnitLoader.getLoader(CodecCrypto.class).checkUnit(this.config.getCrypto());
	}

	public NetPackV1Codec setMessageCodec(NettyMessageCodec messageCodec) {
		this.messageCodec = messageCodec;
		return this;
	}

	public NetPackV1Codec setVerifier(CodecVerifier verifier) {
		this.verifier = verifier;
		return this;
	}

	public NetPackV1Codec setCrypto(CodecCrypto crypto) {
		this.crypto = crypto;
		return this;
	}

	public NetPackV1Codec setConfig(DataPacketCodecSetting config) {
		this.config = config;
		return this;
	}

}
