package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.message.common.*;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/11 4:23 下午
 */
public class ByteBufMessageBody implements OctetMessageBody {

	/**
	 * 消息体 buf
	 */
	private ByteBuf buffer;

	/**
	 * 是否是转发
	 */
	private final boolean relay;

	private final AtomicBoolean released = new AtomicBoolean(false);

	public ByteBufMessageBody(ByteBuf buffer, boolean relay) {
		this.buffer = buffer;
		this.relay = relay;
	}

	@Override
	protected void finalize() throws Throwable {
		this.release();
	}

	@Override
	public boolean isRelay() {
		return relay;
	}

	@Override
	public ByteBuf getBodyBytes() {
		return buffer;
	}

	@Override
	public void release() {
		if (released.compareAndSet(false, true)) {
			ByteBuf buffer = this.buffer;
			if (buffer != null) {
				this.buffer = null;
				ReferenceCountUtil.release(buffer);
			}
		}
	}

}
