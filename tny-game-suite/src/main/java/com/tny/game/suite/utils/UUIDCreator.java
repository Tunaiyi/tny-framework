package com.tny.game.suite.utils;

import com.tny.game.common.RunningChecker;
import static com.tny.game.common.utils.StringAide.*;
import com.tny.game.common.utils.Throws;
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

    public static final long parseWorkerID(long id) {
        return parseWorkerID(id, DEFAULT_WORKER_ID_BITS, DEFAULT_SEQUENCE_BITS);
    }

    public static final long parseWorkerID(long id, long workIDBit) {
        return parseWorkerID(id, workIDBit, WORKER_SEQUENCE_BITS - workIDBit);
    }

    public static final long parseWorkerID(long id, long workIDBit, long sequenceBits) {
        return id >> sequenceBits & (~(-1L << workIDBit));
    }

    public static final long parseTime(long id) {
        return BASE_TIME + (id >> WORKER_SEQUENCE_BITS);
    }

    public static final long parseTime(long id, long workIDBit, long sequenceBits) {
        return BASE_TIME + (id >> workIDBit + sequenceBits);
    }

    public UUIDCreator(long workerID) {
        this(workerID, DEFAULT_WORKER_ID_BITS, DEFAULT_SEQUENCE_BITS);
    }

    public UUIDCreator(long workerID, long workerIDBits) {
        this(workerID, workerIDBits, WORKER_SEQUENCE_BITS - workerIDBits);
    }

    public UUIDCreator(long workerID, long workerIDBits, long sequenceBits) {
        Throws.checkArgument(workerIDBits + sequenceBits <= 22, "workerIDBits {} + sequenceBits {} > 22", workerIDBits, sequenceBits);
        long maxWorkerID = ~(-1L << workerIDBits);
        Throws.checkArgument(workerID >= 0 && workerID <= maxWorkerID, "worker ID {} 不在 0 - {} 范围内", workerID, maxWorkerID);
        this.sequenceMask = ~(-1L << sequenceBits);
        this.workerIdShift = sequenceBits;
        this.timestampShift = sequenceBits + workerIDBits;
        this.workerID = workerID;
    }


    public long createID() {
        long lockStamp = 0;
        try {
            long timestamp;
            long seq = 0;
            lockStamp = lock.readLock();
            while (true) {
                long lastTime = this.lastTimestamp;
                timestamp = timeGenerate();
                if (timestamp < lastTime)
                    throw new IllegalArgumentException(format("时间发生回滚, {} milliseconds", lastTime - timestamp));
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
                        lock.unlockRead(lockStamp);
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
                        lock.unlockRead(lockStamp);
                        lockStamp = lock.writeLock();
                    }
                }
            }
            return ((timestamp - BASE_TIME) << (int) timestampShift) | (workerID << (int) workerIdShift) | seq;
        } finally {
            if (lockStamp != 0)
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


    public static void main(String[] args) {
        System.out.println(Math.pow(2, 13));
        UUIDCreator creator = new UUIDCreator(1, 13);
        RunningChecker.startPrint(UUIDCreator.class);
        for (int i = 0; i < 1000000; i++) {
            creator.createID();
            // ForkJoinPool.commonPool()
            //         .submit(() -> creator.createID());
        }
        long cost = RunningChecker.end(UUIDCreator.class).cost();
        System.out.println(cost);
        // System.out.println(creator.createID());
        // System.out.println(System.currentTimeMillis());
        // System.out.println(Long.MAX_VALUE);
        // UUIDCreator[] creators = new UUIDCreator[]{
        //         // new UUIDCreator(0, 3),
        //         // new UUIDCreator(1, 3),
        //         // new UUIDCreator(2, 3),
        //         // new UUIDCreator(3, 3),
        //         // new UUIDCreator(4, 3),
        //         // new UUIDCreator(5, 3),
        //         // new UUIDCreator(6, 3),
        //         // new UUIDCreator(7, 3),
        // };
        // RunningChecker.startPrint(UUIDCreator.class);
        // for (int i = 0; i < 1000000; i++) {
        //     int index = i;
        //     ForkJoinPool.commonPool()
        //             .submit(() -> creators[index % creators.length].createID());
        // }
        // long cost = RunningChecker.end(UUIDCreator.class).cost();
        // System.out.println(cost);
        // size.forEach(System.out::println);
    }

}
