package com.tny.game.net.netty4.codec;

import com.tny.game.common.buff.*;
import io.netty.buffer.*;
import io.netty.util.ReferenceCountUtil;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/13 11:26 上午
 */
public class NettyByteBufferAllocator implements ByteBufferAllocator {

	private final ByteBufAllocator allocator;

	private final List<ByteBuf> byteBufList = new ArrayList<>();

	public NettyByteBufferAllocator(ByteBufAllocator allocator) {
		this.allocator = allocator;
	}

	@Override
	public ByteBuffer alloc(int capacity) {
		ByteBuf byteBuf = allocator.heapBuffer(capacity);
		byteBufList.add(byteBuf);
		return byteBuf.nioBuffer(0, capacity);
	}

	@Override
	public void release() {
		for (ByteBuf byteBuf : byteBufList) {
			ReferenceCountUtil.release(byteBuf);
		}
		byteBufList.clear();
	}

}
