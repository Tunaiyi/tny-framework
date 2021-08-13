package com.tny.game.common.buff;

import org.slf4j.*;

import java.nio.ByteBuffer;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/12 3:18 下午
 */
public class NioByteBufferAllocator implements ByteBufferAllocator {

	public static final Logger LOGGER = LoggerFactory.getLogger(NioByteBufferAllocator.class);

	private int size = 0;

	private int count = 0;

	@Override
	public ByteBuffer alloc(int capacity) {
		size += capacity;
		count++;
		return ByteBuffer.allocate(capacity);
	}

	@Override
	public void release() {
		LOGGER.warn("NioByteBufferAllocator release {} count, total {} size", this.count, this.size);
	}

}
