package com.tny.game.asyndb.impl;

import com.tny.game.asyndb.*;
import com.tny.game.asyndb.log.*;
import com.tny.game.common.concurrent.*;
import org.slf4j.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 平均速度同步线程执行器
 *
 * @author KGTny
 */
public class AverageRateSyncDBExecutor implements SyncDBExecutor {

    private final static Logger LOGGER = LoggerFactory.getLogger(LogName.SYNC_DB_EXECUTOR);

    private long startTime = 0L;

    private long stopTime = 0L;

    /**
     * 每次同步多少条记录 休眠时间1000/(SPEED/STEP)
     */
    private final int step;

    /**
     * 每秒同步速度 休眠时间1000/(SPEED/STEP)
     */
    private final int speed;

    /**
     * 失败尝试次数
     */
    private final int tryTime;

    /**
     * 同步队列数量
     */
    private final int syncQueueSize;

    /**
     * 是否停止
     */
    private volatile boolean stop = true;

    /**
     * 提交线程同步队列线程
     */
    private Thread sumitSyncDBTaskThread;

    /**
     * 同步线程服务
     */
    private ExecutorService executorService;

    /**
     * 同步计算器
     */
    private AtomicLong countSync = new AtomicLong();

    /**
     * 同步队列
     */
    private BlockingQueue<PersistentObject>[] syncingQueueArray;

    /**
     * 提交队列
     */
    private final BlockingQueue<PersistentObject> sumitQueue = new LinkedTransferQueue<>();

    private ScheduledExecutorService monitor;

    @SuppressWarnings("unchecked")
    public AverageRateSyncDBExecutor(int syncQueueSize, int step, int speed, int tryTime) {
        this.speed = speed;
        this.step = step;
        this.syncQueueSize = syncQueueSize;
        this.tryTime = tryTime;
        this.syncingQueueArray = new BlockingQueue[this.syncQueueSize];
        this.start();
    }

    @Override
    public boolean summit(PersistentObject object) {
        if (this.stop) {
            return false;
        }
        return this.sumitQueue.add(object);
    }

    @Override
    public boolean shutdown() throws InterruptedException {
        if (this.stop) {
            return false;
        }
        synchronized (this) {
            LOGGER.info("#SyncDBExecutor#正在关闭同步执行器......");
            this.stop = true;
            this.sumitSyncDBTaskThread.join();
            LOGGER.info("#SyncDBExecutor#同步线程池停止!");
            this.stopTime = System.currentTimeMillis();
            this.monitor.shutdown();
            LOGGER.info("#SyncDBExecutor#同步执行器关闭!");
            return true;
        }
    }

    private ExecutorService creatExecutorService() {
        return Executors.newCachedThreadPool(new CoreThreadFactory("SyncDBExecutorThread", false));
        //new ThreadPoolExecutor(SIZE, SIZE, 0L, TimeUnit.MILLISECONDS, new LinkedTransferQueue<Runnable>(),
        //new CoreThreadFactory("SyncDBExecutorThread", false));
    }

    private boolean doSync(Synchronizer<?> synchronizer, Object object, AsyncDBState currentState) {
        for (int trySyncCount = 0; trySyncCount++ < this.tryTime; trySyncCount++) {
            try {
                currentState.doOperation(synchronizer, object);
                return true;
            } catch (Exception e) {
                LOGGER.error("#AsynDBEntity#尝试同步异常尝试 {} 次", trySyncCount, e);
            }
        }
        return false;
    }

    private void recordSuccess() {
        if (this.countSync.get() == Long.MAX_VALUE) {
            this.countSync.compareAndSet(Long.MAX_VALUE, 0);
        }
        this.countSync.incrementAndGet();
    }

