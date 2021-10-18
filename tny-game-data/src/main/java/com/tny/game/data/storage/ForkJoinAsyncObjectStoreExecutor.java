package com.tny.game.data.storage;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.concurrent.collection.*;
import org.apache.commons.lang3.builder.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 5:10 下午
 */
public class ForkJoinAsyncObjectStoreExecutor implements AsyncObjectStoreExecutor {

	public static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinAsyncObjectStoreExecutor.class);

	private final ScheduledExecutorService scheduledExecutorService =
			Executors.newScheduledThreadPool(1, new CoreThreadFactory("ForkJoinAsyncObjectStoreScheduled", true));

	/**
	 * 线程池
	 */
	private final ForkJoinPool forkJoinPool;

	/**
	 * 持久化任务
	 */
	private final Set<ObjectStorageTask> tasks = new ConcurrentHashSet<>();

	/**
	 * 关闭状态
	 */
	private final AtomicBoolean shutdown = new AtomicBoolean(false);

	/**
	 * 配置
	 */
	private final AsyncObjectStoreExecutorSetting setting;

	public ForkJoinAsyncObjectStoreExecutor(AsyncObjectStoreExecutorSetting setting) {
		this.setting = setting;
		this.forkJoinPool = ForkJoinPools.pool(setting.getParallelSize(), "ForkJoinAsyncObjectStoreExecutor");
	}

	@Override
	public boolean shutdown() throws InterruptedException {
		if (shutdown.compareAndSet(false, true)) {
			this.flushAllStorage();
			forkJoinPool.shutdown();
			return forkJoinPool.awaitTermination(setting.getShutdownWaitTimeout(), TimeUnit.MILLISECONDS);
		}
		return false;
	}

	@Override
	public void register(AsyncObjectStorage<?, ?> storage) {
		ObjectStorageTask task = new ObjectStorageTask(storage);
		if (tasks.add(task)) {
			delay(task, setting.getIdleInterval());
		}
	}

	private void flushAllStorage() {
		List<ForkJoinTask<?>> futures = tasks.stream().map(ObjectStorageTask::flush).collect(Collectors.toList());
		long start = System.currentTimeMillis();
		long timeout = setting.getShutdownWaitTimeout();
		for (ForkJoinTask<?> future : futures) {
			long current = System.currentTimeMillis();
			long waitTime = Math.max(timeout - (current - start), 0);
			if (waitTime > 0) {
				try {
					future.get(waitTime, TimeUnit.MILLISECONDS);
				} catch (Throwable e) {
					LOGGER.error("shutdown wait exception", e);
				}
			}
		}
	}

	private void delay(ObjectStorageTask task, long delayTime) {
		scheduledExecutorService.schedule(task::submit, delayTime, TimeUnit.MILLISECONDS);
	}

	private class ObjectStorageTask implements Runnable {

		private final AsyncObjectStorage<?, ?> storage;

		private final AtomicBoolean submit = new AtomicBoolean(false);

		private final Lock lock = new ReentrantLock();

		private ObjectStorageTask(AsyncObjectStorage<?, ?> storage) {
			this.storage = storage;
		}

		@Override
		public void run() {
			if (!lock.tryLock()) {
				return;
			}
			StoreExecuteAction action = StoreExecuteAction.WAIT;
			try {
				action = storage.store(setting.getStep(), setting.getTryTime());
			} catch (Throwable e) {
				LOGGER.error("{} exception on store", storage, e);
			} finally {
				submit.set(false);
				lock.unlock();
				switch (action) {
					case WAIT:
						delay(this, Math.max(setting.getIdleInterval(), 100L));
						break;
					case YIELD:
						if (setting.getYieldInterval() <= 0) {
							this.submit();
						} else {
							delay(this, setting.getYieldInterval());
						}
						break;
				}
			}
		}

		public void onFlush() {
			lock.lock();
			try {
				storage.store(Integer.MAX_VALUE, setting.getTryTime());
				storage.operateAll();
				storage.store(Integer.MAX_VALUE, setting.getTryTime());
			} catch (Throwable e) {
				LOGGER.error("{} exception on flush", storage, e);
			} finally {
				lock.unlock();
			}
		}

		private ForkJoinTask<?> flush() {
			return forkJoinPool.submit(this::onFlush);
		}

		private void submit() {
			if (submit.compareAndSet(false, true)) {
				if (!forkJoinPool.isShutdown()) {
					forkJoinPool.submit(this);
				}
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}

			if (!(o instanceof ObjectStorageTask)) {
				return false;
			}

			ObjectStorageTask that = (ObjectStorageTask)o;

			return new EqualsBuilder().append(storage, that.storage).isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 37).append(storage).toHashCode();
		}

	}

}
