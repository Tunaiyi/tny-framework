package com.tny.game.common.codec.protobuf.value;

import com.baidu.bjf.remoting.protobuf.annotation.*;
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
@TypeProtobuf(PB_STRING_LIST)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBStringList implements PBList<String, String[]> {

    @Packed
    @Protobuf(order = 1)
    private List<String> values;

    public PBStringList() {
    }

    public PBStringList(String... values) {
        this.values = Arrays.asList(values);
    }

    public PBStringList(List<String> values) {
        this.values = values;
    }

    @Override
    public List<String> getValueList() {
        return this.values;
    }

    @Override
    public String[] getValueArray() {
        return this.values.toArray(new String[0]);
    }

    @Override
    public String toString() {
        return String.valueOf(this.values);
    }

}
