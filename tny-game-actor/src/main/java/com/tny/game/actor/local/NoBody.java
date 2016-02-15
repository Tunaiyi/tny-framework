package com.tny.game.actor.local;

import com.tny.game.actor.ActorPath;
import com.tny.game.actor.ActorRefProvider;

public final class NoBody extends MinimalActorRef {

	private static NoBody nobody = new NoBody();

	private NoBody() {
		super(null);
	}

	static NoBody noBody() {
		return nobody;
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public ActorRefProvider getProvider() {
		throw new UnsupportedOperationException("Nobody does not provide");
	}

	@Override
	public ActorPath getPath() {
		//TODO return new RootActorPath(Address("akka", "all-systems"), "/Nobody");
		return null;
	}

}
