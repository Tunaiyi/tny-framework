package com.tny.game.net.netty4;

import com.tny.game.net.base.NetLogger;
import com.tny.game.net.exception.CodecException;
import com.tny.game.net.netty4.codec.DataPacketEncoder;
import com.tny.game.net.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.*;

import java.nio.ByteBuffer;

public class EncodeHandler extends MessageToByteEncoder<Object> {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(NetLogger.CODER);

    private DataPacketEncoder encoder;

    public EncodeHandler(DataPacketEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof ByteBuf) {
            out.writeBytes((ByteBuf) msg);
            return;
        } else if (msg instanceof byte[]) {
            out.writeBytes((byte[]) msg);
            return;
        }
        if (msg instanceof ByteBuffer) {
            out.writeBytes((ByteBuffer) msg);
            return;
        }
        if (msg instanceof Message) {
            try {
                this.encoder.encodeObject(ctx, (Message<?>) msg, out);
            } catch (Throwable e) {
                LOG.error("#BaseEncode# 编码异常 ", e);
            }
            return;
        }
        throw CodecException.causeEncode("can not encode {}", msg.getClass());
    }

}