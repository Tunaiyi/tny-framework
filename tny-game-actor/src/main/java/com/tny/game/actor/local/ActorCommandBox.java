package com.tny.game.actor.local;

import com.tny.game.actor.exception.*;
import com.tny.game.common.worker.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Actor 命令箱子
 * Created by Kun Yang on 16/4/25.
 */
public abstract class ActorCommandBox extends AbstractWorkerCommandBox<ActorCommand<?>, ActorCommandBox> {

	private ActorCell actorCell;

	private volatile boolean terminated;

	public ActorCommandBox(ActorCell actorCell) {
		super(new ConcurrentLinkedQueue<>());
		this.actorCell = actorCell;
	}

	@Override
	protected Queue<ActorCommand<?>> acceptQueue() {
		return this.queue;
	}

	protected void terminate() {
		if (this.terminated) {
			return;
		}
		this.terminated = true;
		Queue<ActorCommand<?>> queue = this.acceptQueue();
		while (!queue.isEmpty()) {
			ActorCommand<?> cmd = queue.poll();
			cmd.cancel();
			this.executeCommand(cmd);
		}
		for (ActorCommandBox box : boxes()) {
			box.getActorCell().terminate();
		}
	}

	boolean isTerminated() {
		return this.terminated;
	}

	boolean detach() {
		return this.worker != null && this.worker.unregister(this);
	}

	private ActorCell getActorCell() {
		return this.actorCell;
	}

	private void checkTerminated() {
		if (this.isTerminated()) {
			throw new ActorTerminatedException(this.actorCell.getActor());
		}
	}

	public int getStepSize() {
		return this.actorCell.getStepSize();
	}

	@Override
	public boolean accept(ActorCommand<?> command) {
		this.checkTerminated();
		return super.accept(command);
	}

	@Override
	public boolean bindWorker(CommandWorker worker) {
		return !this.isTerminated() && super.bindWorker(worker);
	}

	@Override
	public boolean unbindWorker() {
		return super.unbindWorker();
	}

	@Override
	public boolean register(CommandBox commandBox) {
		return !this.terminated && super.register(commandBox);
	}

}
