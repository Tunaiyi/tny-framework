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
import com.google.common.primitives.Ints;
import com.tny.game.codec.annotation.*;
import com.tny.game.codec.protobuf.*;
import com.tny.game.codec.typeprotobuf.*;
import com.tny.game.codec.typeprotobuf.annotation.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 4:09 下午
 */
@ProtobufClass
@TypeProtobuf(TypeProtobufTypeId.PB_INT_LIST)
@Codable(ProtobufMimeType.PROTOBUF)
public class PBIntList implements PBList<Integer, int[]> {

    @Packed
    @Protobuf(order = 1)
    private List<Integer> values;

    public PBIntList() {
    }

    public PBIntList(int... values) {
        this.values = Ints.asList(values);
    }

    public PBIntList(List<Integer> values) {
        this.values = values;
    }

    @Override
    public List<Integer> getValueList() {
        return Collections.unmodifiableList(this.values);
    }

    @Override
    public int[] getValueArray() {
        return Ints.toArray(this.values);
    }

    @Override
    public String toString() {
        return String.valueOf(this.values);
    }

}
