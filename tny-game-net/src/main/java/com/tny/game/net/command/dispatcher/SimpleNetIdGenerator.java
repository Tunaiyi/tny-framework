package com.tny.game.net.command.dispatcher;

import com.tny.game.net.base.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 4:09 下午
 */
public class SimpleNetIdGenerator implements NetIdGenerator {

	private final AtomicLong idGenerator = new AtomicLong();

	@Override
	public long generate() {
		return idGenerator.incrementAndGet();
	}

}
