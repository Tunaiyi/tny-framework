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
