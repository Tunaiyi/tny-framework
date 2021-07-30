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
@Codecable(ProtobufMimeType.PROTOBUF)
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
