package com.tny.game.net.netty4.datagram.codec;

import com.tny.game.common.exception.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.result.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.netty4.datagram.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class DatagramPackV1Codec implements AppPrepareStart {

	protected final Logger logger;

	protected NettyMessageCodec messageCodec;

	protected CodecVerifier verifier;

	protected CodecCrypto crypto;

	protected DatagramPackCodecSetting config;

	public DatagramPackV1Codec() {
		logger = LoggerFactory.getLogger(this.getClass());
	}

	public DatagramPackV1Codec(DatagramPackCodecSetting config) {
		super();
		logger = LoggerFactory.getLogger(this.getClass());
		this.config = config;
	}

	@Override
	public PrepareStarter getPrepareStarter() {
		return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_6);
	}

	@Override
	public void prepareStart() {
		MessageBodyCodec<Object> bodyCoder = as(UnitLoader.getLoader(MessageBodyCodec.class).checkUnit(this.config.getBodyCodec()));
		MessageRelayStrategy messageRelayStrategy;
		if (this.config.isHasMessageRelayStrategy()) {
			messageRelayStrategy = as(UnitLoader.getLoader(MessageRelayStrategy.class).checkUnit(this.config.getMessageRelayStrategy()));
		} else {
			messageRelayStrategy = MessageRelayStrategy.NO_RELAY_STRATEGY;
		}
		this.messageCodec = new DefaultNettyMessageCodec(bodyCoder, messageRelayStrategy);
		this.verifier = UnitLoader.getLoader(CodecVerifier.class).checkUnit(this.config.getVerifier());
		this.crypto = UnitLoader.getLoader(CodecCrypto.class).checkUnit(this.config.getCrypto());
	}

	public DatagramPackV1Codec setMessageCodec(NettyMessageCodec messageCodec) {
		this.messageCodec = messageCodec;
		return this;
	}

	public DatagramPackV1Codec setVerifier(CodecVerifier verifier) {
		this.verifier = verifier;
		return this;
	}

	public DatagramPackV1Codec setCrypto(CodecCrypto crypto) {
		this.crypto = crypto;
		return this;
	}

	public DatagramPackV1Codec setConfig(DatagramPackCodecSetting config) {
		this.config = config;
		return this;
	}

	protected void handleOnDecodeError(ChannelHandlerContext ctx, Throwable exception) {
		handleOnError(ctx, exception, "Message解码");
	}

	protected void handleOnEncodeError(ChannelHandlerContext ctx, Throwable exception) {
		handleOnError(ctx, exception, "Message编码");
	}

	private void handleOnError(ChannelHandlerContext ctx, Throwable exception, String action) {
		Tunnel<?> tunnel = null;
		Channel channel = null;
		if (ctx != null) {
			channel = ctx.channel();
			tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).get();
		}
		boolean close = false;
		if (channel != null) {
			if (config.isCloseOnError()) {
				close = true;
			} else {
				ResultCode code = null;
				if (exception instanceof ResultCodeRuntimeException) {
					code = ((ResultCodeRuntimeException)exception).getCode();
				}
				if (exception instanceof ResultCodeException) {
					code = ((ResultCodeException)exception).getCode();
				}
				if (code != null && code.getType() == ResultCodeType.ERROR) {
					close = true;
				}
			}
			if (close) {
				channel.close();
			}
		}
		logger.error("# Tunnel ({}) [{}] {}异常 {}", tunnel, channel, action, close ? ", 服务器主动关闭连接" : "", exception);
	}

}
