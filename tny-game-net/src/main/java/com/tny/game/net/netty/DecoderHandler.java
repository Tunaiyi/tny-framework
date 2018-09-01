package com.tny.game.net.netty;

import com.tny.game.net.base.NetLogger;
import com.tny.game.net.netty.coder.*;
import com.tny.game.net.tunnel.Tunnel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.*;

import java.util.List;

public class DecoderHandler extends ByteToMessageDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    private DataPacketDecoder decoder;

    public DecoderHandler(DataPacketDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            Object messageObject = this.decoder.decodeObject(in);
            if (messageObject != null) {
                out.add(messageObject);
            }
        } catch (Exception exception) {
            Tunnel tunnel = null;
            Channel channel = null;
            if (ctx != null) {
                channel = ctx.channel();
                tunnel = channel.attr(NettyAttrKeys.TUNNEL).get();
            }
            LOG.error("#BaseCoder# IP {} 解码 {} 信息异常", channel, tunnel == null ? "SOME ONE UNLOGION!" : tunnel.getUid(), exception);
            if (exception instanceof PacketHeadException) {
                if (channel != null)
                    channel.close();
            }
        }
    }
}
