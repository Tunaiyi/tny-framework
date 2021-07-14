package com.tny.game.net.message.codec;

import com.tny.game.common.enums.*;
import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.protoex.*;
import org.slf4j.*;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.codec.ProtobufConstants.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/11 11:13 下午
 */
@Unit
public class JProtobufMessageBodyCodec<T> implements MessageBodyCodec<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JProtobufMessageBodyCodec.class);

    private final ProtobufObjectCodecorFactory codecorFactory;

    public JProtobufMessageBodyCodec() {
        this.codecorFactory = ProtobufObjectCodecorFactory.getDefault();
    }

    public JProtobufMessageBodyCodec(ProtobufObjectCodecorFactory codecorFactory) {
        this.codecorFactory = codecorFactory;
    }

    @Override
    public T decode(byte[] bytes) throws Exception {
        ProtoExInputStream input = new ProtoExInputStream(bytes);
        List<Object> paramList = null;
        Object body = null;
        while (input.hasRemaining()) {
            byte option = input.readByte();
            if ((option & PROTOBUF_MESSAGE_PARAMS) != 0 && paramList == null) {
                paramList = new ArrayList<>();
                body = new MessageParamList(paramList);
            }
            ProtobufRawType rawType = EnumAide.of(ProtobufRawType.class, (byte)(option & PROTOBUF_HEAD_TYPE_BIT_MASK));
            Object value = doDecode(rawType, input);
            if (paramList != null) {
                paramList.add(value);
            } else {
                body = value;
            }
        }
        return as(body);
    }

    private Object doDecode(ProtobufRawType rawType, ProtoExInputStream input) throws Exception {
        if (rawType.isHasValueReader()) {
            return rawType.readerValue(input);
        } else {
            int protobufId = input.readInt();
            ProtobufCodec<Object> codec = this.codecorFactory.getCodecor(protobufId);
            if (codec == null) {
                LOGGER.warn("未找到 protobufId = {} 关联的 ProtobufCodec !!!", protobufId);
                return null;
            }
            return codec.decode(input.readBytes());
        }
    }

    @Override
    public byte[] encode(T object) throws Exception {
        ProtoExOutputStream output = new ProtoExOutputStream();
        if (object instanceof MessageParamList) {
            for (Object param : ((MessageParamList)object)) {
                doEncode(output, param, true);
            }
        } else {
            doEncode(output, object, false);
        }
        byte[] data = output.toByteArray();
        return data;
    }

    private void doEncode(ProtoExOutputStream output, Object object, boolean params) throws Exception {
        byte option = params ? PROTOBUF_MESSAGE_PARAMS : 0;
        if (object == null) {
            output.writeByte((byte)(option | ProtobufRawType.NULL.option()));
            return;
        }
        ProtobufRawType rawType = ProtobufRawType.ofObject(object);
        output.writeByte((byte)(option | rawType.option()));
        if (rawType.isHasValueWriter()) {
            rawType.writeValue(output, object);
        } else {
            ProtobufCodec<Object> codec = this.codecorFactory.getCodecor(object.getClass());
            output.writeInt(codec.getTypeId());
            byte[] objectBytes = codec.encode(object);
            output.writeBytes(objectBytes);
        }
    }

}
