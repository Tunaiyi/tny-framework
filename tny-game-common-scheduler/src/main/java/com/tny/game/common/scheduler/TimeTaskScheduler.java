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
	private static ScheduledExecutorService executorService = Executors
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

	private final Lock writeLock = this.lock.readLock();

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
			this.executorService = Executors.newScheduledThreadPool(1, new CoreThreadFactory("TimeTaskScheduler"));
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
		try {
			for (TimeTaskScheme model : setting.getTimeTaskSchemeList()) {
				for (String handlerName : model.getTasks()) {
					if (this.handlerHolder.getHandler(handlerName) == null) {
						LOG.warn("定时任务模型 {} 处理器不存在", handlerName);
					}
				}
				timeTaskTriggers.add(new TimeTaskTrigger(model, this.stopTime));
			}
			this.timeTaskTriggers = timeTaskTriggers;
		} catch (Exception e) {
			LOG.error("init schedule exception", e);
		}
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
		CreateTimeTaskRunnable taskRunnable = this.timeTaskRunnable();
		if (taskRunnable != null) {
			this.execute(taskRunnable);
		}
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