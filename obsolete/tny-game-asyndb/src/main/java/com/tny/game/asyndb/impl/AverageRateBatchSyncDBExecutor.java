package com.tny.game.asyndb.impl;

import com.tny.game.asyndb.*;
import com.tny.game.asyndb.log.*;
import com.tny.game.common.concurrent.*;
import org.slf4j.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 平均速度同步线程执行器
 *
 * @author KGTny
 */
public class AverageRateBatchSyncDBExecutor implements SyncDBExecutor {

    private final static Logger LOGGER = LoggerFactory.getLogger(LogName.SYNC_DB_EXECUTOR);

    /**
     *
     */
    private final int step;

    /**
     * 失败尝试次数
     */
    private final int tryTime;

    /**
     * 每次暂停时间
     */
    private final long waitTime;

    /**
     * 是否停止
     */
    private volatile boolean stop = true;

    /**
     * 提交线程同步队列线程
     */
    private ExecutorService submitSyncDBTaskExecutor = Executors.newCachedThreadPool(new CoreThreadFactory("SubmitSyncDBTask"));

    /**
     * 同步计算器
     */
    private AtomicLong countSync = new AtomicLong();

    /**
     * 提交队列
     */
    //	private final BlockingQueue<Synchronizable> sumitQueue = new LinkedTransferQueue<Synchronizable>();

    private final BlockingQueue<PersistentObject>[] sumitQueues;

    @SuppressWarnings("unchecked")
    public AverageRateBatchSyncDBExecutor(int step, long waitTime, int tryTime, int syncThreadSize) {
        this.step = step;
        this.waitTime = waitTime < 0 ? 0 : waitTime;
        this.tryTime = tryTime;
        this.sumitQueues = new BlockingQueue[syncThreadSize];
        for (int index = 0; index < syncThreadSize; index++) {
            this.sumitQueues[index] = new LinkedTransferQueue<PersistentObject>();
        }
        this.start();
    }

    @Override
    public boolean summit(PersistentObject object) {
        if (this.stop) {
            return false;
        }
        Object value = object.getObject();
        if (value != null) {
            int hashCode = Math.abs(value.hashCode());
            this.sumitQueues[hashCode % this.sumitQueues.length].add(object);
        }
        return true;
    }

    @Override
    public boolean shutdown() throws InterruptedException {
        if (this.stop) {
            return false;
        }
        synchronized (this) {
            LOGGER.info("#SyncDBExecutor#正在关闭同步执行器......");
            this.stop = true;
            LOGGER.info("#SyncDBExecutor#同步线程池shutdown....");
            this.submitSyncDBTaskExecutor.shutdown();
            while (!this.submitSyncDBTaskExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
            }
            LOGGER.info("#SyncDBExecutor#同步线程池停止!");
            LOGGER.info("#SyncDBExecutor#同步执行器关闭!");
            return true;
        }
    }

    public boolean start() {
        if (this.stop) {
            synchronized (this) {
                LOGGER.info("#SyncDBExecutor#正在启动同步执行器......");
                this.stop = false;
                int id = 0;
                for (BlockingQueue<PersistentObject> queues : this.sumitQueues) {
                    this.submitSyncDBTaskExecutor.submit(new SyncController(id++, queues));
                }
                LOGGER.info("#SyncDBExecutor#同步执行器启动!");
                return true;
            }
        }
        return false;
    }

    private void recordSucc(int trySize) {
        while (true) {
            long value = this.countSync.get();
            if (value < 0 || value == Long.MAX_VALUE) {
                value = 0;
            }
            if (this.countSync.compareAndSet(value, value + trySize)) {
                break;
            }
        }
    }

    public final class SyncTask {

        //		private Synchronizer<?> synchronizer;
        //		private Set<Object> syncSet = new HashSet<>();

        private boolean finish = false;

        private AsyncDBState state;

        private Map<Synchronizer<?>, Set<Object>> synchronizerMap = new HashMap<>();

        private Map<Object, PersistentObject> persistentObjectMap = new HashMap<>();

        public SyncTask(AsyncDBState state) {
            this.state = state;
        }

        public boolean put(AsyncDBState state, Object value, PersistentObject synchronizable) {
            if (state != this.state) {
                return false;
            }
            Synchronizer<?> synchronizer = synchronizable.getSynchronizer();
            Set<Object> syncSet = this.synchronizerMap.computeIfAbsent(synchronizer, k -> new HashSet<>());
            if (syncSet.add(value)) {
                this.persistentObjectMap.put(value, synchronizable);
            }
            return true;
        }

