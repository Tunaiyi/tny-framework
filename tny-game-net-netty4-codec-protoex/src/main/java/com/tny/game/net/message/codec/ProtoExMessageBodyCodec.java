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

package com.tny.game.net.message.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;
import io.netty.buffer.ByteBuf;

@Unit
public class ProtoExMessageBodyCodec<T> implements MessageBodyCodec<T> {

    @Override
    public T decode(ByteBuf buffer) {
        try (ProtoExReader bodyReader = new ProtoExReader(new ProtoExInputStream(buffer.nioBuffer()))) {
            return bodyReader.readMessage();
        }
    }

    @Override
    public void encode(T object, ByteBuf code) {
        try (ProtoExWriter writer = new ProtoExWriter()) {
            if (object != null) {
                writer.writeMessage(object, TypeEncode.EXPLICIT);
            }
            code.writeBytes(writer.toByteArray());
        }
    }

}
