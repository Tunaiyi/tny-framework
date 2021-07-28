package com.tny.game.codec.protobuf.value;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.google.common.primitives.Floats;
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
@TypeProtobuf(TypeProtobufTypeId.PB_FLOAT_LIST)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBFloatList implements PBList<Float, float[]> {

    @Packed
    @Protobuf(order = 1)
    private List<Float> values;

    public PBFloatList() {
    }

    public PBFloatList(float... values) {
        this.values = Floats.asList(values);
    }

    public PBFloatList(List<Float> values) {
        this.values = values;
    }

    @Override
    public List<Float> getValueList() {
        return Collections.unmodifiableList(this.values);
    }

    @Override
    public float[] getValueArray() {
        return Floats.toArray(this.values);
    }

    @Override
    public String toString() {
        return String.valueOf(this.values);
    }

}