        public boolean isFinish() {
            return this.finish;
        }

        public void sync() {
            long time = System.currentTimeMillis();
            Collection<Object> fails = Collections.emptyList();
            for (Entry<Synchronizer<?>, Set<Object>> entry : this.synchronizerMap.entrySet()) {
                Synchronizer<?> synchronizer = entry.getKey();
                Set<Object> syncSet = entry.getValue();
                for (int trySyncCount = 0; trySyncCount++ < AverageRateBatchSyncDBExecutor.this.tryTime; trySyncCount++) {
                    try {
                        fails = this.state.doOperation(synchronizer, syncSet);
                        AverageRateBatchSyncDBExecutor.this.recordSucc(syncSet.size());
                        break;
                    } catch (Exception e) {
                        fails = syncSet;
                        LOGGER.error("#AsynDBEntity#尝试同步异常尝试 {} 次", trySyncCount, e);
                    }
                }
                for (Object fail : fails) {
                    PersistentObject persistentObject = this.persistentObjectMap.get(fail);
                    persistentObject.syncFail(this.state);
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.info("SyncTask 通过 {} 执行 {} 同步size = {} 消耗 : {}", synchronizer.getClass().getName(), this.state, syncSet.size(),
                            System.currentTimeMillis() - time);
                }
            }
            this.finish = true;

        }

    }

    private class SyncController implements Runnable {

        private int syncID;

        private BlockingQueue<PersistentObject> sumitQueue;

        public SyncController(int syncID, BlockingQueue<PersistentObject> sumitQueue) {
            super();
            this.syncID = syncID;
            this.sumitQueue = sumitQueue;
        }

        @Override
        public void run() {
            long time = 0;
            while (true) {
                try {
                    long costTime = 0;
                    long startAt = System.currentTimeMillis();
                    if (time == 0 || System.currentTimeMillis() > time) {
                        SyncTask task = null;
                        int syncSize;
                        for (syncSize = 0; syncSize < AverageRateBatchSyncDBExecutor.this.step && !this.sumitQueue.isEmpty(); ) {
                            try {
                                final PersistentObject persistentObject = this.sumitQueue.poll();
                                if (persistentObject == null) {
                                    continue;
                                }
                                TrySyncDone done = persistentObject.trySync();
                                if (!done.isSync()) {
                                    continue;
                                }
                                syncSize++;
                                AsyncDBState state = done.getState();
                                if (task == null) {
                                    task = new SyncTask(state);
                                }
                                if (!task.put(state, done.getValue(), persistentObject)) {
                                    if (!task.isFinish()) {
                                        task.sync();
                                    }
                                    task = new SyncTask(state);
                                    task.put(state, done.getValue(), persistentObject);
                                }
                            } catch (Throwable e) {
                                LOGGER.error("同步对象异常", e);
                            }
                        }
                        if (task != null && !task.isFinish()) {
                            task.sync();
                        }
                        int size = this.sumitQueue.size();
                        costTime = System.currentTimeMillis() - startAt;
                        if (LOGGER.isInfoEnabled() && syncSize > 0) {
                            LOGGER.info("同步器 [{}-{}] 消耗 {} ms, 同步 {} 对象! 提交队列对象数: {}",
                                    AverageRateBatchSyncDBExecutor.class.getSimpleName(),
                                    this.syncID,
                                    costTime, syncSize, size);
                        }
                        time = System.currentTimeMillis() + AverageRateBatchSyncDBExecutor.this.waitTime;
                    }
                    if (AverageRateBatchSyncDBExecutor.this.stop) {
                        if (!this.sumitQueue.isEmpty()) {
                            // 要求停服，但同步队列非空，则不休眠，快速提交所有同步对象
                            continue;
                        } else {
                            break;
                        }
                    } else {
                        // 没有停服，通过休眠来控制同步速率
                        try {
                            if (costTime < 50L) {
                                Thread.sleep(50L);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Throwable e) {
                    LOGGER.error("同步对象提交队列异常", e);
                }
            }
            LOGGER.info("同步器 {} 关闭成功!", AverageRateBatchSyncDBExecutor.class);
        }

    }

}
