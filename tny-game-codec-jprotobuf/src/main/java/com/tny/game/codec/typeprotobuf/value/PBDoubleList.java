package com.tny.game.codec.typeprotobuf.value;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.google.common.primitives.Doubles;
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
@TypeProtobuf(PB_DOUBLE_LIST)
@Codable(ProtobufMimeType.PROTOBUF)
public class PBDoubleList implements PBList<Double, double[]> {

    @Packed
    @Protobuf(order = 1)
    private List<Double> values;

    public PBDoubleList() {
    }

    public PBDoubleList(double... values) {
        this.values = Doubles.asList(values);
    }

    public PBDoubleList(List<Double> values) {
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
