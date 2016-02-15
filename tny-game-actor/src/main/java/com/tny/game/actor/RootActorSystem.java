package com.tny.game.actor;

import com.tny.game.actor.event.EventStream;

import java.util.Collection;


public class RootActorSystem implements ActorSystem {

	@Override
	public ActorRef actorOf(Props props) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActorRef actorOf(Props props, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stop(ActorRef actor) {
		// TODO Auto-generated method stub

	}

	@Override
	public ActorPath asPath(String childPath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActorPath asPath(Collection<String> childPath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerOnTermination(Runnable runnable) {
		// TODO Auto-generated method stub

	}

	@Override
	public Scheduler scheduler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventStream eventStream() {
		return null;
	}

	@Override
	public ActorRef getDeadLetters() {
		return null;
	}

}
