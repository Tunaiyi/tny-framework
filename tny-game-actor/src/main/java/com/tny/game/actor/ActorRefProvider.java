package com.tny.game.actor;

import com.tny.game.actor.local.BaseActorSystem;

public interface ActorRefProvider {

    ActorRef getDeadLetters();

    InternalActorRef actorOf(BaseActorSystem system, Props props, InternalActorRef self, ActorPath childPath, boolean systemService);

}
