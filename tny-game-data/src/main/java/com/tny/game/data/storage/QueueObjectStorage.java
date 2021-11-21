package com.tny.game.data.storage;

import com.tny.game.common.concurrent.lock.locker.*;
import com.tny.game.data.accessor.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.*;

import java.util.*;
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
	private final Class<O> entityClass;

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
	private final Queue<StorageEntry<K, O>> entriesQueue = new ConcurrentLinkedQueue<>();

	/**
	 * 监视器
	 */
	private final ObjectStorageMonitor monitor;

	/**
	 * 对象锁
	 */
	private final ObjectLocker<Object> locker;

	public QueueObjectStorage(Class<O> entityClass, StorageAccessor<K, O> accessor, ObjectLocker<Object> locker) {
		this.accessor = accessor;
		this.locker = locker;
		this.entityClass = entityClass;
		this.monitor = new ObjectStorageMonitor(this.entityClass);
	}

	@Override
	public String getDataSource() {
		return accessor.getDataSource();
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
	public <T> List<T> find(Map<String, Object> findValue, Class<T> returnClass) {
		return accessor.find(findValue, returnClass);
	}

	@Override
	public <T> List<T> findAll(Class<T> returnClass) {
		return accessor.findAll(returnClass);
	}

	@Override
	public List<O> find(Map<String, Object> findValue) {
		return accessor.find(findValue);
	}

	@Override
	public List<O> findAll() {
		return accessor.findAll();
	}

	@Override
	public boolean save(K key, O object) {
		operate(key, object, StorageAction.SAVE);
		return true;
	}

	@Override
	public void operateAll() {
		if (!entriesMap.isEmpty()) {
			this.entriesQueue.addAll(entriesMap.values());
		}
	}

	@Override
	public StoreExecuteAction store(int maxSize, int tryTimes) {
		int operateSize = 0;
		long startAt = System.currentTimeMillis();
		long costTime;
		StoreExecuteAction action = StoreExecuteAction.WAIT;
		List<StorageEntry<K, O>> unconfirmedQueue = new LinkedList<>();
		List<StorageEntry<K, O>> retryQueue = new LinkedList<>();
		// 连续获取, 最多获取 Step 个记录进行同步
		try {
			while (operateSize < 10000) {
				StorageEntry<K, O> entry = this.entriesQueue.poll();
				if (entry != null) {
					if (!handleStorageEntry(entry)) {
						retryQueue.add(entry);
					} else {
						unconfirmedQueue.add(entry);
					}
					operateSize++;
					if (operateSize == maxSize) {
						action = StoreExecuteAction.YIELD;
					}
				} else {
					break;
				}
			}
			if (!unconfirmedQueue.isEmpty()) {
				accessor.execute();
				unconfirmedQueue.clear();
			}
		} catch (Throwable e) {
			LOGGER.error("{} store exception", entityClass, e);
			this.entriesQueue.addAll(unconfirmedQueue);
			unconfirmedQueue.clear();
		}

		if (!retryQueue.isEmpty()) {
			this.entriesQueue.addAll(retryQueue);
			retryQueue.clear();
		}
		costTime = System.currentTimeMillis() - startAt;
		// 如果同步够 Step 个记录进行一次休眠, 如果少于 step 个则有 take 进行阻塞等待.
		if (LOGGER.isInfoEnabled() && operateSize > 0) {
			LOGGER.info("同步器 {} [{}] 消耗 {} ms, 同步 {} 对象! 提交队列对象数: {}",
					QueueObjectStorage.class.getSimpleName(), this.entityClass, costTime, operateSize, this.entriesQueue.size());
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

	private void operate(K key, O object, StorageAction action) {
		Lock lock = locker.lock(key);
		try {
			StorageEntry<K, O> entry = this.entriesMap.get(key);
			if (entry == null) {
				entry = new StorageEntry<>(key, object, action);
				if (this.entriesMap.putIfAbsent(key, entry) == null) {
					this.entriesQueue.add(entry);
				}
			} else {
				if (!entry.updateOperator(object, action)) {
					LOGGER.warn("{} entry current operator[{}] can not change to Action[{}]", entry.getValue(), entry.getOperator(), action);
				}
			}
		} finally {
			locker.unlock(key, lock);
		}
	}

	private boolean handleStorageEntry(StorageEntry<K, O> entry) {
		StorageOperator operator = null;
		O value = null;
		Lock lock = locker.lock(entry.getKey());
		try {
			operator = entry.getOperator();
			if (!operator.isNeedOperate()) {
				this.entriesMap.remove(entry.getKey(), entry);
				return true;
			}
			value = entry.getValue();
			operator.operate(this.accessor, value);
			this.entriesMap.remove(entry.getKey(), entry); // TODO 是否直接清除可以??
			monitor.onSuccess(operator);
			return true;
		} catch (Throwable e) {
			LOGGER.error("operator [{}] {} exception", operator, value, e);
			entry.operationFailed();
			monitor.onFailure();
			return false;
		} finally {
			locker.unlock(entry.getKey(), lock);
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("objectClass", entityClass)
				.toString();
	}

}
