/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.common.utils;

import org.slf4j.*;

import java.time.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;

import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 16/8/13.
 */
public class SnowflakeIdCreator implements IdCreator {

    public static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeIdCreator.class);

    private final static long BASE_TIME = ZonedDateTime.of(2016, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
            .toInstant().toEpochMilli();

    private final static long DEFAULT_WORKER_ID_BITS = 12;

    private final static long DEFAULT_SEQUENCE_BITS = 10;

    private final static long WORKER_SEQUENCE_BITS = DEFAULT_WORKER_ID_BITS + DEFAULT_SEQUENCE_BITS;

    // private long workerIDBits;
    // private long sequenceBits;
    // private long maxWorkerID;
    private final long sequenceMask;

    private final long workerIdShift;

    private final long timestampShift;

    private long workerID = 0;

    private long sequence = 0;

    private volatile long lastTimestamp = -1;

    /** 小幅回拨容忍阈值 */
    private static final long MAX_BACKWARD_MILLIS = 200L;

    private final StampedLock lock = new StampedLock();

    /** 时钟源注入缝（测试可控回拨；公共构造保持真实时钟） */
    private final Supplier<Long> timeSource;

    /** 身份位占用登记：同 (位宽配置, workerID) 双实例同毫秒必撞号，构造期显式冲突 */
    private static final ConcurrentHashMap<String, SnowflakeIdCreator> RESERVED = new ConcurrentHashMap<>();

    public static long parseWorkerId(long id) {
        return parseWorkerId(id, DEFAULT_WORKER_ID_BITS, DEFAULT_SEQUENCE_BITS);
    }

    public static long parseWorkerId(long id, long workIDBit) {
        return parseWorkerId(id, workIDBit, WORKER_SEQUENCE_BITS - workIDBit);
    }

    public static long parseWorkerId(long id, long workIDBit, long sequenceBits) {
        return id >> sequenceBits & (~(-1L << workIDBit));
    }

    public static long parseTime(long id) {
        return BASE_TIME + (id >> WORKER_SEQUENCE_BITS);
    }

    public static long parseTime(long id, long workIDBit, long sequenceBits) {
        return BASE_TIME + (id >> workIDBit + sequenceBits);
    }

    public SnowflakeIdCreator(long workerID) {
        this(workerID, DEFAULT_WORKER_ID_BITS, DEFAULT_SEQUENCE_BITS);
    }

    public SnowflakeIdCreator(long workerID, long workerIDBits) {
        this(workerID, workerIDBits, WORKER_SEQUENCE_BITS - workerIDBits);
    }

    public SnowflakeIdCreator(long workerID, long workerIDBits, long sequenceBits) {
        this(workerID, workerIDBits, sequenceBits, System::currentTimeMillis);
    }

    /**
     * 包内注入缝：可控时钟源构造（测试回拨场景），公共签名不变。
     */
    SnowflakeIdCreator(long workerID, long workerIDBits, long sequenceBits, Supplier<Long> timeSource) {
        Asserts.checkArgument(workerIDBits + sequenceBits <= 22, "workerIDBits {} + sequenceBits {} > 22", workerIDBits, sequenceBits);
        long maxWorkerID = ~(-1L << workerIDBits);
        Asserts.checkArgument(workerID >= 0 && workerID <= maxWorkerID, "worker ID {} 不在 0 - {} 范围内", workerID, maxWorkerID);
        this.sequenceMask = ~(-1L << sequenceBits);
        this.workerIdShift = sequenceBits;
        this.timestampShift = sequenceBits + workerIDBits;
        this.workerID = workerID;
        this.timeSource = timeSource;
        String identity = workerIDBits + ":" + sequenceBits + ":" + workerID;
        if (RESERVED.putIfAbsent(identity, this) != null) {
            throw new IllegalStateException("workerID " + workerID + "（位宽 " + workerIDBits + "/" + sequenceBits
                                            + "）已被另一实例占用——同毫秒双实例必撞号，显式拒绝");
        }
    }

    @Override
    public long createId() {
        long lockStamp = 0;
        try {
            long timestamp;
            long seq = 0;
            lockStamp = this.lock.readLock();
            while (true) {
                long lastTime = this.lastTimestamp;
                timestamp = timeGenerate();
                long delay = timestamp - lastTime;
                if (delay < 0) {
                    // 回拨处置方向修正（原"小幅抛、大幅持锁长眠且醒后不重取时间"）：
                    // ≤阈值：释放锁自旋等待追平（不阻塞其他读者、醒后重读时间）；超阈值：显式失败不产号
                    if (lastTime - timestamp > MAX_BACKWARD_MILLIS) {
                        throw new IllegalStateException(format("时钟大幅回拨 {} milliseconds，拒绝产号", lastTime - timestamp));
                    }
                    // 按当前持有票种通用解锁：进入本分支时票种可能是读票（常规）或写票
                    // （上一轮"降读→排队取写"成功后重入），unlockRead 对写票必抛
                    // IllegalMonitorStateException 连坐取号线程
                    this.lock.unlock(lockStamp);
                    lockStamp = 0;
                    while (timeGenerate() < lastTime) {
                        Thread.yield();
                    }
                    lockStamp = this.lock.readLock();
                    continue;
                }
                if (lastTime == timestamp) {
                    long writeStamp = this.lock.tryConvertToWriteLock(lockStamp);
                    if (writeStamp != 0L) {
                        lockStamp = writeStamp;
                        long currentSequence = ++this.sequence;
                        seq = currentSequence & this.sequenceMask;
                        if (seq == 0) {
                            timestamp = tilNextMillis(lastTime);
                        }
                        this.lastTimestamp = timestamp;
                        break;
                    } else {
                        this.lock.unlockRead(lockStamp);
                        lockStamp = this.lock.writeLock();
                    }
                } else {
                    long writeStamp = this.lock.tryConvertToWriteLock(lockStamp);
                    if (writeStamp != 0L) {
                        lockStamp = writeStamp;
                        this.sequence = 0L;
                        this.lastTimestamp = timestamp;
                        break;
                    } else {
                        this.lock.unlockRead(lockStamp);
                        lockStamp = this.lock.writeLock();
                    }
                }
            }
            return ((timestamp - BASE_TIME) << (int) this.timestampShift) | (this.workerID << (int) this.workerIdShift) | seq;
        } finally {
            if (lockStamp != 0) {
                this.lock.unlock(lockStamp);
            }
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
        return this.timeSource.get();
    }


}
