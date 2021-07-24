package com.tny.game.common.codec.protobuf.value;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.google.common.primitives.Booleans;
import com.tny.game.common.codec.annotation.*;
import com.tny.game.common.codec.protobuf.*;
import com.tny.game.common.codec.typeprotobuf.annotation.*;

import java.util.*;

import static com.tny.game.common.codec.typeprotobuf.TypeProtobufTypeId.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 4:09 下午
 */
@ProtobufClass
@TypeProtobuf(PB_BOOLEAN_LIST)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBBooleanList implements PBList<Boolean, boolean[]> {

    @Packed
    @Protobuf(order = 1)
    private List<Boolean> values;

    public PBBooleanList() {
    }

    public PBBooleanList(boolean... values) {
        this.values = Booleans.asList(values);
    }

    public PBBooleanList(List<Boolean> values) {
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
