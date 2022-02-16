package com.tny.game.common.worker;

import com.tny.game.common.worker.command.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FrequencyCommandBox<C extends Command, CB extends CommandBox<C>> extends AbstractWorkerCommandBox<C, CB> {

	public FrequencyCommandBox(Queue<C> queue) {
		super(queue);
	}

	public FrequencyCommandBox() {
		super(new ConcurrentLinkedQueue<>());
	}

	@Override
	protected Queue<C> acceptQueue() {
		return this.queue;
	}

	@Override
	public boolean submit() {
		return true;
	}

	@Override
	protected void doProcess() {
		Queue<C> queue = this.acceptQueue();
		long startTime = System.currentTimeMillis();
		int currentSize = queue.size();
		this.runSize = 0;
		for (C cmd : queue) {
			currentSize++;
			if (this.runSize > currentSize) {
				break;
			}
			executeCommand(cmd);
			this.runSize++;
			if (cmd.isDone()) {
				queue.remove(cmd);
			}
		}
		for (CommandBox<?> commandBox : boxes()) {
			commandBox.process();
			// runSize += commandBox.getProcessSize();
		}
		long finishTime = System.currentTimeMillis();
		this.runUseTime = finishTime - startTime;
	}

	@Override
	public boolean execute(CommandBox<?> commandBox) {
		return true;
	}

}
