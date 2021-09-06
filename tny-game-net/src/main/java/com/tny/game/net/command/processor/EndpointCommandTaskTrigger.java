package com.tny.game.net.command.processor;

import com.tny.game.common.runtime.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.task.*;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.base.NetLogger.*;
import static com.tny.game.net.endpoint.task.CommandTaskBoxConstants.*;
import static com.tny.game.net.endpoint.task.CommandTaskBoxStatus.*;
import static org.slf4j.LoggerFactory.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/20 7:41 下午
 */
public class EndpointCommandTaskTrigger implements Runnable {

	private static final Logger LOG_NET = getLogger(NetLogger.EXECUTOR);

	/**
	 * 执行状态
	 */
	private final AtomicInteger executeStatus = new AtomicInteger(STOP_VALUE);

	/**
	 * 消息盒子
	 */
	private final CommandTaskBox taskBox;

	/**
	 * 当前任务
	 */
	private volatile Command currentCommand;

	private final EndpointCommandTaskProcessor<EndpointCommandTaskTrigger> processor;

	private long commandStartTime = 0;

	private int commandTickTimes = 0;

	public EndpointCommandTaskTrigger(CommandTaskBox taskBox, EndpointCommandTaskProcessor<?> processor) {
		this.taskBox = taskBox;
		this.processor = as(processor);
	}

	private boolean tickCommand() {
		if (!doCommand(this.currentCommand)) {
			this.commandTickTimes++;
			this.executeStatus.set(DELAY_VALUE);
			this.processor.schedule(this);
			return false;
		} else {
			this.currentCommand = null;
			this.commandTickTimes = 0;
			this.commandStartTime = 0L;
			return true;
		}
	}

	private boolean doCommand(Command command) {
		if (executeCommand(command)) {
			return true;
		}
		int time = 0;
		while (time < this.processor.getBusSpinTimes()) {
			if (executeCommand(command)) {
				return true;
			}
		}
		time = 0;
		while (time < this.processor.getYieldTimes()) {
			if (executeCommand(command)) {
				return true;
			} else {
				Thread.yield();
			}
		}
		return command.isDone();
	}

	private boolean executeCommand(Command command) {
		try {
			ProcessTracer trace = NET_TRACE_INPUT_EXECUTE_COMMAND_WATCHER.trace();
			command.execute();
			trace.done();
		} catch (Throwable e) {
			LOG_NET.error("run command task {} exception", command.getName(), e);
		}
		return command.isDone();
	}

	@Override
	public void run() {
		this.executeStatus.set(PROCESSING_VALUE);
		while (true) {
			ProcessTracer tracer = NET_TRACE_INPUT_BOX_PROCESS_WATCHER.trace();
			try {
				if (isCommandRunning()) {
					if (!tickCommand()) {
						break;
					}
				}
				// 确保 command 执行完
				if (this.taskBox.isClosed()) {
					return;
				}
				CommandTask event = this.taskBox.poll();
				if (event != null) {
					this.currentCommand = event.createCommand();
					if (this.currentCommand != null) {
						this.commandTickTimes = 0;
						this.commandStartTime = System.currentTimeMillis();
					}
				} else {
					this.executeStatus.set(STOP_VALUE);
					this.trySubmitWhen(STOP);
					break;
				}
			} catch (Throwable e) {
				Command command = this.currentCommand;
				if (command != null) {
					LOG_NET.error("execute command {} exception", currentCommand.getName(), e);
				} else {
					LOG_NET.error("execute command exception", e);
				}
			} finally {
				tracer.done();
			}
		}
	}

	public long getCommandStartTime() {
		return this.commandStartTime;
	}

	public int getCommandTickTimes() {
		return this.commandTickTimes;
	}

	private boolean isCanRun() {
		Command command = this.currentCommand;
		return !this.taskBox.isEmpty() || (command != null && !command.isDone());
	}

	public void trySubmit() {
		int currentState = this.executeStatus.get();
		if (currentState != SUBMIT_VALUE && currentState != PROCESSING_VALUE
				&& isCanRun() && this.executeStatus.compareAndSet(currentState, SUBMIT_VALUE)) {
			this.processor.process(this);
		}
	}

	private void trySubmitWhen(CommandTaskBoxStatus status) {
		if (isCanRun() && this.executeStatus.get() == status.getId() && this.executeStatus.compareAndSet(status.getId(), SUBMIT.getId())) {
			this.processor.process(this);
		}
	}

	private boolean isCommandRunning() {
		return this.currentCommand != null && !this.currentCommand.isDone();
	}

}
