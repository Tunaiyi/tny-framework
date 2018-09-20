package com.tny.game.net.netty.coder;

import com.tny.game.net.base.NetLogger;
import com.tny.game.net.transport.message.DetectMessage;
import com.tny.game.net.transport.message.coder.*;
import io.netty.buffer.ByteBuf;
import org.slf4j.*;

public class SimpleDataPacketDecoder implements DataPacketDecoder {

    private static final Logger DECODER_LOG = LoggerFactory.getLogger(NetLogger.CODER);

    private MessageCoder coder;

    public SimpleDataPacketDecoder(MessageCoder coder) {
        super();
        this.coder = coder;
    }

    @Override
    public final Object decodeObject(final ByteBuf buffer) throws Exception {
        if (buffer.readableBytes() < CoderContent.FRAME_MAGIC.length + CoderContent.OPTION_SIZE + CoderContent.MESSAGE_LENGTH_SIZE)
            return null;
        buffer.markReaderIndex();
        // 检验消息头
        if (DECODER_LOG.isDebugEnabled())
            DECODER_LOG.debug("read head");
        final byte[] magics = new byte[CoderContent.FRAME_MAGIC.length];
        buffer.readBytes(magics);
        if (!this.isMagic(magics))
            throw new PacketHeadException("非法包头");

        if (DECODER_LOG.isDebugEnabled())
            DECODER_LOG.debug("read option");
        final byte option = buffer.readByte();

        if (DECODER_LOG.isDebugEnabled())
            DECODER_LOG.debug("read body size");
        final int messageBodySize = buffer.readInt();

        if ((option & CoderContent.PING_OPTION) > 0) {
            return DetectMessage.ping();
        }

        if ((option & CoderContent.PONG_OPTION) > 0) {
            return DetectMessage.pong();
        }

        // 读取请求信息体
        if (buffer.readableBytes() < messageBodySize) {
            buffer.resetReaderIndex();
            return null;
        }

        byte[] messageBytes = new byte[messageBodySize];
        if (DECODER_LOG.isDebugEnabled())
            DECODER_LOG.debug("read body");
        buffer.readBytes(messageBytes, 0, messageBodySize);

        if (DECODER_LOG.isDebugEnabled())
            DECODER_LOG.debug("do decoder");

        // if ((option & CoderContent.COMPRESS_OPTION) > 0) {
        //     messageBytes = CompressUtils.decompressBytes(messageBytes);
        // }
        return this.coder.decode(messageBytes);
    }

    /**
     * 是否是文件头
     *
     * @param magics
     * @return
     */
    private boolean isMagic(final byte[] magics) {
        for (int index = 0; index < magics.length; index++) {
            if (CoderContent.FRAME_MAGIC[index] != magics[index])
                return false;
        }
        return true;
    }

}
