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

    public ByteSequence toBytes(String value) {
        return ByteSequence.from(value, charset);
    }

    public String toString(ByteSequence value) {
        return value.toString(charset);
    }

    public <T> ByteSequence encode(T value, ObjectMimeType<T> type) {
        if (value == null) {
            return ByteSequence.EMPTY;
        }
        ObjectCodec<T> codec = codecOf(type);
        try {
            byte[] data = codec.encode(value);
            return ByteSequence.from(data);
        } catch (IOException e) {
            throw new NamespaceNodeCodecException(format("encode value {} exception", value, e));
        }
    }

    public <T> NameNode<T> decode(byte[] data, KeyValue kv, long createRevision, long version, ObjectMimeType<T> type) {
        String path = toString(kv.getKey());
        ObjectCodec<T> codec = codecOf(type);
        try {
            T value = codec.decode(data);
            boolean delete = kv.getVersion() == 0;
            return new NameNode<>(path, createRevision, value, version, kv.getModRevision(), delete);
        } catch (IOException e) {
            throw new NamespaceNodeCodecException(format("decode value {} exception", path, e));
        }
    }

    public <T> NameNode<T> decode(KeyValue kv, ObjectMimeType<T> type) {
        return this.decode(kv.getValue().getBytes(), kv, kv.getCreateRevision(), kv.getVersion(), type);
    }

    public <T> NameNode<T> decodeKeyValue(List<KeyValue> pairs, ObjectMimeType<T> type) {
        if (pairs == null || pairs.size() == 0) {
            return null;
        }
        KeyValue pair = pairs.get(0);
        return decode(pair, type);
    }

    public <T> List<NameNode<T>> decodeAllKeyValues(List<KeyValue> pairs, ObjectMimeType<T> type) {
        return pairs.stream().map(pair -> decode(pair, type)).collect(Collectors.toList());
    }

    private <T> ObjectCodec<T> codecOf(ObjectMimeType<T> type) {
        return type.hasMineType() ?
               objectCodecAdapter.codec(type.getType(), type.getMineType()) :
               objectCodecAdapter.codec(type.getType());
    }

}
