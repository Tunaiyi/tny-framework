package com.tny.game.actor.local;

import com.tny.game.actor.ActorRef;

public interface ChildNode {

    Integer getAID();

    String getName();

    ActorRef getActorRef();

    boolean isReserved();

    boolean isNameReserved();

    boolean contains(ActorRef actorRef);

}
