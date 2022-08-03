package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.exception.*;
import io.etcd.jetcd.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.StringAide.*;

/**
 * Etcd对象
 * <p>
 *
 * @author kgtny
 * @date 2022/7/1 03:19
 **/
public abstract class EtcdObject {

    protected Charset charset;

    protected final ObjectCodecAdapter objectCodecAdapter;

    public EtcdObject(ObjectCodecAdapter objectCodecAdapter, Charset charset) {
        this.objectCodecAdapter = objectCodecAdapter;
        this.charset = charset;
    }

    protected ByteSequence toBytes(String value) {
        return ByteSequence.from(value, charset);
    }

    protected String toString(ByteSequence value) {
        return value.toString(charset);
    }

    protected <T> ByteSequence encode(T value, ObjectMineType<T> type) {
        if (value == null) {
            return ByteSequence.EMPTY;
        }
        ObjectCodec<T> codec = codecOf(type);
        try {
            byte[] data = codec.encode(value);
            return ByteSequence.from(data);
        } catch (IOException e) {
            throw new NameNodeCodecException(format("encode value {} exception", value, e));
        }
    }

    protected <T> NameNode<T> decode(byte[] data, KeyValue kv, long createRevision, ObjectMineType<T> type) {
        String path = toString(kv.getKey());
        ObjectCodec<T> codec = codecOf(type);
        try {
            T value = codec.decode(data);
            return new NameNode<>(path, createRevision, value, kv.getVersion(), kv.getModRevision());
        } catch (IOException e) {
            throw new NameNodeCodecException(format("decode value {} exception", path, e));
        }
    }

    protected <T> NameNode<T> decode(KeyValue kv, ObjectMineType<T> type) {
        return this.decode(kv.getValue().getBytes(), kv, kv.getCreateRevision(), type);
    }

    protected <T> NameNode<T> decodeKeyValue(List<KeyValue> pairs, ObjectMineType<T> type) {
        if (pairs == null || pairs.size() == 0) {
            return null;
        }
        KeyValue pair = pairs.get(0);
        return decode(pair, type);
    }

    protected <T> List<NameNode<T>> decodeAllKeyValues(List<KeyValue> pairs, ObjectMineType<T> type) {
        return pairs.stream().map(pair -> decode(pair, type)).collect(Collectors.toList());
    }

    private <T> ObjectCodec<T> codecOf(ObjectMineType<T> type) {
        return type.hasMineType() ?
                objectCodecAdapter.codec(type.getType(), type.getMineType()) :
                objectCodecAdapter.codec(type.getType());
    }

}
