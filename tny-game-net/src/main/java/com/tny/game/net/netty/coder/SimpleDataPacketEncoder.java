package com.tny.game.net.netty.coder;

import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageMode;
import com.tny.game.net.message.coder.CoderContent;
import com.tny.game.net.message.coder.MessageCoder;
import com.tny.game.utils.CompressUtils;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tny.game.net.message.coder.CoderContent.*;

public class SimpleDataPacketEncoder implements DataPacketEncoder {

    public static final Logger LOGGER = LoggerFactory.getLogger(DataPacketEncoder.class);

    private boolean compress;

    private MessageCoder coder;

    public SimpleDataPacketEncoder(MessageCoder coder, boolean compress) {
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
            if (!(msg instanceof Message))
                return;
            out.writeBytes(FRAME_MAGIC);

            Message<?> message = (Message<?>) msg;

            if (message.getMode() == MessageMode.PING) {
                out.writeByte(CoderContent.PING_OPTION);
                out.writeInt(0); // BodySize
            } else if (message.getMode() == MessageMode.PONG) {
                out.writeByte(CoderContent.PONG_OPTION);
                out.writeInt(0); // BodySize
            } else {
                data = this.coder.encode((Message<?>) msg);
                boolean compress = this.compress && data.length > 1024;
                if (compress) {
                    data = CompressUtils.compressBytes(data);
                }
                out.writeByte(compress ? CoderContent.COMPRESS_OPTION : 0);
                out.writeInt(data.length);
                out.writeBytes(data);
            }
        } catch (Exception e) {
            LOGGER.error("编码 {} 类Message {} 异常", msg.getClass(), msg, e);
        }
    }
}