    public boolean start() {
        if (this.stop) {
            synchronized (this) {
                this.executorService = this.creatExecutorService(); // new
                LOGGER.info("#SyncDBExecutor#正在启动同步执行器......");
                this.startTime = System.currentTimeMillis();
                this.stopTime = 0L;
                this.stop = false;

                for (int index = 0; index < this.syncingQueueArray.length; index++) {
                    this.syncingQueueArray[index] = new LinkedTransferQueue<>();
                    final BlockingQueue<PersistentObject> synchronizableQueue = this.syncingQueueArray[index];
                    this.executorService.execute(new Runnable() {

                        @Override
                        public void run() {
                            while (!AverageRateSyncDBExecutor.this.stop || !synchronizableQueue.isEmpty() ||
                                   !AverageRateSyncDBExecutor.this.sumitQueue.isEmpty()) {
                                PersistentObject synchronizable;
                                try {
                                    synchronizable = synchronizableQueue.poll(1000, TimeUnit.MILLISECONDS);
                                    if (synchronizable == null) {
                                        continue;
                                    }
                                    try {
                                        TrySyncDone done = synchronizable.trySync();
                                        if (!done.isSync()) {
                                            continue;
                                        }
                                        Synchronizer<?> synchronizer = synchronizable.getSynchronizer();
                                        AsyncDBState state = done.getState();
                                        if (AverageRateSyncDBExecutor.this.doSync(synchronizer, done.getValue(), state)) {
                                            AverageRateSyncDBExecutor.this.recordSuccess();
                                        } else {
                                            synchronizable.syncFail(state);
                                        }
                                    } catch (Exception e) {
                                        LOGGER.error("#SyncDBExecutor#同步异常 : {}", synchronizable, e);
                                    }
                                } catch (InterruptedException e1) {
                                    LOGGER.error("#SyncDBExecutor#等待中断", e1);
                                }
                            }
                        }

                    });
                }
                this.sumitSyncDBTaskThread = new Thread(new SyncController(), "SumitSyncDBTaskThread");
                this.sumitSyncDBTaskThread.start();
                this.monitor = Executors.newScheduledThreadPool(1, new CoreThreadFactory("SyncDBMonitor"));
                this.monitor.schedule(new Runnable() {

                    private long preTime = 0;

                    @Override
                    public void run() {
                        long thisTime = AverageRateSyncDBExecutor.this.countSync.get();
                        int syncingSize = 0;
                        for (BlockingQueue<PersistentObject> queue : AverageRateSyncDBExecutor.this.syncingQueueArray)
                            syncingSize += queue.size();
                        LOGGER.debug(
                                "#SyncDBExecutor#同步器启动时间{} # 已提交同步队列数量: {} # 提交线程队列的任务数量 : {} # 已同步的任务数量 : {} # 单位时间执行数量 : {} # 停止时间 {}",
                                new Object[]{AverageRateSyncDBExecutor.this.startTime, AverageRateSyncDBExecutor.this.sumitQueue.size(), syncingSize,
                                        thisTime, thisTime - this.preTime, AverageRateSyncDBExecutor.this.stopTime});
                        this.preTime = thisTime;
                        if (!AverageRateSyncDBExecutor.this.monitor.isShutdown()) {
                            AverageRateSyncDBExecutor.this.monitor.schedule(this, 30, TimeUnit.SECONDS);
                        }
                    }

                }, 30, TimeUnit.SECONDS);
                LOGGER.info("#SyncDBExecutor#同步执行器启动!");
                return true;
            }
        }
        return false;
    }

    public int getUnsyncSize() {
        return this.sumitQueue.size();
    }

    private class SyncController implements Runnable {

        @Override
        public void run() {
            long rate = 1000 / (AverageRateSyncDBExecutor.this.speed / AverageRateSyncDBExecutor.this.step);
            ExecutorService currentService = AverageRateSyncDBExecutor.this.executorService;
            while (true) {
                ThreadPoolExecutor pool = (ThreadPoolExecutor) currentService;
                for (int i = 0; i < AverageRateSyncDBExecutor.this.step; i++) {
                    final PersistentObject synchronizable = AverageRateSyncDBExecutor.this.sumitQueue.poll();
                    if (synchronizable == null) {
                        continue;
                    }
                    LOGGER.trace("#SyncDBExecutor#提交同步线程池# : {}", synchronizable);
                    int code = synchronizable.hashCode();
                    code = Math.abs(code) % AverageRateSyncDBExecutor.this.syncQueueSize;
                    AverageRateSyncDBExecutor.this.syncingQueueArray[code].add(synchronizable);
                }
                if (AverageRateSyncDBExecutor.this.stop) {
                    if (!AverageRateSyncDBExecutor.this.sumitQueue.isEmpty()) {
                        // 要求停服，但同步队列非空，则不休眠，快速提交所有同步对象
                        continue;
                    } else if (currentService.isShutdown() == false) {
                        // 同步队列所有元素已经被提交，这个时候才能shutdown执行器
                        LOGGER.info("#SyncDBExecutor#正在停止同步线程池...... ");
                        currentService.shutdown();
                    } else {
                        // 已经调用shutdown，则等待执行器终止
                        while (currentService.isTerminated() == false) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        int unexecuteTaskSize = pool.getQueue().size();
                        // 到此所有元素被真正完全同步，syncThread即将退出
                        if (unexecuteTaskSize != 0) {
                            LOGGER.warn("#SyncDBExecutor#同步线程已停止,但还有{}任务未同步", unexecuteTaskSize);
                        }
                        break;
                    }
                } else {
                    // 没有停服，通过休眠来控制同步速率
                    try {
                        Thread.sleep(rate);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        System.out.println("shutdown");
        executorService.shutdown();
        System.out.println("shutdowning");

        try {
            while (!executorService.isTerminated()) {
                executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
                System.out.println("shutdowning.....");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("shutdowned");
    }

}
