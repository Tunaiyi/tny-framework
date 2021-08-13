package com.tny.game.net.netty4.codec;

import com.tny.game.net.message.common.*;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/11 4:23 下午
 */
public class ByteBufMessageBody implements MessageBytesBody {

	private ByteBuf buffer;

	public ByteBufMessageBody(ByteBuf buffer) {
		this.buffer = buffer;
	}

	@Override
	public ByteBuf getBodyBytes() {
		return buffer;
	}

	public void release() {
		ByteBuf buffer = this.buffer;
		this.buffer = null;
		ReferenceCountUtil.release(buffer);
	}

}
