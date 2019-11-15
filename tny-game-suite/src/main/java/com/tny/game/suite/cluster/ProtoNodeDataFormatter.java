package com.tny.game.suite.cluster;

import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.zookeeper.*;

public class ProtoNodeDataFormatter implements NodeDataFormatter {

    @Override
    public byte[] data2Bytes(Object data) {
        if (data == null)
            return new byte[0];
        if (data instanceof byte[] && ((byte[]) data).length == 0)
            return (byte[]) data;
        ProtoExWriter writer = new ProtoExWriter(256);
        writer.writeMessage(data, TypeEncode.EXPLICIT);
        return writer.toByteArray();
    }

    @Override
    public <D> D bytes2Data(byte[] bytes) {
        if (bytes.length == 0)
            return null;
        ProtoExReader reader = new ProtoExReader(bytes);
        return reader.readMessage();
    }

}
