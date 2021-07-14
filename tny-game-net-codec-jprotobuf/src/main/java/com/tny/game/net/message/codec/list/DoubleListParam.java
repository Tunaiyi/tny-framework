package com.tny.game.net.message.codec.list;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.google.common.primitives.Doubles;
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
@ProtobufType(PROTOBUF_RAW_TYPE_ID_DOUBLE)
public class DoubleListParam implements ListParam<Double, double[]> {

    @Packed
    @Protobuf(order = 1)
    private List<Double> values;

    public DoubleListParam() {
    }

    public DoubleListParam(double... values) {
        this.values = Doubles.asList(values);
    }

    public DoubleListParam(List<Double> values) {
        this.values = values;
    }

    @Override
    public List<Double> getValueList() {
        return Collections.unmodifiableList(this.values);
    }

    @Override
    public double[] getValueArray() {
        return Doubles.toArray(this.values);
    }

    @Override
    public String toString() {
        return String.valueOf(this.values);
    }

}
