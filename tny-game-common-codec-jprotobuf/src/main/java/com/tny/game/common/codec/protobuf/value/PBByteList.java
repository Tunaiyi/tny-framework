package com.tny.game.common.codec.protobuf.value;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.google.common.primitives.Bytes;
import com.tny.game.common.codec.annotation.*;
import com.tny.game.common.codec.protobuf.*;
import com.tny.game.common.codec.typeprotobuf.annotation.*;

import java.util.List;

import static com.tny.game.common.codec.typeprotobuf.TypeProtobufTypeId.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 4:09 下午
 */
@ProtobufClass
@TypeProtobuf(PB_BYTE_LIST)
@Codecable(ProtobufMimeType.PROTOBUF)
public class PBByteList implements PBList<Byte, byte[]> {

    @Packed
    @Protobuf(order = 1)
    private byte[] values;

    public PBByteList() {
    }

    public PBByteList(byte... values) {
        this.values = values;
    }

    public PBByteList(List<Byte> values) {
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
