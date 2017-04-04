package com.tny.game.net.netty;

import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.message.NetMessage;
import com.tny.game.net.tunnel.Tunnel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.rmi.server.UID;
import java.util.Date;

/**
 * 游戏请求处理器. 负责获取请求并将请求传给分发器
 *
 * @author KGTny
 */
@Sharable
public class NettyMessageHandler extends SimpleChannelInboundHandler<NetMessage<?>> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.NET);


    protected AppConfiguration appConfiguration;

    public NettyMessageHandler(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (LOG.isInfoEnabled()) {
            Channel channel = ctx.channel();
            LOG.info("接受连接##通道 {} ==> {} 在 {} 时链接服务器", channel.remoteAddress(), channel.localAddress(), new Date());
            Tunnel<?> tunnel = new NettyTunnel<>(channel, appConfiguration.getSessionFactory());
            channel.attr(NettyAttrKeys.TUNNEL).set(tunnel);
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
    protected void channelRead0(ChannelHandlerContext context, NetMessage message) throws Exception {
        Channel channel = context.channel();
        if (message == null) {
            LOG.warn("读取的message为null 服务器主动断开 {} 连接", channel.localAddress());
            // channel.write(new SimpleResponse(CoreResponseCode.DECODE_ERROR));
            channel.disconnect();
            return;
        }
        try {
            Tunnel<UID> tunnel = channel.attr(NettyAttrKeys.TUNNEL).get();
            if (tunnel != null) {
                message.sendBy(tunnel);
                tunnel.receive(message);
            }
        } catch (Throwable ex) {
            LOG.error("#GameServerHandler#接受请求异常", ex);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Tunnel<?> tunnel = channel.attr(NettyAttrKeys.TUNNEL).get();
        if (LOG.isInfoEnabled())
            LOG.info("断开链接##通道 {} ==> {} 在 {} 时断开链接", channel.remoteAddress(), channel.localAddress(), new Date());
        if (tunnel != null)
            tunnel.close();
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
