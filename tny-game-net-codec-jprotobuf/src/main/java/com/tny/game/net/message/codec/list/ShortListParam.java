package com.tny.game.net.message.codec.list;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.message.codec.*;
import com.tny.game.protoex.annotations.Packed;

import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.net.message.codec.ProtobufConstants.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 4:09 下午
 */
@ProtobufClass
@ProtobufType(PROTOBUF_RAW_TYPE_ID_SHORT)
public class ShortListParam implements ListParam<Short, short[]> {

    @Packed
    @Protobuf(order = 1)
    private List<Integer> values;

    public ShortListParam() {
    }

    public ShortListParam(short... values) {
        this.values = new ArrayList<>();
        int index = 0;
        for (short value : values) {
            this.values.add((int)value);
        }
    }

    public ShortListParam(List<Short> values) {
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
