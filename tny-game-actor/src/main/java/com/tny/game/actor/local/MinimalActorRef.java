package com.tny.game.actor.local;

import com.tny.game.actor.*;

public abstract class MinimalActorRef extends InternalActorRef {

	protected MinimalActorRef(ActorPath path) {
		super(path);
	}

	@Override
	public InternalActorRef getParent() {
		return ActorUtils.noBody();
	}

	@Override
	public InternalActorRef getChild(Iterable<String> name) {
		for (String value : name) {
			if (value.length() > 0)
				return ActorUtils.noBody();
		}
		return this;
	}

	@Override
	public void start() {
	}

	@Override
	public void suspend() {
	}

	@Override
	public void resume(Throwable causedByFailure) {
	}

	@Override
	public void stop() {
	}

	@Override
	public boolean isTerminated() {
		return false;
	}

	@Override
	public void tell(Object message) {
	}

	@Override
	public void tell(Object message, ActorRef sender) {
	}

	@Override
	public <V> Answer<V> ask(Object message, ActorRef sender, Answer<V> answer) {
		return null;
	}

	@Override
	public void sendSystemMessage(SystemMessage message) {
	}

	@Override
	public void restart(Throwable caused) {
	}

	@Override
	public boolean isLocal() {
		return true;
	}

}
