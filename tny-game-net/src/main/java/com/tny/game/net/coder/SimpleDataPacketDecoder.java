package com.tny.game.net.coder;

import com.tny.game.log.NetLogger;
import com.tny.game.utils.CompressUtils;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDataPacketDecoder implements DataPacketDecoder {

    private static final Logger DECODER_LOG = LoggerFactory.getLogger(NetLogger.CODER);

    private MessageBodyCoder coder;

    public SimpleDataPacketDecoder(MessageBodyCoder coder) {
        super();
        this.coder = coder;
    }

    @Override
    public final Object decodeObject(final ByteBuf buffer) throws Exception {

        if (buffer.readableBytes() < CoderContent.FRAME_MAGIC.length + CoderContent.MESSAGE_LENGTH_SIZE + CoderContent.OPTION_SIZE)
            return null;
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

        // 读取请求信息体
        if (buffer.readableBytes() < messageBodySize) {
            buffer.readerIndex(buffer.readerIndex() - (CoderContent.FRAME_MAGIC.length + CoderContent.MESSAGE_LENGTH_SIZE + CoderContent.OPTION_SIZE));
            return null;
        }

        byte[] messageBytes = new byte[messageBodySize];
        if (DECODER_LOG.isDebugEnabled())
            DECODER_LOG.debug("read body");
        buffer.readBytes(messageBytes, 0, messageBodySize);

        if (DECODER_LOG.isDebugEnabled())
            DECODER_LOG.debug("do decoder");

        if ((option & CoderContent.COMPRESS_OPTION) > 0) {
            messageBytes = CompressUtils.decompressBytes(messageBytes);
        }
        return this.coder.doDecoder(messageBytes, (option & CoderContent.RESPONSE_OPTION) == 0);

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
