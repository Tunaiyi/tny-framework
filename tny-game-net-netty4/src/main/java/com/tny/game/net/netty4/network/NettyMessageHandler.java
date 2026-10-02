/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.netty4.network;

import com.tny.game.common.exception.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.result.*;
import com.tny.game.common.runtime.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.network.codec.NetPacketEncodeException;
import com.tny.game.net.rpc.*;
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
import static com.tny.game.net.message.Protocols.*;

/**
 * 游戏请求处理器. 负责获取请求并将请求传给分发器
 *
 * @author KGTny
 */
@Sharable
@Unit
@UnitInterface
public class NettyMessageHandler extends ChannelDuplexHandler {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NettyMessageHandler.class);

    protected final RpcMonitor rpcMonitor;

    private final NetBootstrapSetting bootstrapSetting;

    public NettyMessageHandler(NetworkContext networkContext) {
        this.rpcMonitor = networkContext.getRpcMonitor();
        this.bootstrapSetting = networkContext.getSetting();
    }

    @Override
    public void channelActive(@Nonnull ChannelHandlerContext ctx) throws Exception {
        if (LOGGER.isInfoEnabled()) {
            Channel channel = ctx.channel();
            if (channel.isActive()) {
                LOGGER.info("[Tunnel] 连接成功 ## 通道 {} ==> {}", channel.remoteAddress(), channel.localAddress());
            } else {
                LOGGER.info("[Tunnel] 连接失败 ## 通道 {} ==> {}", channel.remoteAddress(), channel.localAddress());
            }
        }
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof NetPacketEncodeException) {
            // 编码方向失败仅影响本次写回执（netty 已 tryFailure）——不透传管道，保持通道健康（D2）
            LOGGER.warn("[Tunnel] encode rejected, write future failed, channel kept: {}", cause.getMessage());
            return;
        }
        if (this == ctx.pipeline().last()) {
            Channel channel = ctx.channel();
            if (cause instanceof ClosedChannelException) {
                LOGGER.warn("[Tunnel] # {} # 客户端连接已断开 # {}", ClosedChannelException.class, cause.getMessage());
            } else if (cause instanceof IOException) {
                LOGGER.warn("[Tunnel] # {} # {}", IOException.class, cause.getMessage(), cause);
            } else if (cause instanceof WriteTimeoutException) {
                LOGGER.warn("[Tunnel]  ## 通道 {} ==> {} 断开链接 # cause {} 写出数据超时, message: {}", channel.remoteAddress(),
                        channel.localAddress(), WriteTimeoutException.class, cause.getMessage());
            } else if (cause instanceof ReadTimeoutException) {
                LOGGER.warn("[Tunnel]  ## 通道 {} ==> {} 断开链接 # cause {} 读取数据超时, message: {}", channel.remoteAddress(),
                        channel.localAddress(), ReadTimeoutException.class, cause.getMessage());
            } else if (cause instanceof ResultCodableException) {
                handleResultCodeException(channel, ((ResultCodableException) cause).getCode(), cause);
            }
        } else {
            LOGGER.error(this.getClass().getName() + ".exceptionCaught() 截获异常", cause);
        }
        super.exceptionCaught(ctx, cause);
    }

    private void handleResultCodeException(Channel channel, ResultCode code, Throwable cause) {
        if (code.getLevel() == ResultLevel.ERROR) {
            LOGGER.error("[Tunnel]  ## 通道 {} ==> {} 断开链接 # cause {}({})[{}], message:{}", channel.remoteAddress(), channel.localAddress(), code,
                    code.getCode(), code.getMessage(), cause.getMessage(), cause);
            NetTunnel tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).get();
            if (tunnel != null) {
                RpcMessageAide.send(tunnel, MessageContents.push(PUSH, code), true);
            }
        } else {
            LOGGER.error("[Tunnel]  ## 通道 {} ==> {} 异常 # cause {}({})[{}], message:{}", channel.remoteAddress(), channel.localAddress(), code,
                    code.getCode(), code.getMessage(), cause.getMessage(), cause);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext context, @Nonnull Object object) {
        Channel channel = context.channel();
        if (object instanceof NetMessage) {
            try {
                NetMessage message = as(object);
                // 心跳为跨连接共享单例：忽略头清理不得触碰其可变面（net-protocol"心跳不产生共享状态变更"）
                if (message.getMode() != MessageMode.PING && message.getMode() != MessageMode.PONG) {
                    RpcMessageAide.ignoreHeaders(message, bootstrapSetting.getReadIgnoreHeaders());
                }
                NetTunnel tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).get();
                if (tunnel != null) {
                    tunnel.receive(message);
                } else {
                    // 未就绪丢弃必须可观察（net-tunnel 规格）；rpcMonitor 埋点属观测主题后续项
                    LOGGER.warn("[Tunnel] 通道 {} ==> {} 尚未绑定会话，丢弃消息 id {} protocol {} mode {}",
                            channel.remoteAddress(), channel.localAddress(),
                            message.getId(), message.getProtocolId(), message.getMode());
                }
            } catch (Throwable ex) {
                LOGGER.error("#GameServerHandler#接受请求异常", ex);
            }
        }
    }

    @Override
    public void write(ChannelHandlerContext context, Object msg, ChannelPromise promise) {
        try (ProcessTracer ignored = MESSAGE_ENCODE_WATCHER.trace()) {
            if (msg instanceof NetMessage message) {
                RpcMessageAide.ignoreHeaders(message, bootstrapSetting.getWriteIgnoreHeaders());
                Channel channel = context.channel();
                NetTunnel tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).get();
                rpcMonitor.onSend(tunnel, message);

            }
            context.write(msg, promise);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        NetTunnel tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).getAndSet(null);
        if (tunnel != null) {
            LOGGER.info("[Tunnel] 断开链接 ## 通道 {} 断开链接", channel);
            if (tunnel.getAccessMode() == NetAccessMode.SERVER) {
                tunnel.close();
            } else {
                tunnel.disconnect();
            }
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
                        break;
                    default:
                        break;
                }
                LOGGER.info("[Tunnel] {} ## 通道 {} ==> {} 开始断开链接", op, channel.remoteAddress(), channel.localAddress());
            }
            ctx.close();
        }
        super.userEventTriggered(ctx, evt);
    }

}
