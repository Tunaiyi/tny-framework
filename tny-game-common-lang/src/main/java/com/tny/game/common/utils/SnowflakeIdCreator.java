package com.tny.game.common.utils;

import org.slf4j.*;

import java.time.*;
import java.util.concurrent.locks.StampedLock;

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

	private final StampedLock lock = new StampedLock();

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
		Asserts.checkArgument(workerIDBits + sequenceBits <= 22, "workerIDBits {} + sequenceBits {} > 22", workerIDBits, sequenceBits);
		long maxWorkerID = ~(-1L << workerIDBits);
		Asserts.checkArgument(workerID >= 0 && workerID <= maxWorkerID, "worker ID {} 不在 0 - {} 范围内", workerID, maxWorkerID);
		this.sequenceMask = ~(-1L << sequenceBits);
		this.workerIdShift = sequenceBits;
		this.timestampShift = sequenceBits + workerIDBits;
		this.workerID = workerID;
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
					if (delay < -200) {
						try {
							Thread.sleep(Math.abs(delay));
						} catch (InterruptedException e) {
							throw new IllegalArgumentException(format("时间发生回滚, {} milliseconds", lastTime - timestamp, e));
						}
					} else {
						throw new IllegalArgumentException(format("时间发生回滚, {} milliseconds", lastTime - timestamp));
					}
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
			return ((timestamp - BASE_TIME) << (int)this.timestampShift) | (this.workerID << (int)this.workerIdShift) | seq;
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
		return System.currentTimeMillis();
	}

	public static void main(String[] args) {
		// System.out.println(Math.pow(2, 13));
		// SnowflakeIdCreator creator = new SnowflakeIdCreator(0, 1);
		//
		// RunningChecker.startPrint(SnowflakeIdCreator.class);
		// for (int i = 0; i < 1000000; i++) {
		//     creator.createId();
		//     // ForkJoinPool.commonPool()
		//     //         .submit(() -> creator.createID());
		// }
		// long cost = RunningChecker.end(SnowflakeIdCreator.class).cost();
		// System.out.println(cost);
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
