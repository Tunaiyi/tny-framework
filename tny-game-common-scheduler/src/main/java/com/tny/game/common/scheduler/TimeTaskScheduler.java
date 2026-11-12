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

package com.tny.game.common.scheduler;

import com.tny.game.common.*;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.utils.*;
import org.slf4j.*;

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
     * 执行线程池
     *
     * @uml.property name="executorService"
     */
    /**
     * 执行线程池（原为 static 却被实例方法 reload/shutdown 改写——
     * 多实例共享同一池，一个实例重启会杀死/替换其他实例的调度线程）。
     */
    private volatile ScheduledExecutorService executorService = Executors
            .newScheduledThreadPool(1, new CoreThreadFactory("TimeTaskSchedulerThread"));

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(LogAide.TIME_TASK);

    /**
     * 任务队列
     *
     * @uml.property name="timeTaskQueue"
     */
    private TimeTaskQueue timeTaskQueue;

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
    private volatile NavigableSet<TimeTaskTrigger> timeTaskTriggers = new ConcurrentSkipListSet<>();

    /**
     * 时间任务处理器管理器
     *
     * @uml.property name="handlerHodler"
     */
    private final TimeTaskHandlerHolder handlerHolder;

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

    private final Lock writeLock = this.lock.writeLock();

    private volatile ScheduledFuture<?> scheduledFuture;

    /**
     * 构造器
     *
     * @param handlerHolder
     */
    public TimeTaskScheduler(TimeTaskHandlerHolder handlerHolder, SchedulerStore store, int maxTaskSize) {
        if (handlerHolder == null) {
            throw new NullPointerException("handlerHolder is null");
        }
        this.handlerHolder = handlerHolder;
        this.store = store;
        this.timeTaskQueue = new TimeTaskQueue(maxTaskSize);
    }

    public boolean isStart() {
        return this.state.get();
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
     * 关闭调度器：终止状态位 + 取消待执行任务 + 关闭本实例线程池。
     * （原实现只关池：state 仍为 true，进行中的 run() 会向已关闭的池再排任务而静默炸链。）
     */
    public void shutdown() {
        // 持写锁完成终态翻转与链取消：与 run() 开头的 state 守卫构成原子交接，
        // 杜绝"检查后-调度前"窗口把新链步排进已关池
        this.writeLock.lock();
        try {
            this.state.set(false);
            ScheduledFuture<?> scheduledFuture = this.scheduledFuture;
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
                this.scheduledFuture = null;
            }
        } finally {
            this.writeLock.unlock();
        }
        this.executorService.shutdownNow();
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
                List<TimeTaskHandler> handlerList = this.handlerHolder.getHandlerList(receiver.getType(), timeTask.getHandlerList());
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
        this.store.store(this);
    }

    private void loadTaskQueue() {
        if (this.store == null) {
            return;
        }
        SchedulerBackup backup = this.store.restore();
        if (backup != null) {
            long now = System.currentTimeMillis();
            long startTime = Math.min(backup.getStopTime(), now);
            LOG.info("\n 读取存储备份 : {} 最后运行时间 - {}", backup, new Date(startTime));
            this.stopTime = startTime;
            if (backup.getTimeTaskQueue() != null) {
                this.timeTaskQueue.restore(backup.getTimeTaskQueue());
                LOG.info("=读取存储 TimeTask size : {}", this.timeTaskQueue.getTimeTaskList().size());
                if (LOG.isDebugEnabled()) {
                    this.timeTaskQueue.getTimeTaskList().forEach(t -> LOG.debug("{}", t));
                }
            }
        }
    }

    /**
     * 重新读取方案
     */
    public void reload(TimeTaskSchemesSetting setting) {
        this.writeLock.lock();
        try {
            if (!this.state.compareAndSet(true, false)) {
                return;
            }
            ScheduledFuture<?> scheduledFuture = this.scheduledFuture;
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
                this.scheduledFuture = null;
            }
            if (this.executorService != null && !this.executorService.isShutdown()) {
                this.executorService.shutdownNow();
                this.stopTime = System.currentTimeMillis();
            }
            this.executorService = Executors.newScheduledThreadPool(1, new CoreThreadFactory("TimeTaskSchedulerThread"));
            this.timeTaskTriggers.clear();
            this.start(setting);
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
    private void initSchedule(TimeTaskSchemesSetting setting) {
        this.timeTaskTriggers.clear();
        NavigableSet<TimeTaskTrigger> timeTaskTriggers = new ConcurrentSkipListSet<>();
        // 单方案失败不得连坐：原实现一个 try 包住整个循环，第 N 个方案抛错即放弃整表，
        // 调度器"启动成功"但永远不产出任务
        for (TimeTaskScheme model : setting.getTimeTaskSchemeList()) {
            try {
                List<String> tasks = model.getTasks();
                if (tasks != null) {
                    for (String handlerName : tasks) {
                        if (this.handlerHolder.getHandler(handlerName) == null) {
                            LOG.warn("定时任务模型 {} 处理器不存在", handlerName);
                        }
                    }
                }
                timeTaskTriggers.add(new TimeTaskTrigger(model, this.stopTime));
            } catch (Exception e) {
                LOG.error("init schedule exception | cron 方案 [{}] 被跳过", model.getCron(), e);
            }
        }
        this.timeTaskTriggers = timeTaskTriggers;
    }

    /**
     * 执行船舰 <br>
     */
    public void start(TimeTaskSchemesSetting setting) throws Exception {
        this.writeLock.lock();
        try {
            this.doStart(setting);
        } catch (Exception e) {
            LOG.error("time task scheduler reload exception ", e);
        } finally {
            this.writeLock.unlock();
        }
    }

    private void doStart(TimeTaskSchemesSetting setting) throws Exception {
        if (this.state.compareAndSet(false, true)) {
            try {
                this.loadTaskQueue();
                this.initSchedule(setting);
            } catch (Exception e) {
                if (this.executorService != null && !this.executorService.isShutdown()) {
                    this.executorService.shutdownNow();
                }
                LOG.error("创建任务调度器失败!", e);
                throw e;
            }
            this.executeCreateTimeTaskRunnable();
        }
    }

    private void executeCreateTimeTaskRunnable() {
        // 宕机/重启追赶：超窗过期槽位一次快进合并投递（原逐秒回放=百万次即时触发风暴）
        TimeTask catchUp = fastForwardOverdue(this.timeTaskTriggers, System.currentTimeMillis(),
                this.catchUpWindowMillis);
        if (catchUp != null) {
            LOG.warn("定时任务启动追赶：过期槽位快进合并为一次投递 {}", catchUp);
            this.timeTaskQueue.put(catchUp);
        }
        CreateTimeTaskRunnable taskRunnable = this.timeTaskRunnable();
        if (taskRunnable != null) {
            this.execute(taskRunnable);
        }
    }

    /** 追赶窗口（毫秒）：执行时间点落后 now 超过该值即视为"过期槽位"参与快进 */
    private long catchUpWindowMillis = 60_000L;

    public long getCatchUpWindowMillis() {
        return this.catchUpWindowMillis;
    }

    public void setCatchUpWindowMillis(long catchUpWindowMillis) {
        Asserts.checkArgument(catchUpWindowMillis > 0, "追赶窗口必须为正: {}", catchUpWindowMillis);
        this.catchUpWindowMillis = catchUpWindowMillis;
    }

    /**
     * 把"下一触发点落后于 now-window"的触发器逐个快进到窗口内，
     * 其处理器合并为至多一个投递任务；无需快进时返回 null。
     */
    static TimeTask fastForwardOverdue(NavigableSet<TimeTaskTrigger> triggers, long nowMillis, long windowMillis) {
        long deadline = nowMillis - windowMillis;
        Set<String> mergedHandlers = new LinkedHashSet<>();
        int fastForwarded = 0;
        while (!triggers.isEmpty()) {
            TimeTaskTrigger first = triggers.pollFirst();
            if (first == null) {
                break;
            }
            if (first.nextFireTime() >= deadline) {
                triggers.add(first);
                break;
            }
            do {
                first.trigger();
            } while (first.nextFireTime() < deadline);
            triggers.add(first);
            mergedHandlers.addAll(first.getHandlerList());
            fastForwarded++;
        }
        if (fastForwarded == 0) {
            return null;
        }
        return new TimeTask(new ArrayList<>(mergedHandlers), nowMillis / 1000 * 1000);
    }

    private CreateTimeTaskRunnable timeTaskRunnable() {
        if (this.timeTaskTriggers.isEmpty()) {
            return null;
        }
        TimeTask timeTask = null;
        TimeTaskTrigger trigger;
        do {
            trigger = this.timeTaskTriggers.pollFirst();
            if (trigger == null) {
                break;
            }
            if (timeTask == null) {
                timeTask = new TimeTask(trigger);
            } else {
                timeTask.addTaskHandler(trigger);
            }
            trigger.trigger();
            this.timeTaskTriggers.add(trigger);
            trigger = this.timeTaskTriggers.first();
        } while (trigger != null && timeTask.getExecuteTime() == trigger.nextFireTime() / 1000L * 1000L);
        return new CreateTimeTaskRunnable(timeTask);
    }

    private void execute(CreateTimeTaskRunnable runnable) {
        if (!this.state.get()) {
            // 终态止损：在途链步的尾段调度撞上关闭时，不得向已关池排任务
            return;
        }
        long time = runnable.getRemainTime();
        if (LOG.isDebugEnabled()) {
            LOG.debug("时间任务将在 " + time + " 毫秒后执行.");
        }
        scheduledFuture = this.executorService.schedule(runnable, time, TimeUnit.MILLISECONDS);
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
            // 关闭守卫：持写锁查 state，false 即终止链——不再入队、不再回调、不再排已关池
            // （shutdown/reload 的终态翻转同样持写锁，构成原子交接）
            TimeTaskScheduler.this.writeLock.lock();
            try {
                if (!TimeTaskScheduler.this.state.get()) {
                    return;
                }
            } finally {
                TimeTaskScheduler.this.writeLock.unlock();
            }
            if (this.timeTask != null) {
                try {
                    TimeTaskScheduler.this.writeLock.lock();
                    try {
                        TimeTaskScheduler.this.stopTime = timeTask.getExecuteTime();
                        TimeTaskScheduler.this.timeTaskQueue.put(this.timeTask);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(" =插入新 TimeTask = {}", this.timeTask);
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

}