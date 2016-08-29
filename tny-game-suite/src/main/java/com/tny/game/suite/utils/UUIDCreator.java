package com.tny.game.suite.utils;

import com.tny.game.LogUtils;
import com.tny.game.common.ExceptionUtils;
import org.joda.time.DateTime;

import java.util.concurrent.locks.StampedLock;

/**
 * Created by Kun Yang on 16/8/13.
 */
public class UUIDCreator {

    private final static long BASE_TIME = new DateTime(2016, 1, 1, 0, 0, 0, 0).getMillis();

    private final static long DEFAULT_WORKER_ID_BITS = 12;
    private final static long DEFAULT_SEQUENCE_BITS = 10;
    private final static long WORKER_SEQUENCE_BITS = DEFAULT_WORKER_ID_BITS + DEFAULT_SEQUENCE_BITS;

    // private long workerIDBits;
    // private long sequenceBits;
    // private long maxWorkerID;
    private long sequenceMask;
    private long workerIdShift;
    private long timestampShift;
    private long workerID = 0;
    private volatile long sequence = 0;
    private volatile long lastTimestamp = -1;
    private final StampedLock lock = new StampedLock();

    public static final long workerID(long id) {
        return workerID(id, DEFAULT_WORKER_ID_BITS, DEFAULT_SEQUENCE_BITS);
    }

    public static final long workerID(long id, long workIDBit) {
        return workerID(id, workIDBit, WORKER_SEQUENCE_BITS - workIDBit);
    }

    public static final long workerID(long id, long workIDBit, long sequenceBits) {
        return id >> sequenceBits & (~(-1L << workIDBit));
    }

    public UUIDCreator(long workerID) {
        this(workerID, DEFAULT_WORKER_ID_BITS, DEFAULT_SEQUENCE_BITS);
    }

    public UUIDCreator(long workerID, long workerIDBits) {
        this(workerID, workerIDBits, WORKER_SEQUENCE_BITS - workerIDBits);
    }

    public UUIDCreator(long workerID, long workerIDBits, long sequenceBits) {
        ExceptionUtils.checkArgument(workerIDBits + sequenceBits <= 22, "workerIDBits {} + sequenceBits {} > 22", workerIDBits, sequenceBits);
        long maxWorkerID = ~(-1L << workerIDBits);
        ExceptionUtils.checkArgument(workerID >= 0 && workerID <= maxWorkerID, "worker ID {} 不在 0 - {} 范围内", workerID, maxWorkerID);
        this.sequenceMask = ~(-1L << sequenceBits);
        this.workerIdShift = sequenceBits;
        this.timestampShift = sequenceBits + workerIDBits;
        this.workerID = workerID;
    }


    public long createID() {
        long lockStamp = lock.readLock();
        try {
            long timestamp;
            long seq = 0;
            while (true) {
                long lastTime = this.lastTimestamp;
                timestamp = timeGenerate();
                if (timestamp < lastTime)
                    throw new IllegalArgumentException(LogUtils.format("时间发生回滚, {} milliseconds", lastTime - timestamp));
                if (lastTime == timestamp) {
                    long writeStamp = lock.tryConvertToWriteLock(lockStamp);
                    if (writeStamp != 0L) {
                        lockStamp = writeStamp;
                        seq = this.sequence = (sequence + 1) & sequenceMask;
                        if (seq == 0)
                            timestamp = tilNextMillis(lastTime);
                        this.lastTimestamp = timestamp;
                        break;
                    } else {
                        lock.unlock(lockStamp);
                        lockStamp = lock.writeLock();
                    }
                } else {
                    long writeStamp = lock.tryConvertToWriteLock(lockStamp);
                    if (writeStamp != 0L) {
                        lockStamp = writeStamp;
                        sequence = 0L;
                        this.lastTimestamp = timestamp;
                        break;
                    } else {
                        lock.unlock(lockStamp);
                        lockStamp = lock.writeLock();
                    }
                }
            }
            return ((timestamp - BASE_TIME) << (int) timestampShift) | (workerID << (int) workerIdShift) | seq;
        } finally {
            lock.unlock(lockStamp);
        }

    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGenerate();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGenerate();
        }
        return timestamp;
    }

    private long timeGenerate() {
        return System.currentTimeMillis();
    }


}
