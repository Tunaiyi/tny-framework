/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        if (pairs == null || pairs.isEmpty()) {
            return null;
        }
        KeyValue pair = pairs.getFirst();
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
