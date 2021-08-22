package com.tny.game.relay.netty4;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.result.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.transport.*;
import com.tny.game.relay.*;
import com.tny.game.relay.message.*;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 处理
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/16 5:35 下午
 */
@Unit
@Sharable
public class NettyRelayMessageHandler extends NettyMessageHandler {

	protected static final Logger LOG = LoggerFactory.getLogger(NettyMessageHandler.class);

	private final NetRelayService<Object> relayService;

	private final RelayRole relayTo;

	public NettyRelayMessageHandler(NetRelayService<?> relayService) {
		this(relayService, RelayRole.PROVIDER);
	}

	public NettyRelayMessageHandler(NetRelayService<?> relayService, RelayRole relayTo) {
		this.relayService = as(relayService);
		this.relayTo = relayTo;
	}

	@Override
	public void channelRead(ChannelHandlerContext context, Object object) {
		if (object instanceof NetMessage) {
			RelayMessage message = createRelayMessage((NetMessage)object);
			if (message.isRelay()) {
				relayMessage(context, message);
			}
		}
		super.channelRead(context, object);
	}

	private RelayMessage createRelayMessage(NetMessage message) {
		return new RelayMessage(message);
	}

	private void relayMessage(ChannelHandlerContext context, RelayMessage message) {
		Channel channel = context.channel();
		NetTunnel<?> tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).get();
		// 没有获取到 repeater 关闭接口销毁消息
		ResultCode code = relayService.relay(tunnel, message);
		if (code.isSuccess()) {
			return;
		}
		try {
			throw new NetGeneralException(code, "repeater error");
		} finally {
			message.release();
		}
	}

	@Override
	public void write(ChannelHandlerContext context, Object object, ChannelPromise promise) throws Exception {
		super.write(context, object, promise);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		NetTunnel<?> tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).get();
		if (tunnel != null) {
			relayService.unload(tunnel);
		}
		super.channelInactive(ctx);
	}

}
