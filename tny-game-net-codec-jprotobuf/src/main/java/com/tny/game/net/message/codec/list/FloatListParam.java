package com.tny.game.net.message.codec.list;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.google.common.primitives.Floats;
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
@ProtobufType(PROTOBUF_RAW_TYPE_ID_FLOAT)
public class FloatListParam implements ListParam<Float, float[]> {

    @Packed
    @Protobuf(order = 1)
    private List<Float> values;

    public FloatListParam() {
    }

    public FloatListParam(float... values) {
        this.values = Floats.asList(values);
    }

    public FloatListParam(List<Float> values) {
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
