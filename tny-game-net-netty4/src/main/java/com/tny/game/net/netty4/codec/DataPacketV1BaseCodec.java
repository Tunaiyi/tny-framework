package com.tny.game.net.netty4.codec;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.unit.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.codec.v1.*;
import com.tny.game.net.message.codec.*;
import org.apache.commons.lang3.StringUtils;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class DataPacketV1BaseCodec implements AppPrepareStart {

    public MessageCodec messageCodec;

    public CodecVerifier verifier;

    public CodecCrypto crypto;

    public DataPacketV1Config config;

    public DataPacketV1BaseCodec() {
    }

    public DataPacketV1BaseCodec(DataPacketV1Config config) {
        super();
        this.config = config;
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_6);
    }

    @Override
    public void prepareStart() {
        MessageBodyCodec<Object> bodyCoder = as(UnitLoader.getLoader(MessageBodyCodec.class).getUnitAnCheck(this.config.getBodyCodec()));
        //        MessageBodyCodec<Object> tailCoder = as(UnitLoader.getLoader(MessageBodyCodec.class).getUnit(this.config.getTailCodec(), null));
        DecodeStrategy decodeStrategy;
        if (StringUtils.isBlank(this.config.getBodyDecodeStrategy())) {
            decodeStrategy = DecodeStrategy.DECODE_ALL_STRATEGY;
        } else {
            decodeStrategy = as(UnitLoader.getLoader(DecodeStrategy.class).getUnitAnCheck(this.config.getBodyDecodeStrategy()));
        }
        this.messageCodec = new DefaultMessageCodec(bodyCoder, decodeStrategy);
        this.verifier = UnitLoader.getLoader(CodecVerifier.class).getUnitAnCheck(this.config.getVerifier());
        this.crypto = UnitLoader.getLoader(CodecCrypto.class).getUnitAnCheck(this.config.getCrypto());
    }

    public DataPacketV1BaseCodec setMessageCodec(MessageCodec messageCodec) {
        this.messageCodec = messageCodec;
        return this;
    }

    public DataPacketV1BaseCodec setVerifier(CodecVerifier verifier) {
        this.verifier = verifier;
        return this;
    }

    public DataPacketV1BaseCodec setCrypto(CodecCrypto crypto) {
        this.crypto = crypto;
        return this;
    }

    public DataPacketV1BaseCodec setConfig(DataPacketV1Config config) {
        this.config = config;
        return this;
    }

}
