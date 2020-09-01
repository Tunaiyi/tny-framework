package com.tny.game.common.scheduler;

import com.thoughtworks.xstream.XStream;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.config.*;
import com.tny.game.common.utils.*;
import org.apache.commons.io.monitor.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;

/**
 * @author KGTny
 * @ClassName: TimeTaskScheduler
 * @Description: 时间任务调度器
 * @date 2011-10-28 下午3:02:57
 * <p>
 * 时间任务调度器
 * <p>
 * <br>
 */
public class TimeTaskScheduler {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(LogAide.TIME_TASK);

    /**
     * 配置文件
     */
    private static final String TIME_CONFIG_PATH = "TimeTaskModel.xml";

    private final String path;

    /**
     * 执行线程池
     *
     * @uml.property name="executorService"
     */
    private ScheduledExecutorService executorService = Executors
            .newScheduledThreadPool(1, new CoreThreadFactory("TimeTaskSchedulerThread"));

    /**
     * 任务队列
     *
     * @uml.property name="timeTaskQueue"
     */
    private TimeTaskQueue timeTaskQueue = new TimeTaskQueue();

    /**
     * 时间任务监听器
     *
     * @uml.property name="listenerList"
     */
    private final List<TimeTaskListener> listenerList = new CopyOnWriteArrayList<>();

    /**
     * 时间任务模型持有器
     *
     * @uml.property name="taskModelSet"
     */
    private NavigableSet<TimeTaskModel> taskModelSet = new ConcurrentSkipListSet<>();

    /**
     * 时间任务处理器管理器
     *
     * @uml.property name="handlerHodler"
     */
    private final TimeTaskHandlerHolder handlerHodler;

    /**
     * 任务队列存储器
     *
     * @uml.property name="store"
     */
    private final SchedulerStore store;

    /**
     * 停止时间
     *
     * @uml.property name="stopTime"
     */
    private long stopTime = 0;

    private final AtomicBoolean state = new AtomicBoolean(false);

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final Lock readLock = this.lock.readLock();

    private final Lock writeLock = this.lock.readLock();

    /**
     * 配置修改监听器
     *
     * @uml.property name="listener"
     */
    private final FileAlterationListener listener = new FileAlterationListenerAdaptor() {

        @Override
        public void onFileChange(final File file) {
            TimeTaskScheduler.this.reloadSchedule();
        }

    };

    /**
     * 构造器
     *
     * @param handlerHolder
     * @throws Exception
     */
    public TimeTaskScheduler(TimeTaskHandlerHolder handlerHolder, SchedulerStore store) throws Exception {
        this(TIME_CONFIG_PATH, handlerHolder, store);
    }

    /**
     * 构造器
     *
     * @param handlerHodler
     * @throws Exception
     */
    public TimeTaskScheduler(String path, TimeTaskHandlerHolder handlerHodler, SchedulerStore store) throws Exception {
        if (handlerHodler == null) {
            throw new NullPointerException("handlerHolder is null");
        }
        this.handlerHodler = handlerHodler;
        this.store = store;
        this.path = path;
    }

    /**
     * 添加监听器 <br>
     *
     * @param listener 监听器
     */
    public void addListener(TimeTaskListener listener) {
        this.listenerList.add(listener);
    }

    /**
     * 添加监听器集合 <br>
     * <br>
     *
     * @param listenerColl 监听器集合
     */
    public void addListener(Collection<TimeTaskListener> listenerColl) {
        this.listenerList.addAll(listenerColl);
    }

    /**
     * 移除监听器 <br>
     *
     * @param listener 监听器
     */
    public void removeListener(TimeTaskListener listener) {
        this.listenerList.remove(listener);
    }

    /**
     * 获取停止时间 <br>
     *
     * @return 返回停止时间
     * @uml.property name="stopTime"
     */
    public long getStopTime() {
        return this.stopTime;
    }

    /**
     * 关闭调度器 <br>
     */
    public void shutdown() {
        this.executorService.shutdown();
    }

