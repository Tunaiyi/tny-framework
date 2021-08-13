package com.tny.game.common.buff;

import java.nio.ByteBuffer;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/12 3:11 下午
 */
public interface ByteBufferAllocator {

	ByteBuffer alloc(int capacity);

	void release();

}
