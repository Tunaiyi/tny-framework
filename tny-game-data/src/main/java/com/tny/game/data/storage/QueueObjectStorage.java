package com.tny.game.data.storage;

import com.tny.game.common.concurrent.lock.locker.*;
import com.tny.game.data.accessor.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.*;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

/**
 * <p>
 */
public class QueueObjectStorage<K extends Comparable<?>, O> implements AsyncObjectStorage<K, O> {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueueObjectStorage.class);

	/**
	 * 存储的类型
	 */
	private final Class<O> objectClass;

	/**
	 * 对象访问器
	 */
	private final StorageAccessor<K, O> accessor;

	/**
	 * 提交的实体
	 */
	private final ConcurrentMap<K, StorageEntry<K, O>> entriesMap = new ConcurrentHashMap<>();

	/**
	 * 提交的任务
	 */
	private final TransferQueue<StorageEntry<K, O>> entriesQueue = new LinkedTransferQueue<>();

	/**
	 * 监视器
	 */
	private final ObjectStorageMonitor monitor;

	/**
	 * 对象锁
	 */
	private final ObjectLocker<Object> locker;

	//	/**
	//	 * 删除对象延迟清理时间
	//	 */
	//	private final int deletedDelayClear;

	//	/**
	//	 * 最后一次清理时间
	//	 */
	//	private long lastDeletedClearTime = 0;

	public QueueObjectStorage(Class<O> objectClass, StorageAccessor<K, O> accessor, ObjectLocker<Object> locker) {
		this.accessor = accessor;
		this.locker = locker;
		this.objectClass = objectClass;
		this.monitor = new ObjectStorageMonitor(this.objectClass);
	}

	@Override
	public O get(K key) {
		Lock lock = locker.lock(key);
		try {
			StorageEntry<K, O> entry = this.entriesMap.get(key);
			if (entry != null) {
				if (entry.isDelete()) {
					return null;
				}
				return entry.getValue();
			}
		} finally {
			locker.unlock(key, lock);
		}
		return this.accessor.get(key);
	}

	@Override
	public boolean insert(K key, O object) {
		operate(key, object, StorageAction.INSERT);
		return true;
	}

	@Override
	public boolean update(K key, O object) {
		operate(key, object, StorageAction.UPDATE);
		return true;
	}

	@Override
	public boolean delete(K key, O object) {
		operate(key, object, StorageAction.DELETE);
		return true;
	}

	@Override
	public boolean save(K key, O object) {
		operate(key, object, StorageAction.SAVE);
		return true;
	}

	@Override
	public StoreExecuteAction store(int maxSize, int tryTimes) {
		int operateSize = 0;
		long startAt = System.currentTimeMillis();
		long costTime;
		StoreExecuteAction action = StoreExecuteAction.WAIT;
		// 连续获取, 最多获取 Step 个记录进行同步
		while (operateSize < maxSize) {
			StorageEntry<K, O> entry = this.entriesQueue.poll();
			if (entry != null) {
				handleStorageEntry(entry, tryTimes);
				operateSize++;
				if (operateSize == maxSize) {
					action = StoreExecuteAction.YIELD;
				}
			} else {
				break;
			}
		}
		costTime = System.currentTimeMillis() - startAt;
		// 如果同步够 Step 个记录进行一次休眠, 如果少于 step 个则有 take 进行阻塞等待.
		if (LOGGER.isInfoEnabled() && operateSize > 0) {
			LOGGER.info("同步器 {} [{}] 消耗 {} ms, 同步 {} 对象! 提交队列对象数: {}",
					QueueObjectStorage.class.getSimpleName(), this.objectClass, costTime, operateSize, this.entriesQueue.size());
		}
		return action;
	}

	@Override
	public ObjectStorageMonitor getMonitor() {
		return monitor;
	}

	@Override
	public int queueSize() {
		return this.entriesQueue.size();
	}

	//	private void clearDeletedEntries() {
	//		long now = System.currentTimeMillis();
	//		for (Entry<K, StorageEntry<K, O>> entry : this.entriesMap.entrySet()) {
	//			StorageEntry<K, O> storageEntry = entry.getValue();
	//			if (!storageEntry.isNeedClear(now)) {
	//				continue;
	//			}
	//			Lock lock = locker.lock(entry.getKey());
	//			try {
	//				if (!storageEntry.isNeedClear(now)) {
	//					continue;
	//				}
	//				this.entriesMap.remove(entry.getKey(), entry.getValue());
	//			} catch (Throwable e) {
	//				LOGGER.error("", e);
	//			} finally {
	//				locker.unlock(entry.getKey(), lock);
	//			}
	//		}
	//	}

	private void operate(K key, O object, StorageAction operation) {
		Lock lock = locker.lock(key);
		try {
			StorageEntry<K, O> entry = this.entriesMap.get(key);
			if (entry == null) {
				entry = new StorageEntry<>(key, object);
			}
			if (entry.updateOperator(object, operation) && this.entriesMap.putIfAbsent(key, entry) == null) {
				this.entriesQueue.add(entry);
			}
		} finally {
			locker.unlock(key, lock);
		}
	}

	private void handleStorageEntry(StorageEntry<K, O> entry, int tryTimes) {
		StorageOperator operator = null;
		O value = null;
		while (true) {
			Lock lock = locker.lock(entry.getKey());
			try {
				operator = entry.getOperator();
				if (!operator.isNeedOperate()) {
					return;
				}
				value = entry.getValue();
				operator.operate(this.accessor, value);
				this.entriesMap.remove(entry.getKey(), entry); // TODO 是否直接清除可以??
				monitor.onSuccess(operator);
				return;
			} catch (Throwable e) {
				LOGGER.error("operator [{}] {} exception", operator, value, e);
				entry.operationFailed();
				monitor.onFailure();
				// 失败重试
				if (entry.getFailureTimes() <= tryTimes) {
					this.entriesQueue.add(entry);
				} else {
					break;
				}
			} finally {
				locker.unlock(entry.getKey(), lock);
			}
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("objectClass", objectClass)
				.toString();
	}

}