    /**
     * 调度任务 <br>
     *
     * @param receiver 任务接收者
     */
    public void schedule(TaskReceiver receiver) {
        this.readLock.lock();
        try {
            List<TimeTask> timeTaskList = this.timeTaskQueue.getTimeTaskHandlerByLast(receiver.getLastHandlerTime());
            if (LOG.isDebugEnabled()) {
                LOG.debug("时间任务执行者 {} 最后执行任务时间: {} # 当前执行时间: {} # 获取任务数量为: {}",
                        LogAide.msg(receiver.toString(), new Date(receiver.getLastHandlerTime()), new Date(), timeTaskList.size()));
            }

            Queue<TimeTaskEvent> eventList = new ArrayDeque<>();
            for (TimeTask timeTask : timeTaskList) {
                List<TimeTaskHandler> handlerList = this.handlerHodler.getHandlerList(receiver.getGroup(), timeTask.getHandlerList());
                eventList.add(new TimeTaskEvent(timeTask, handlerList));
            }
            receiver.handle(eventList);
        } finally {
            this.readLock.unlock();
        }
    }

    private void save() {
        if (this.store == null) {
            return;
        }
        this.stopTime = System.currentTimeMillis();
        this.store.save(this);
    }

    private void load() {
        if (this.store == null) {
            return;
        }
        SchedulerBackup backup = this.store.load();
        if (backup != null) {
            long now = System.currentTimeMillis();
            long startTime = backup.stopTime > now ? now : backup.stopTime;
            LOG.info("\n 读取存储备份 : {} 最后运行时间 - {}", backup, new Date(startTime));
            this.stopTime = startTime;
            if (backup.getTimeTaskQueue() != null) {
                this.timeTaskQueue = backup.getTimeTaskQueue();
                if (LOG.isInfoEnabled()) {
                    LOG.info("=读取存储 TimeTask=");
                    this.timeTaskQueue.getTimeTaskList()
                            .forEach(t -> LOG.info("{}", t));
                }
            }
        }
    }

    /**
     * 重新读取配置文件 <br>
     */
    private void reloadSchedule() {
        this.writeLock.lock();
        try {
            if (!this.state.compareAndSet(true, false)) {
                return;
            }
            if (this.executorService != null && !this.executorService.isShutdown()) {
                this.executorService.shutdownNow();
                this.stopTime = System.currentTimeMillis();
            }
            this.executorService = Executors.newScheduledThreadPool(1, new CoreThreadFactory("TimeTaskScheduler"));
            this.taskModelSet.clear();
            this.start();
        } catch (Exception e) {
            LOG.error("time task scheduler reload exception ", e);
        } finally {
            this.writeLock.unlock();
        }
    }

