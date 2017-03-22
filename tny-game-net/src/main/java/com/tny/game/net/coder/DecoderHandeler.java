package com.tny.game.net.coder;

import com.tny.game.net.base.NetLogger;
import com.tny.game.net.netty.NettyAttrKeys;
import com.tny.game.net.session.Session;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DecoderHandeler extends ByteToMessageDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    private DataPacketDecoder decoder;

    public DecoderHandeler(DataPacketDecoder decoder) {
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
            Session session = null;
            Channel channel = null;
            if (ctx != null) {
                channel = ctx.channel();
                session = channel.attr(NettyAttrKeys.SESSION).get();
            }
            LOG.error("#BaseCoder# IP {} 解码 {} 信息异常", channel, session == null ? "SOME ONE UNLOGION!" : session.getUID(), exception);
            if (exception instanceof PacketHeadException)
                channel.close();
        }
    }
}
