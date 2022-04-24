package com.tny.game.net.command.dispatcher;

import com.tny.game.net.base.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 4:09 下午
 */
public class AutoIncrementIdGenerator implements NetIdGenerator {

    private static final int PROCESSORS_SIZE = Runtime.getRuntime().availableProcessors();

    private final AtomicLong[] idGenerators;

    private final int bitSize;

    public AutoIncrementIdGenerator() {
        this(PROCESSORS_SIZE);
    }

    public AutoIncrementIdGenerator(int concurrentLevel) {
        this.bitSize = Integer.bitCount(concurrentLevel);
        this.idGenerators = new AtomicLong[concurrentLevel];
        for (int i = 0; i < idGenerators.length; i++) {
            idGenerators[i] = new AtomicLong();
        }
    }

    @Override
    public long generate() {
        long id = Thread.currentThread().getId();
        int index = (int)(id % idGenerators.length);
        AtomicLong generator = idGenerators[index];
        return generator.incrementAndGet() << bitSize | index;
    }

}
