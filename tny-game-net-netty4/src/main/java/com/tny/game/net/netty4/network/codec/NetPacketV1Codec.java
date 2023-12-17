/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.netty4.network.codec;

import com.tny.game.common.exception.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.result.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import org.slf4j.*;

public abstract class NetPacketV1Codec implements AppPrepareStart {

    protected final Logger logger;

    protected NettyMessageCodec messageCodec;

    protected CodecVerifier verifier;

    protected CodecCrypto crypto;

    protected NetPacketCodecSetting config;

    public NetPacketV1Codec() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public NetPacketV1Codec(NetPacketCodecSetting config) {
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
        MessageBodyCodec<Object> bodyCoder = UnitLoader.getLoader(MessageBodyCodec.class).checkUnit(this.config.getMessageBodyCodec());
        MessageHeaderCodec messageHeaderCodec = UnitLoader.getLoader(MessageHeaderCodec.class).checkUnit(this.config.getMessageHeaderCodec());
        MessageRelayStrategy messageRelayStrategy;
        if (this.config.isHasMessageRelayStrategy()) {
            messageRelayStrategy = UnitLoader.getLoader(MessageRelayStrategy.class).checkUnit(this.config.getMessageRelayStrategy());
        } else {
            messageRelayStrategy = MessageRelayStrategy.NO_RELAY_STRATEGY;
        }
        this.messageCodec = new DefaultNettyMessageCodec(bodyCoder, messageHeaderCodec, messageRelayStrategy);
        this.verifier = UnitLoader.getLoader(CodecVerifier.class).checkUnit(this.config.getVerifier());
        this.crypto = UnitLoader.getLoader(CodecCrypto.class).checkUnit(this.config.getCrypto());
    }

    public NetPacketV1Codec setMessageCodec(NettyMessageCodec messageCodec) {
        this.messageCodec = messageCodec;
        return this;
    }

    public NetPacketV1Codec setVerifier(CodecVerifier verifier) {
        this.verifier = verifier;
        return this;
    }

    public NetPacketV1Codec setCrypto(CodecCrypto crypto) {
        this.crypto = crypto;
        return this;
    }

    public NetPacketV1Codec setConfig(NetPacketCodecSetting config) {
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
        Tunnel tunnel = null;
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
                if (exception instanceof ResultCodableException) {
                    code = ((ResultCodableException) exception).getCode();
                }
                if (code != null && code.getLevel() == ResultLevel.ERROR) {
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
