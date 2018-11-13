package com.tny.game.net.netty4.codec;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.unit.UnitLoader;
import com.tny.game.net.codec.*;
import com.tny.game.net.codec.v1.DataPacketV1Config;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.coder.Codec;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class DataPacketV1BaseCodec implements AppPrepareStart {

    protected Codec<Message<?>> codec;

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
        this.codec = as(UnitLoader.getLoader(Codec.class).getUnitAnCheck(config.getCodec()));
        this.verifier = UnitLoader.getLoader(CodecVerifier.class).getUnitAnCheck(config.getVerifier());
        this.crypto = UnitLoader.getLoader(CodecCrypto.class).getUnitAnCheck(config.getCrypto());
    }
}
