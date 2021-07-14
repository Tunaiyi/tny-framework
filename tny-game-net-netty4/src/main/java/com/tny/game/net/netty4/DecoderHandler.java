package com.tny.game.net.netty4;

import com.tny.game.common.context.*;
import com.tny.game.common.result.*;
import com.tny.game.common.runtime.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.codec.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.*;

import java.util.List;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.base.NetLogger.*;

public class DecoderHandler extends ByteToMessageDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    private final DataPacketDecoder decoder;

    private final DataPacketMarker marker = new DataPacketMarker();

    public DecoderHandler(DataPacketDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            try (ProcessTracer ignored = MESSAGE_DECODE_WATCHER.trace()) {
                while (in.readableBytes() > 0) {
                    ProcessTracer allTrace = NET_TRACE_ALL_WATCHER.trace();
                    ProcessTracer handleTrace = NET_TRACE_INPUT_ALL.trace();
                    ProcessTracer toTunnelTrace = NET_TRACE_INPUT_READ_TO_TUNNEL_WATCHER.trace();
                    Object messageObject = this.decoder.decodeObject(ctx, in, this.marker);
                    if (messageObject instanceof Message) {
                        Message message = as(messageObject);
                        if (message.getType() == MessageType.MESSAGE) {
                            Attributes attributes = (message).attributes();
                            attributes.setAttribute(NET_TRACE_ALL_ATTR_KEY, allTrace);
                            attributes.setAttribute(NET_TRACE_INPUT_ALL_ATTR_KEY, handleTrace);
                            attributes.setAttribute(NET_TRACE_INPUT_READ_TO_TUNNEL_ATTR_KEY, toTunnelTrace);
                        }
                        out.add(messageObject);
                    } else {
                        break;
                    }
                }
            }
        } catch (Exception exception) {
            Tunnel<?> tunnel = null;
            Channel channel = null;
            if (ctx != null) {
                channel = ctx.channel();
                tunnel = channel.attr(NettyAttrKeys.TUNNEL).get();
            }
            LOG.error("#BaseCoder# IP {} 解码 {} 信息异常", channel, tunnel == null ? "SOME ONE UNLOGION!" : tunnel.getUserId(), exception);
            if (channel != null && exception instanceof CommandException) {
                CommandException codecException = as(exception);
                if (codecException.getResultCode().getType() == ResultCodeType.ERROR) {
                    channel.close();
                }
            }
        }
    }

}
