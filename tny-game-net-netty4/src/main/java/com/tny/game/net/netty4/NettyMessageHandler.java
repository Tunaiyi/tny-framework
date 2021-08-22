package com.tny.game.net.netty4;

import com.tny.game.common.exception.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.result.*;
import com.tny.game.common.runtime.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.*;
import org.slf4j.*;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.base.NetLogger.*;
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

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			Channel channel = ctx.channel();
			LOGGER.info("[Tunnel] 接受连接##通道 {} ==> {} 链接服务器", channel.remoteAddress(), channel.localAddress());
		}
		super.channelRegistered(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (this == ctx.pipeline().last()) {
			Channel channel = ctx.channel();
			if (cause instanceof ClosedChannelException) {
				LOGGER.warn("[Tunnel] # {} # 客户端连接已断开 # {}", ClosedChannelException.class, cause.getMessage());
			} else if (cause instanceof IOException) {
				LOGGER.warn("[Tunnel] # {} # {}", IOException.class, cause.getMessage());
			} else if (cause instanceof WriteTimeoutException) {
				LOGGER.warn("[Tunnel] ##通道 {} ==> {} 断开链接 # cause {} 写出数据超时, message: {}",
						channel.remoteAddress(), channel.localAddress(), WriteTimeoutException.class, cause.getMessage());
			} else if (cause instanceof ReadTimeoutException) {
				LOGGER.warn("[Tunnel] ##通道 {} ==> {} 断开链接 # cause {} 读取数据超时, message: {}",
						channel.remoteAddress(), channel.localAddress(), ReadTimeoutException.class, cause.getMessage());
			} else if (cause instanceof ResultCodeException) {
				handleResultCodeException(channel, ((ResultCodeException)cause).getCode(), cause);
			} else if (cause instanceof ResultCodeRuntimeException) {
				handleResultCodeException(channel, ((ResultCodeRuntimeException)cause).getCode(), cause);
			} else {
				LOGGER.error(this.getClass().getName() + ".exceptionCaught() 截获异常", cause);
			}
		}
		super.exceptionCaught(ctx, cause);
	}

	private void handleResultCodeException(Channel channel, ResultCode code, Throwable cause) {
		LOGGER.error("[Tunnel] ##通道 {} ==> {} 断开链接 # cause {}({})[{}], message:{}",
				channel.remoteAddress(), channel.localAddress(), code, code.getCode(), code.getMessage(), cause.getMessage(), cause);
		if (code.getType() == ResultCodeType.ERROR) {
			NetTunnel<?> tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).getAndSet(null);
			if (tunnel != null) {
				TunnelAides.responseMessage(tunnel, MessageContexts.push(PUSH, code));
			}
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext context, Object object) {
		Channel channel = context.channel();
		if (object == null) {
			LOGGER.info("[Tunnel] {}##通道 {} ==> {} 断开链接", "消息为null", channel.remoteAddress(), channel.localAddress());
			channel.disconnect();
			return;
		}
		if (object instanceof NetMessage) {
			try {
				NetMessage message = as(object);
				traceDone(NET_TRACE_INPUT_READ_TO_TUNNEL_ATTR, message);
				trace(NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_ATTR, message);
				NetTunnel<?> tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).get();
				if (tunnel != null) {
					tunnel.receive(message);
				}
			} catch (Throwable ex) {
				LOGGER.error("#GameServerHandler#接受请求异常", ex);
			}
		}
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		try (ProcessTracer ignored = MESSAGE_ENCODE_WATCHER.trace()) {
			if (msg instanceof NettyMessageBearer) {
				msg = ((NettyMessageBearer)msg).message();
			}
			ctx.write(msg, promise);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		NetTunnel<?> tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).getAndSet(null);
		if (tunnel != null) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("[Tunnel] 断开链接##通道 {} ==> {} 断开链接", channel.remoteAddress(), channel.localAddress());
			}
			if (tunnel.getMode() == TunnelMode.SERVER) {
				tunnel.close();
			} else {
				tunnel.disconnect();
			}
		}
		super.channelInactive(ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent)evt;
			Channel channel = ctx.channel();
			Tunnel<?> tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).get();
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
				LOGGER.info("[Tunnel] {}##通道 {} ==> {} 开始断开链接", op, channel.remoteAddress(), channel.localAddress());
			}
			ctx.close();
		}
		super.userEventTriggered(ctx, evt);
	}

}
