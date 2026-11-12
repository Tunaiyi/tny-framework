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
            this.values.add((int) value);
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
            shorts[index++] = (short) value;
        }
        return shorts;
    }

    @Override
    public String toString() {
        return String.valueOf(this.values);
    }

}
