package com.tny.game.net.coder;

import com.tny.game.net.base.Message;
import com.tny.game.utils.CompressUtils;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tny.game.net.coder.CoderContent.FRAME_MAGIC;

public class SimpleDataPacketEncoder implements DataPacketEncoder {

    public static final Logger LOGGER = LoggerFactory.getLogger(DataPacketEncoder.class);

    private boolean compress;

    private MessageBodyCoder coder;

    public SimpleDataPacketEncoder(MessageBodyCoder coder, boolean compress) {
        super();
        this.compress = compress;
        this.coder = coder;
    }

    @Override
    public void encodeObject(Object msg, ByteBuf out) throws Exception {
        if (msg instanceof ByteBuf) {
            out.writeBytes((ByteBuf) msg);
            return;
        }
        if (msg instanceof byte[]) {
            out.writeBytes((byte[]) msg);
            return;
        }
        byte[] data;
        try {
            boolean response = Message.class.isInstance(msg);
            data = this.coder.doEncode(msg);
            boolean compress = this.compress && data.length > 1024;
            if (compress) {
                data = CompressUtils.compressBytes(data);
            }
            out.writeBytes(FRAME_MAGIC);
            out.writeByte((compress ? CoderContent.COMPRESS_OPTION : 0)
                    | (response ? CoderContent.RESPONSE_OPTION : 0));
            out.writeInt(data.length);
            out.writeBytes(data);
        } catch (Exception e) {
            LOGGER.error("编码 {} 类Message {} 异常", msg.getClass(), msg, e);
        }
    }
}
