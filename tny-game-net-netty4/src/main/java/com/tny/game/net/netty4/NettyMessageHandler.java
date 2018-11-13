package com.tny.game.net.netty4;

import com.tny.game.net.base.NetLogger;
import com.tny.game.net.command.MessageHandler;
import com.tny.game.net.message.NetMessage;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.*;
import org.slf4j.*;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.Date;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 游戏请求处理器. 负责获取请求并将请求传给分发器
 *
 * @author KGTny
 */
@Sharable
public class NettyMessageHandler extends ChannelDuplexHandler {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.NET);

    private MessageHandler messageHandler;

    public NettyMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (LOG.isInfoEnabled()) {
            Channel channel = ctx.channel();
            LOG.info("接受连接##通道 {} ==> {} 在 {} 时链接服务器", channel.remoteAddress(), channel.localAddress(), new Date());
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (this == ctx.pipeline().last()) {
            Channel channel = ctx.channel();
            if (cause instanceof ClosedChannelException) {
                LOG.warn(this.getClass().getName() + " # java.nio.channels.ClosedChannelException # 客户端连接已断开");
            } else if (cause instanceof IOException) {
                LOG.warn(this.getClass().getName() + " # java.io.IOException #" + cause.getMessage());
            } else if (cause instanceof WriteTimeoutException) {
                LOG.info("{}##通道 {} ==> {} 在 {} 时断开链接", "写出数据超时", channel.remoteAddress(), channel.localAddress(), new Date());
            } else if (cause instanceof ReadTimeoutException) {
                LOG.info("{}##通道 {} ==> {} 在 {} 时断开链接", "读取数据超时", channel.remoteAddress(), channel.localAddress(), new Date());
            } else {
                LOG.warn(this.getClass().getName() + ".exceptionCaught() 截获异常 : ", cause.getCause());
            }
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext context, Object object) {
        Channel channel = context.channel();
        if (object == null) {
            LOG.warn("读取的message为null 服务器主动断开 {} 连接", channel);
            channel.disconnect();
            return;
        }
        if (object instanceof NetMessage) {
            try {
                NetMessage<Object> message = as(object);
                NetTunnel<Object> tunnel = channel.attr(NettyAttrKeys.TUNNEL).get();
                if (tunnel != null)
                    messageHandler.handle(tunnel, message);
            } catch (Throwable ex) {
                LOG.error("#GameServerHandler#接受请求异常", ex);
            }
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        NettyTunnel<?> tunnel = channel.attr(NettyAttrKeys.TUNNEL).getAndSet(null);
        if (tunnel != null) {
            if (LOG.isInfoEnabled())
                LOG.info("断开链接##通道 {} ==> {} 在 {} 时断开链接", channel.remoteAddress(), channel.localAddress(), new Date());
            if (tunnel.getMode() == TunnelMode.SERVER)
                tunnel.close();
            else
                tunnel.disconnect();
        }
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            Channel channel = ctx.channel();
            Tunnel<?> tunnel = channel.attr(NettyAttrKeys.TUNNEL).get();
            if (tunnel != null) {
                String op = "空闲超时";
                switch (event.state()) {
                    case READER_IDLE:
                        op = "读空闲超时";
                        break;
                    case WRITER_IDLE:
                        op = "写空闲超时";
                        break;
                    default:
                        break;
                }
                LOG.info("{}##通道 {} ==> {} 在 {} 时开始断开链接", op, channel.remoteAddress(), channel.localAddress(), new Date());
            }
            ctx.close();
        }
        super.userEventTriggered(ctx, evt);
    }

}