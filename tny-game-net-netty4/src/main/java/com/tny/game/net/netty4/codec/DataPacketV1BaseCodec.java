package com.tny.game.net.netty4.codec;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.unit.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.codec.v1.*;
import com.tny.game.net.message.coder.*;
import org.apache.commons.lang3.StringUtils;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class DataPacketV1BaseCodec implements AppPrepareStart {

    protected MessageCodec<Object> messageCodec;

    protected CodecVerifier verifier;

    protected CodecCrypto crypto;

    protected DataPacketV1Config config;

    protected DataPacketV1BaseCodec() {
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
        Codec<Object> bodyCoder = as(UnitLoader.getLoader(Codec.class).getUnitAnCheck(this.config.getBodyCodec()));
        Codec<Object> tailCoder = as(UnitLoader.getLoader(Codec.class).getUnit(this.config.getTailCodec(), null));
        DecodeStrategy decodeStrategy;
        if (StringUtils.isBlank(this.config.getBodyDecodeStrategy()))
            decodeStrategy = DecodeStrategy.DECODE_ALL_STRATEGY;
        else
            decodeStrategy = as(UnitLoader.getLoader(DecodeStrategy.class).getUnitAnCheck(this.config.getBodyDecodeStrategy()));
        this.messageCodec = new DefaultMessageCodec<>(bodyCoder, tailCoder, decodeStrategy);
        this.verifier = UnitLoader.getLoader(CodecVerifier.class).getUnitAnCheck(this.config.getVerifier());
        this.crypto = UnitLoader.getLoader(CodecCrypto.class).getUnitAnCheck(this.config.getCrypto());
    }
}
