package com.tny.game.redisson.codec;

import org.redisson.client.codec.*;
import org.redisson.client.protocol.*;

import java.lang.reflect.Type;

import static com.tny.game.codec.CoderCharsets.*;
import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/10/13 3:07 下午
 */
public abstract class TypedBaseCodec extends BaseCodec {

    private static final StringCodec DEFAULT_KEY_CODEC = new StringCodec(DEFAULT);

    private final Type type;

    private final Codec keyCodec;

    private Encoder valueEncoder;

    private Decoder<Object> valueDecoder;

    public TypedBaseCodec(Type type, boolean initCodec) {
        this(type, null, initCodec);
    }

    public TypedBaseCodec(Type type, Codec keyCodec, boolean initCodec) {
        this.keyCodec = ifNull(keyCodec, DEFAULT_KEY_CODEC);
        this.type = type;
        if (initCodec) {
            this.initCodec();
        }
    }

    protected void initCodec() {
        this.valueEncoder = createEncoder(this.type);
        this.valueDecoder = createDecoder(this.type);
    }

    protected abstract Decoder<Object> createDecoder(Type type);

    protected abstract Encoder createEncoder(Type type);

    public Type getType() {
        return this.type;
    }

    @Override
    public Encoder getValueEncoder() {
        return this.valueEncoder;
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return this.valueDecoder;
    }

    @Override
    public Encoder getMapKeyEncoder() {
        return this.keyCodec.getMapKeyEncoder();
    }

    @Override
    public Decoder<Object> getMapKeyDecoder() {
        return as(this.keyCodec.getMapKeyDecoder());
    }

}