    /**
     * 初始化调度器 <br>
     */
    @SuppressWarnings("unchecked")
    private void initSchedule(InputStream inputStream) {
        this.executorService = Executors.newScheduledThreadPool(1, new CoreThreadFactory("TimeTaskScheduler"));
        this.taskModelSet.clear();
        NavigableSet<TimeTaskModel> taskModelSet = new ConcurrentSkipListSet<>();
        try {
            XStream xstream = new XStream();
            xstream.autodetectAnnotations(true);
            xstream.alias("TimeTaskModels", ArrayList.class);
            xstream.alias("TimeTaskModel", TimeTaskModel.class);
            xstream.alias("handler", String.class);
            List<TimeTaskModel> list = (List<TimeTaskModel>)xstream.fromXML(inputStream);
            for (TimeTaskModel model : list) {
                for (String handlerName : model.getHandlerList()) {
                    if (this.handlerHodler.getHandler(handlerName) == null) {
                        LOG.warn("定时任务模型 {} 处理器不存在", handlerName);
                    }
                }
                model.setStopTime(this.stopTime);
            }
            taskModelSet.addAll(list);
            this.taskModelSet = taskModelSet;
        } catch (Exception e) {
            LOG.error("init schedule exception", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOG.error("close schedule inputStream exception", e);
                }
            }
        }
        // start();
    }

    public boolean isStart() {
        return this.state.get();
    }

    /**
     * 执行船舰 <br>
     */
    public void start() throws Exception {
        if (this.state.compareAndSet(false, true)) {
            this.handlerHodler.init();
            InputStream inputStream = null;
            try {
                this.load();
                inputStream = ConfigLoader.loadInputStream(this.path, this.listener);
                this.initSchedule(inputStream);
            } catch (Exception e) {
                if (this.executorService != null && !this.executorService.isShutdown()) {
                    this.executorService.shutdownNow();
                }
                LOG.error("创建任务调度器失败!", e);
                throw e;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.executeCreateTimeTaskRunnable();
        }

    }

    private void executeCreateTimeTaskRunnable() {
        CreateTimeTaskRunnable taskRunnable = this.createCreateTimeTaskRunnable();
        if (taskRunnable != null) {
            this.execute(taskRunnable);
        }
    }

    private CreateTimeTaskRunnable createCreateTimeTaskRunnable() {
        if (this.taskModelSet.isEmpty()) {
            return null;
        }
        TimeTask timeTask = null;
        TimeTaskModel taskModel;
        do {
            taskModel = this.taskModelSet.pollFirst();
            if (timeTask == null) {
                timeTask = new TimeTask(taskModel);
            } else {
                timeTask.addTaskHandler(taskModel);
            }
            taskModel.trigger();
            this.taskModelSet.add(taskModel);
            taskModel = this.taskModelSet.first();
        } while (taskModel != null && timeTask.getExecuteTime() == taskModel.nextFireTime().getTime() / 1000L * 1000L);
        return new CreateTimeTaskRunnable(timeTask);
    }

    private void execute(CreateTimeTaskRunnable runnable) {
        long time = runnable.getRemainTime();
        if (LOG.isDebugEnabled()) {
            LOG.debug("时间任务将在 " + time + " 毫秒后执行.");
        }
        this.executorService.schedule(runnable, time, TimeUnit.MILLISECONDS);
    }

    /**
     * @return
     * @uml.property name="timeTaskQueue"
     */
    protected TimeTaskQueue getTimeTaskQueue() {
        return this.timeTaskQueue;
    }

    private void fireTrigger(TimeTask timeTask) {
        for (TimeTaskListener listener : this.listenerList) {
            try {
                listener.trigger(timeTask);
            } catch (Throwable e) {
                LOG.error("trigger listener handle exception", e);
            }
        }
    }

    /**
     * @author KGTny
     * @ClassName : CreateTimeTaskRunnable
     * @Description : 创建时间任务
     * @date 2011-10-28 下午3:55:55
     * <p>
     * <br>
     */
    private class CreateTimeTaskRunnable implements Runnable {

        /**
         * @uml.property name="timeTask"
         */
        private final TimeTask timeTask;

        private CreateTimeTaskRunnable(TimeTask timeTask) {
            this.timeTask = timeTask;
        }

        @Override
        public void run() {
            if (this.timeTask != null) {
                try {
                    TimeTaskScheduler.this.writeLock.lock();
                    try {
                        TimeTaskScheduler.this.timeTaskQueue.put(this.timeTask);
                        if (LOG.isInfoEnabled()) {
                            LOG.info(" =插入新 timetask =\n{}", this.timeTask);
                        }
                    } finally {
                        TimeTaskScheduler.this.writeLock.unlock();
                    }
                    TimeTaskScheduler.this.fireTrigger(this.timeTask);
                } finally {
                    TimeTaskScheduler.this.save();
                }
            }
            TimeTaskScheduler.this.executeCreateTimeTaskRunnable();
        }

        public long getRemainTime() {
            long time = this.timeTask.getExecuteTime() - System.currentTimeMillis();
            return time < 0 ? 0 : time;
        }

    }

    ;

    public static void main(String[] args) {

    }

}