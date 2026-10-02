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
import com.google.common.primitives.Longs;
import com.tny.game.codec.annotation.*;
import com.tny.game.codec.protobuf.*;
import com.tny.game.codec.typeprotobuf.annotation.*;

import java.util.*;

import static com.tny.game.codec.typeprotobuf.TypeProtobufTypeId.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 4:09 下午
 */
@ProtobufClass
@TypeProtobuf(PB_LONG_LIST)
@Codable(ProtobufMimeType.PROTOBUF)
public class PBLongList implements PBList<Long, long[]> {

    @Packed
    @Protobuf(order = 1)
    private List<Long> values;

    public PBLongList() {
    }

    public PBLongList(long... values) {
        this.values = Longs.asList(values);
    }

    public PBLongList(List<Long> values) {
        this.values = values;
    }

    @Override
    public List<Long> getValueList() {
        return Collections.unmodifiableList(this.values);
    }

    @Override
    public long[] getValueArray() {
        return Longs.toArray(this.values);
    }

    @Override
    public String toString() {
        return String.valueOf(this.values);
    }

}
