package com.tny.game.net.message.codec.list;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.google.common.primitives.Bytes;
import com.tny.game.net.message.codec.*;
import com.tny.game.protoex.annotations.Packed;

import java.util.List;

import static com.tny.game.net.message.codec.ProtobufConstants.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 4:09 下午
 */
@ProtobufClass
@ProtobufType(PROTOBUF_RAW_TYPE_ID_BYTE)
public class ByteListParam implements ListParam<Byte, byte[]> {

    @Packed
    @Protobuf(order = 1)
    private byte[] values;

    public ByteListParam() {
    }

    public ByteListParam(byte... values) {
        this.values = values;
    }

    public ByteListParam(List<Byte> values) {
        this.values = Bytes.toArray(values);
    }

    @Override
    public byte[] getValueArray() {
        return this.values;
    }

    @Override
    public List<Byte> getValueList() {
        return Bytes.asList(this.values);
    }

    @Override
    public String toString() {
        return Bytes.asList(this.values).toString();
    }

}
