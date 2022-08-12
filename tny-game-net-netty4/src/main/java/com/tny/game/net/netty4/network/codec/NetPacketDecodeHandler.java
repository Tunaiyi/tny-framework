package com.tny.game.net.netty4.network.codec;

import com.tny.game.common.runtime.*;
import com.tny.game.net.base.*;
import com.tny.game.net.message.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.*;

import java.util.List;

import static com.tny.game.net.base.NetLogger.*;

public class NetPacketDecodeHandler extends ByteToMessageDecoder implements NetPacketCodecErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CODER);

    private final NetPacketDecoder decoder;

    private final boolean closeOnError;

    private final NetPacketDecodeMarker marker = new NetPacketDecodeMarker();

    public NetPacketDecodeHandler(NetPacketDecoder decoder) {
        this(decoder, false);
    }

    public NetPacketDecodeHandler(NetPacketDecoder decoder, boolean closeOnError) {
        this.decoder = decoder;
        this.closeOnError = closeOnError;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try (ProcessTracer ignored = MESSAGE_DECODE_WATCHER.trace()) {
            try {
                while (in.readableBytes() > 0) {
                    Object messageObject = this.decoder.decodeObject(ctx, in, this.marker);
                    if (messageObject instanceof Message) {
                        out.add(messageObject);
                    } else {
                        break;
                    }
                }
            } catch (Throwable exception) {
                handleOnEncodeError(LOGGER, ctx, exception, closeOnError);
            }
        }

    }

}