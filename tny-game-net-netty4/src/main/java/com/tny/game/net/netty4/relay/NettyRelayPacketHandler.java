/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.netty4.relay;

import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;
import com.tny.game.common.runtime.*;
import com.tny.game.net.application.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.*;
import org.slf4j.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.application.NetLogger.*;

/**
 * 游戏请求处理器. 负责获取请求并将请求传给分发器
 * 客户端 = > [NettyRelayMessageHandler] 转发服务器 [NettyRelayPacketHandler] => [NettyRelayPacketHandler] 业务服务器
 *
 * @author KGTny
 */
@Sharable
public class NettyRelayPacketHandler extends ChannelDuplexHandler {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NettyRelayPacketHandler.class);

    private final RelayPacketProcessor relayPacketProcessor;

    private final RelayMonitor relayMonitor;

    private final NetBootstrapSetting bootstrapSetting;

    public NettyRelayPacketHandler(NetBootstrapSetting bootstrapSetting, RelayPacketProcessor relayPacketProcessor) {
        this.relayPacketProcessor = relayPacketProcessor;
        this.relayMonitor = new RelayMonitor();
        this.bootstrapSetting = bootstrapSetting;
    }

    @Override
    public void channelActive(@Nonnull ChannelHandlerContext ctx) throws Exception {
        if (LOGGER.isInfoEnabled()) {
            Channel channel = ctx.channel();
            if (channel.isActive()) {
                LOGGER.info("[RelayLink] 接受连接 ## 通道 {} ==> {} 链接服务器", channel.localAddress(), channel.remoteAddress());
                super.channelRegistered(ctx);
            } else {
                LOGGER.info("[RelayLink] 无效连接 ## 通道 {} ==> {}", channel.localAddress(), channel.remoteAddress());
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext context, @Nonnull Object object) {
        Channel channel = context.channel();
        if (object instanceof RelayPacket) {
            RelayPacket<?> packet = as(object);
            try {
                RelayPacketType packetType = packet.getType();
                if (packetType == RelayPacketType.LINK_OPEN) { // transporter 处理
                    RelayTransporter transporter = channel.attr(NettyRelayAttrKeys.RELAY_TRANSPORTER).get();
                    var openPacket = (LinkOpenPacket) packet;
                    this.relayMonitor.onLinkOpen(transporter, openPacket);
                    this.relayPacketProcessor.onLinkOpen(transporter, openPacket);
                } else {
                    if (packetType.isHandleByLink()) { // link 处理
                        NetRelayLink link = channel.attr(NettyRelayAttrKeys.RELAY_LINK).get();
                        this.relayMonitor.onReadPacket(link, packet);
                        packetType.handle(this.relayPacketProcessor, link, packet);
                    }
                }
            } catch (ResultCodeRuntimeException ex) {
                if (ex.getCode().getLevel() == ResultLevel.ERROR) {
                    channel.close();
                    LOGGER.warn("[RelayLink] 读取消息 ## 通道 {} ==> {} 时断开链接 # RelayLink 为空", channel.localAddress(), channel.remoteAddress(),
                            ex);
                } else {
                    LOGGER.warn("[RelayLink] 读取消息 ## 通道 {} ==> {} 接受转发包{}异常", channel.localAddress(), channel.remoteAddress(), packet,
                            ex);
                }
            } catch (Throwable ex) {
                release(packet);
                LOGGER.warn("[RelayLink] 读取消息 ## 通道 {} ==> {} 接受转发包{}异常", channel.localAddress(), channel.remoteAddress(), packet, ex);
            }
        } else {
            LOGGER.error("处理器无法处理非 {} 的RelayPacket, message : {} ", RelayPacket.class, object);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try (ProcessTracer ignored = MESSAGE_ENCODE_WATCHER.trace()) {
            RelayPacket<?> packet = null;
            if (msg instanceof RelayPacketMaker) {
                packet = ((RelayPacketMaker) msg).make();
            }
            if (msg instanceof RelayPacket) {
                packet = (RelayPacket<?>) msg;
            }
            if (packet != null) {
                var channel = ctx.channel();
                NetRelayLink link = channel.attr(NettyRelayAttrKeys.RELAY_LINK).get();
                if (link != null) {
                    relayMonitor.onWritePacket(link, packet);
                }
                ctx.write(packet, promise);
            }
        }
    }

    private void release(RelayPacket<?> packet) {
        if (packet == null) {
            return;
        }
        RelayPacketArguments arguments = packet.getArguments();
        if (arguments != null) {
            arguments.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (this == ctx.pipeline().last()) {
            Channel channel = ctx.channel();
            if (cause instanceof ClosedChannelException) {
                LOGGER.warn("[RelayLink] 发生异常 # {} # 客户端连接已断开 # {}", ClosedChannelException.class, cause.getMessage());
            } else if (cause instanceof IOException) {
                LOGGER.warn("[RelayLink] 发生异常 # {} # {}", IOException.class, cause.getMessage());
            } else if (cause instanceof WriteTimeoutException) {
                LOGGER.warn("[RelayLink] 发生异常 ## 通道 {} ==> {} 断开链接 # cause {} 写出数据超时, message: {}", channel.localAddress(),
                        channel.remoteAddress(), WriteTimeoutException.class, cause.getMessage());
            } else if (cause instanceof ReadTimeoutException) {
                LOGGER.warn("[RelayLink] 发生异常 ## 通道 {} ==> {} 断开链接 # cause {} 读取数据超时, message: {}", channel.localAddress(),
                        channel.remoteAddress(), ReadTimeoutException.class, cause.getMessage());
            } else {
                LOGGER.error(this.getClass().getName() + ".exceptionCaught() 截获异常", cause);
            }
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        NetRelayLink link = channel.attr(NettyRelayAttrKeys.RELAY_LINK).get();
        if (link != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("[RelayLink] 断开链接 ## 通道 {} ==> {} 断开链接", channel.localAddress(), channel.remoteAddress());
            }
            link.close();
        }
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            Channel channel = ctx.channel();
            Tunnel tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).get();
            if (tunnel != null) {
                String op = "空闲超时";
                switch (event.state()) {
                    case READER_IDLE:
                        op = "读空闲超时";
                        break;
                    case WRITER_IDLE:
                        op = "写空闲超时";
                    default:
                        break;
                }
                LOGGER.info("[Pipe] {}##通道 {} ==> {} 开始断开链接", op, channel.localAddress(), channel.remoteAddress());
            }
            ctx.close();
        }
        super.userEventTriggered(ctx, evt);
    }

}
