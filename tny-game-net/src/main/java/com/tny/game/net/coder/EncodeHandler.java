package com.tny.game.net.coder;

import com.tny.game.log.CoreLogger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class EncodeHandler extends MessageToByteEncoder<Object> {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(CoreLogger.CODER);

    private DataPacketEncoder encoder;

    public EncodeHandler(DataPacketEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof ByteBuf)
            out.writeBytes((ByteBuf) msg);
        else if (msg instanceof byte[])
            out.writeBytes((byte[]) msg);
        else if (msg instanceof ByteBuffer)
            out.writeBytes((ByteBuffer) msg);
        else {
            try {
                this.encoder.encodeObject(msg, out);
            } catch (Throwable e) {
                LOG.error("#BaseEncode# 编码异常 ", e);
            }
        }
    }

}
