/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.codec.typeprotobuf.value;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.codec.annotation.*;
import com.tny.game.codec.protobuf.*;
import com.tny.game.codec.typeprotobuf.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.codec.typeprotobuf.TypeProtobufTypeId.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 4:09 下午
 */
@ProtobufClass
@TypeProtobuf(PB_SHORT_LIST)
@Codable(ProtobufMimeType.PROTOBUF)
public class PBShortList implements PBList<Short, short[]> {

    @Packed
    @Protobuf(order = 1)
    private List<Integer> values;

    public PBShortList() {
    }

    public PBShortList(short... values) {
        this.values = new ArrayList<>();
        int index = 0;
        for (short value : values) {
            this.values.add((int)value);
        }
    }

    public PBShortList(List<Short> values) {
        this.values = new ArrayList<>();
        int index = 0;
        for (Short value : values) {
            this.values.add(value.intValue());
        }
    }

    @Override
    public List<Short> getValueList() {
        return this.values.stream().map(Integer::shortValue).collect(Collectors.toList());
    }

    @Override
    public short[] getValueArray() {
        short[] shorts = new short[this.values.size()];
        int index = 0;
        for (int value : this.values) {
            shorts[index++] = (short)value;
        }
        return shorts;
    }

    @Override
    public String toString() {
        return String.valueOf(this.values);
    }

}
