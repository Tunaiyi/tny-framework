package com.tny.game.net.message.codec.list;

import com.baidu.bjf.remoting.protobuf.annotation.*;
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
@ProtobufType(PROTOBUF_RAW_TYPE_ID_STRING)
public class StringListParam implements ListParam<String, String[]> {

    @Packed
    @Protobuf(order = 1)
    private List<String> values;

    public StringListParam() {
    }

    public StringListParam(String... values) {
        this.values = Arrays.asList(values);
    }

    public StringListParam(List<String> values) {
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
