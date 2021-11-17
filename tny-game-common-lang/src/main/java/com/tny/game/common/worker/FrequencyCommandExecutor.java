package com.tny.game.common.worker;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.Queue;
import java.util.concurrent.*;

public class FrequencyCommandExecutor implements CommandExecutor {

	protected static final Logger LOGGER = LoggerFactory.getLogger(LogAide.WORKER);

	/**
	 * 名字
	 */
	protected String name;

	/**
	 * 是否停止
	 */
	private volatile boolean working = true;

	/**
	 * 当前线程
	 */
	protected Thread currentThread;

	private final Queue<CommandBox<?>> commandBoxQueue = new ConcurrentLinkedQueue<>();

	private ExecutorService executor;

	private long nextRunningTime;

	private long totalRunningTime;

	private long totalSleepTime;

	private long totalRunSize;

	private long sleepTime;

	private long runningTime;

	private int runSize;

	private int continueTime;

	private final CommandWorker worker = new CommandWorker() {

		@Override
		public boolean isOnCurrentThread() {
			return FrequencyCommandExecutor.this.currentThread == Thread.currentThread();
		}

		@Override
		public boolean isWorking() {
			return FrequencyCommandExecutor.this.isWorking();
		}

		@Override
		public boolean register(CommandBox<?> commandBox) {
			return FrequencyCommandExecutor.this.register(commandBox);
		}

		@Override
		public boolean unregister(CommandBox<?> commandBox) {
			return FrequencyCommandExecutor.this.register(commandBox);
		}

		@Override
		public boolean execute(CommandBox<?> commandBox) {
			return true;
		}

	};

	public FrequencyCommandExecutor(String name) {
		this.name = name;
	}

	@Override
	public void stop() {
		if (!this.working) {
			return;
		}
		this.working = false;
	}

	@Override
	public void start() {
		this.executor = Executors.newSingleThreadExecutor(new CoreThreadFactory(this.name, true));
		this.executor.execute(() -> {
			this.nextRunningTime = System.currentTimeMillis();
			this.currentThread = Thread.currentThread();
			while (true) {
				try {
					if (this.executor.isShutdown()) {
						break;
					}
					long currentTime = System.currentTimeMillis();
					int currentRunSize = 0;
					int currentContinueTime = 0;
					while (currentTime >= this.nextRunningTime) {
						for (CommandBox<?> box : this.commandBoxQueue) {
							this.worker.execute(box);
							// box.getProcessUseTime();
							// currentRunSize += box.getProcessSize();
						}
						this.nextRunningTime += 100L;
						currentContinueTime++;
						currentTime = System.currentTimeMillis();
					}
					if (this.commandBoxQueue.isEmpty() && !this.working) {
						return;
					}

					this.continueTime = currentContinueTime;
					this.runSize = currentRunSize;
					this.totalRunSize += this.runSize;
					if (this.totalRunSize < 0) {
						this.totalRunSize = 0;
					}

					long stopTime = System.currentTimeMillis();
					this.runningTime = stopTime - currentTime;

					this.sleepTime = this.nextRunningTime - stopTime;
					this.sleepTime = this.sleepTime < 0 ? 0 : this.sleepTime;

					this.totalRunningTime += this.runningTime;
					this.totalSleepTime += this.sleepTime;
					if (this.sleepTime > 0) {
						Thread.sleep(this.sleepTime);
					}
				} catch (InterruptedException e) {
					LOGGER.warn("InterruptedException by FrequencyWorker " + Thread.currentThread().getName(), e);
				} catch (Exception e) {
					LOGGER.warn("Exception by FrequencyWorker " + Thread.currentThread().getName(), e);
				}
			}
		});
	}

	@Override
	public void shutdown() {
		stop();
		this.executor.shutdown();
	}

	@Override
	public String toString() {
		long totalSleepTime = this.totalSleepTime;
		long totalRunningTime = this.totalRunningTime;
		long sleepTime = this.sleepTime;
		long runningTime = this.runningTime;
		int continueTime = this.continueTime;
		return this.getName() +
				" #任务数量: " +
				size() +
				" #附加任务箱数量: " +
				this.commandBoxQueue.size() +
				" #总运行数量" +
				this.totalRunSize +
				" #最近运行数量" +
				this.runSize +
				" #最近连续次数: " +
				continueTime +
				" #最近休眠时间: " +
				sleepTime +
				" #最近运行时间" +
				runningTime +
				" #最近休眠比率: " +
				(double)sleepTime / (double)(sleepTime + runningTime) +
				" #休眠总时间: " +
				totalSleepTime +
				" #运行总时间: " +
				totalRunningTime +
				" #总休眠比率: " +
				(double)totalSleepTime / (double)(totalSleepTime + totalRunningTime);
	}

	@Override
	public int size() {
		int size = 0;
		for (CommandBox<?> commandBox : this.commandBoxQueue)
			size += commandBox.size();
		return size;
	}

	@Override
	public boolean register(CommandBox<?> commandBox) {
		if (commandBox instanceof WorkerCommandBox) {
			WorkerCommandBox<?, ?> workerCommandBox = (WorkerCommandBox<?, ?>)commandBox;
			if (workerCommandBox.bindWorker(this.worker)) {
				this.commandBoxQueue.add(workerCommandBox);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean unregister(CommandBox<?> commandBox) {
		if (commandBox instanceof WorkerCommandBox) {
			WorkerCommandBox<?, ?> workerCommandBox = (WorkerCommandBox<?, ?>)commandBox;
			if (this.commandBoxQueue.remove(workerCommandBox)) {
				workerCommandBox.unbindWorker();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isWorking() {
		return this.working;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
