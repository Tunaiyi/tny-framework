package com.tny.game.net.netty4.network.codec;

import org.slf4j.*;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/13 12:05 下午
 */
public class MemoryAllotCounter {

    public static final Logger LOGGER = LoggerFactory.getLogger(MemoryAllotCounter.class);

    private int totalSize = 0;

    private int count = 0;

    private int deviationSize = 0;

    private int maxSize = 0;

    private int allotSize = -1;

    private int minSize = Integer.MAX_VALUE;

    private long time = System.currentTimeMillis();

    private final int initSize;

    private final long interval;

    public MemoryAllotCounter() {
        this(2048, TimeUnit.MINUTES.toMillis(10));
    }

    public MemoryAllotCounter(int initSize, long interval) {
        this.initSize = initSize;
        this.interval = interval;
    }

    public void recode(int useSize) {
        if (allotSize < 0) {
            return;
        }
        if (System.currentTimeMillis() > time) {
            this.reset();
        }
        this.totalSize += useSize;
        if (useSize > maxSize) {
            maxSize = useSize;
        }
        if (useSize < minSize) {
            minSize = useSize;
        }
        this.deviationSize += Math.max(useSize - allotSize, 0);
        this.count++;
        this.allotSize = -1;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    "MemoryAllotCounter 本次 分配 {} bytes 使用 {} bytes, 平均分配 {} bytes, 平均误差 {} bytes, 总分配 {} 次, 总分配 {} bytes, 最大 {} bytes, 最小 {} bytes",
                    allotSize, useSize, totalSize / count, deviationSize / count, count, totalSize, maxSize, minSize);
        }
    }

    public int allot() {
        if (count == 0) {
            return initSize;
        }
        return allotSize = Math.max((totalSize / count) * 2, initSize) + (this.deviationSize / count) * 2;
    }

    private void reset() {
        this.totalSize = 0;
        this.count = 0;
        this.maxSize = 0;
        this.minSize = Integer.MAX_VALUE;
        this.time = System.currentTimeMillis() + interval;
    }

}
