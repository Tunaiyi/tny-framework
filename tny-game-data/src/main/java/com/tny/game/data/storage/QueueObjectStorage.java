package com.tny.game.data.storage;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.concurrent.lock.locker.*;
import com.tny.game.data.accessor.*;
import org.slf4j.*;

import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

/**
 * <p>
 */
public class QueueObjectStorage<K extends Comparable<K>, O> implements ObjectStorage<K, O> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueObjectStorage.class);

    private enum State {

        INIT,

        START,

        STOP,

    }

    private volatile State state = State.INIT;

    private static ObjectLocker<Object> locker = new ObjectLocker<>();

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(new CoreThreadFactory("QueueObjectStorageThread", true));

    private static final ScheduledExecutorService CLEAR_DELETED_SERVICE = Executors
            .newScheduledThreadPool(1, new CoreThreadFactory("StorageClearDeletedThread", true));

    /**
     * 存储的类型
     */
    private Class<O> objectClass;

    /**
     * 对象访问器
     */
    private ObjectAccessor<K, O> accessor;

    /**
     * 提交的实体
     */
    private ConcurrentMap<K, StorageEntry<K, O>> entriesMap = new ConcurrentHashMap<>();

    /**
     * 提交的任务
     */
    private TransferQueue<StorageEntry<K, O>> taskQueue = new LinkedTransferQueue<>();

    /**
     * 配置
     */
    private QueueObjectStorageSetting setting;

    /**
     * 同步线程
     */
    private Thread thread;

    /**
     * 同步线程
     */
    private Future<?> clearTaskFuture;

    private AtomicLong insertCounter = new AtomicLong();

    private AtomicLong updateCounter = new AtomicLong();

    private AtomicLong saveCounter = new AtomicLong();

    private AtomicLong deleteCounter = new AtomicLong();

    private AtomicLong failedCounter = new AtomicLong();

    public QueueObjectStorage(Class<O> objectClass, ObjectAccessor<K, O> accessor, QueueObjectStorageSetting setting) {
        this.accessor = accessor;
        this.objectClass = objectClass;
        this.setting = setting;
    }

    private void recodeSuccess(StorageOperation operation) {
        switch (operation) {
            case INSERT:
                this.insertCounter.incrementAndGet();
                break;
            case UPDATE:
                this.updateCounter.incrementAndGet();
                break;
            case SAVE:
                this.saveCounter.incrementAndGet();
                break;
            case DELETE:
                this.deleteCounter.incrementAndGet();
                break;
        }
    }

    @Override
    public synchronized void start() {
        if (this.state == State.INIT) {
            this.state = State.START;
            EXECUTOR_SERVICE.submit(this::storge);
            this.clearTaskFuture = CLEAR_DELETED_SERVICE.scheduleWithFixedDelay(this::clearDeletedEntries,
                    ThreadLocalRandom.current().nextInt(5000),
                    Math.max(this.setting.getDeletedRemoveDelay(), 3000), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public synchronized void stop() {
        if (this.state != State.STOP) {
            this.state = State.STOP;
            if (this.thread != null) {
                this.thread.interrupt();
            }
            if (this.clearTaskFuture != null) {
                this.clearTaskFuture.cancel(true);
            }
        }
    }

    private boolean isStop() {
        return this.state == State.STOP;
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
        operate(key, object, StorageOperation.INSERT);
        return true;
    }

    @Override
    public boolean update(K key, O object) {
        operate(key, object, StorageOperation.UPDATE);
        return true;
    }

    @Override
    public boolean delete(K key, O object) {
        operate(key, object, StorageOperation.DELETE);
        return true;
    }

    @Override
    public boolean save(K key, O object) {
        operate(key, object, StorageOperation.SAVE);
        return true;
    }

    @Override
    public long getInsertNumber() {
        return this.insertCounter.longValue();
    }

    @Override
    public long getUpdateNumber() {
        return this.updateCounter.longValue();
    }

    @Override
    public long getSaveNumber() {
        return this.saveCounter.longValue();
    }

    @Override
    public long getDeleteNumber() {
        return this.deleteCounter.longValue();
    }

    @Override
    public long getFailedNumber() {
        return this.failedCounter.longValue();
    }

    private void clearDeletedEntries() {
        long now = System.currentTimeMillis();
        for (Entry<K, StorageEntry<K, O>> entry : this.entriesMap.entrySet()) {
            StorageEntry<K, O> storage = entry.getValue();
            if (storage.getState() != StorageState.DELETE || !storage.isNeedClear(now)) {
                continue;
            }
            Lock lock = locker.lock(entry.getKey());
            try {
                if (storage.getState() != StorageState.DELETE || !storage.isNeedClear(now)) {
                    continue;
                }
                this.entriesMap.remove(entry.getKey(), entry.getValue());
            } catch (Throwable e) {
                LOGGER.error("", e);
            } finally {
                locker.unlock(entry.getKey(), lock);
            }
        }
    }

    private void operate(K key, O object, StorageOperation operation) {
        Lock lock = locker.lock(key);
        try {
            StorageEntry<K, O> entry = this.entriesMap.get(key);
            if (entry != null) {
                entry.updateState(object, operation);
                return;
            }
            entry = new StorageEntry<>(key, object, operation);
            this.entriesMap.put(key, entry);
            this.taskQueue.add(entry);
        } finally {
            locker.unlock(key, lock);
        }
    }

    private void storge() {
        this.thread = Thread.currentThread();
        if (this.isStop()) {
            this.thread.interrupt();
        }
        while (!this.thread.isInterrupted() || !this.taskQueue.isEmpty()) {
            int operateSize = 0;
            long startAt = 0;
            long costTime;
            try {
                StorageEntry<K, O> entry = this.taskQueue.take();
                startAt = System.currentTimeMillis();
                handleStorageEntry(entry);
                operateSize++;
            } catch (InterruptedException e) {
                LOGGER.error("sync is interrupted", e);
            }
            // 连续获取, 最多获取 Step 个记录进行同步
            while (!this.taskQueue.isEmpty() && operateSize < this.setting.getStep()) {
                StorageEntry<K, O> entry = this.taskQueue.poll();
                if (entry == null) {
                    continue;
                }
                handleStorageEntry(entry);
                operateSize++;
            }
            costTime = System.currentTimeMillis() - startAt;
            // 如果同步够 Step 个记录进行一次休眠, 如果少于 step 个则有 take 进行阻塞等待.
            if (!this.isStop() && operateSize == this.setting.getStep()) {
                try {
                    Thread.sleep(this.setting.getWaitTime());
                } catch (InterruptedException e) {
                    LOGGER.error("sync is interrupted", e);
                }
            }
            if (LOGGER.isInfoEnabled() && operateSize > 0) {
                LOGGER.info("同步器 {} [{}] 消耗 {} ms, 同步 {} 对象! 提交队列对象数: {}", QueueObjectStorage.class.getSimpleName(), this.objectClass, costTime,
                        operateSize, this.taskQueue.size());
            }
        }
        LOGGER.info("同步器 {} [{}] 关闭成功! 提交队列对象数: {}", QueueObjectStorage.class.getSimpleName(), this.objectClass, this.taskQueue.size());
    }

    private void handleStorageEntry(StorageEntry<K, O> entry) {
        StorageOperation operation = null;
        O value = null;
        int tryTime = 0;
        while (true) {
            Lock lock = locker.lock(entry.getKey());
            try {
                StorageState state = entry.getState();
                operation = state.getOperation();
                if (!operation.isNeedOperate()) {
                    return;
                }
                value = entry.getValue();
                operation.operate(this.accessor, value);
                // 记录成功失败
                recodeSuccess(operation);
                if (operation == StorageOperation.DELETE) {
                    entry.deleted(this.setting.getDeletedRemoveDelay());
                } else {
                    this.entriesMap.remove(entry.getKey(), entry);
                }
                return;
            } catch (Throwable e) {
                LOGGER.error("operate [{}] {} exception", operation, value, e);
                this.failedCounter.incrementAndGet();
                // 失败重试
                if (tryTime < this.setting.getTryTime()) {
                    this.taskQueue.add(entry);
                    tryTime++;
                } else {
                    break;
                }
            } finally {
                locker.unlock(entry.getKey(), lock);
            }
        }
    }

}
