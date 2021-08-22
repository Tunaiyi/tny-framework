package com.tny.game.relay.netty4;

import com.tny.game.common.runtime.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.transport.*;
import com.tny.game.relay.*;
import com.tny.game.relay.packet.*;
import com.tny.game.relay.packet.arguments.*;
import com.tny.game.relay.transport.*;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.*;
import org.slf4j.*;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.base.NetLogger.*;

/**
 * 游戏请求处理器. 负责获取请求并将请求传给分发器
 *
 * @author KGTny
 */
@Sharable
public class NettyRelayPacketHandler extends ChannelDuplexHandler {

	protected static final Logger LOGGER = LoggerFactory.getLogger(NettyRelayPacketHandler.class);

	private final RelayPacketHandler relayPacketHandler;

	//	public NettyRelayPacketHandler() {
	//		this(new DefaultRelayPacketHandler());
	//	}

	public NettyRelayPacketHandler(RelayPacketHandler relayPacketHandler) {
		this.relayPacketHandler = relayPacketHandler;
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			Channel channel = ctx.channel();
			LOGGER.info("[RelayLink] 接受连接##通道 {} ==> {} 链接服务器", channel.remoteAddress(), channel.localAddress());
		}
		super.channelRegistered(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext context, Object object) {
		Channel channel = context.channel();
		if (object == null) {
			LOGGER.warn("[RelayLink] ##通道 {} ==> {} 消息为null", channel.remoteAddress(), channel.localAddress());
			channel.disconnect();
			return;
		}
		if (object instanceof RelayPacket) {
			RelayPacket<?> packet = as(object);
			try {
				NetRelayLink link = channel.attr(NettyRelayAttrKeys.RELAY_LINK).get();
				if (link == null) {
					release(packet);
					LOGGER.warn("[RelayLink] ##通道 {} ==> {} 时断开链接 # RelayLink 为空", channel.remoteAddress(), channel.localAddress());
					channel.disconnect();
					return;
				}
				link.receive(packet);
			} catch (Throwable ex) {
				release(packet);
				LOGGER.warn("[RelayLink] ##通道 {} ==> {} 接受转发包{}异常", channel.remoteAddress(), channel.localAddress(), packet, ex);
			}
		} else {
			LOGGER.error("处理器无法处理非 {} 的RelayPacket, message : {} ", RelayPacket.class, object);
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
				LOGGER.warn("[RelayLink] # {} # 客户端连接已断开 # {}", ClosedChannelException.class, cause.getMessage());
			} else if (cause instanceof IOException) {
				LOGGER.warn("[RelayLink] # {} # {}", IOException.class, cause.getMessage());
			} else if (cause instanceof WriteTimeoutException) {
				LOGGER.warn("[RelayLink] ##通道 {} ==> {} 断开链接 # cause {} 写出数据超时, message: {}",
						channel.remoteAddress(), channel.localAddress(), WriteTimeoutException.class, cause.getMessage());
			} else if (cause instanceof ReadTimeoutException) {
				LOGGER.warn("[RelayLink] ##通道 {} ==> {} 断开链接 # cause {} 读取数据超时, message: {}",
						channel.remoteAddress(), channel.localAddress(), ReadTimeoutException.class, cause.getMessage());
			} else {
				LOGGER.error(this.getClass().getName() + ".exceptionCaught() 截获异常", cause);
			}
		}
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		try (ProcessTracer ignored = MESSAGE_ENCODE_WATCHER.trace()) {
			if (msg instanceof RelayPacketMaker) {
				msg = ((RelayPacketMaker)msg).make();
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
				LOGGER.info("[RelayLink] 断开链接##通道 {} ==> {} 断开链接", channel.remoteAddress(), channel.localAddress());
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
					default:
						break;
				}
				LOGGER.info("[Pipe] {}##通道 {} ==> {} 开始断开链接", op, channel.remoteAddress(), channel.localAddress());
			}
			ctx.close();
		}
		super.userEventTriggered(ctx, evt);
	}

}
