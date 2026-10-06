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

package com.tny.game.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.*;
import com.google.protobuf.*;
import com.tny.game.codec.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/10/9 5:56 下午
 */
public class ProtobufObjectCodec<T> implements ObjectCodec<T> {

    private final Codec<T> codec;

    public ProtobufObjectCodec(Class<T> type) {
        this.codec = ProtobufProxy.create(type);
    }

    @Override
    public boolean isPlaintext() {
        return false;
    }

    @Override
    public byte[] encode(T value) throws IOException {
        if (value == null) {
            return new byte[0];
        }
        return this.codec.encode(value);
    }

    @Override
    public void encode(T value, OutputStream output) throws IOException {
        CodedOutputStream out = CodedOutputStream.newInstance(output);
        codec.writeTo(value, out);
        out.flush();
    }

    @Override
    public T decode(byte[] bytes) throws IOException {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        return this.codec.decode(bytes);
    }

    @Override
    public T decode(InputStream input) throws IOException {
        CodedInputStream in = CodedInputStream.newInstance(input);
        return codec.readFrom(in);
    }

}
