package com.tny.game.net.message.codec.list;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.google.common.primitives.Ints;
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
@ProtobufType(PROTOBUF_RAW_TYPE_ID_INT)
public class IntListParam implements ListParam<Integer, int[]> {

    @Packed
    @Protobuf(order = 1)
    private List<Integer> values;

    public IntListParam() {
    }

    public IntListParam(int... values) {
        this.values = Ints.asList(values);
    }

    public IntListParam(List<Integer> values) {
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
