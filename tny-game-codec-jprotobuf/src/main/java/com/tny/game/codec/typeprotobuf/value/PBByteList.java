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
