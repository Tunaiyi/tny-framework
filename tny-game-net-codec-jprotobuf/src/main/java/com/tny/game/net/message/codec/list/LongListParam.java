package com.tny.game.net.message.codec.list;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.google.common.primitives.Longs;
import com.tny.game.net.message.codec.*;
import com.tny.game.protoex.annotations.Packed;

import java.util.*;

import static com.tny.game.net.message.codec.ProtobufConstants.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 4:09 下午
 */
@ProtobufClass
@ProtobufType(PROTOBUF_RAW_TYPE_ID_LONG)
public class LongListParam implements ListParam<Long, long[]> {

    @Packed
    @Protobuf(order = 1)
    private List<Long> values;

    public LongListParam() {
    }

    public LongListParam(long... values) {
        this.values = Longs.asList(values);
    }

    public LongListParam(List<Long> values) {
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
