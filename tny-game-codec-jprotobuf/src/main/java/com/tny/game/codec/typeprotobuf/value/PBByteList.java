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

package com.tny.game.codec.typeprotobuf.value;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.google.common.primitives.Bytes;
import com.tny.game.codec.annotation.*;
import com.tny.game.codec.protobuf.*;
import com.tny.game.codec.typeprotobuf.annotation.*;

import java.util.List;

import static com.tny.game.codec.typeprotobuf.TypeProtobufTypeId.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 4:09 下午
 */
@ProtobufClass
@TypeProtobuf(PB_BYTE_LIST)
@Codable(ProtobufMimeType.PROTOBUF)
public class PBByteList implements PBList<Byte, byte[]> {

    @Packed
    @Protobuf(order = 1)
    private byte[] values;

    public PBByteList() {
    }

    public PBByteList(byte... values) {
        this.values = values;
    }

    public PBByteList(List<Byte> values) {
        this.values = Bytes.toArray(values);
    }

    @Override
    public byte[] getValueArray() {
        return this.values;
    }

    @Override
    public List<Byte> getValueList() {
        return Bytes.asList(this.values);
    }

    @Override
    public String toString() {
        return Bytes.asList(this.values).toString();
    }

}
