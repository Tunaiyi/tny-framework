package com.tny.game.net.message.codec.list;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.google.common.primitives.Booleans;
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
@ProtobufType(PROTOBUF_RAW_TYPE_ID_BOOL)
public class BooleanListParam implements ListParam<Boolean, boolean[]> {

    @Packed
    @Protobuf(order = 1)
    private List<Boolean> values;

    public BooleanListParam() {
    }

    public BooleanListParam(boolean... values) {
        this.values = Booleans.asList(values);
    }

    public BooleanListParam(List<Boolean> values) {
        this.values = values;
    }

    @Override
    public List<Boolean> getValueList() {
        return Collections.unmodifiableList(this.values);
    }

    @Override
    public boolean[] getValueArray() {
        return Booleans.toArray(this.values);
    }

    @Override
    public String toString() {
        return String.valueOf(this.values);
    }

}
