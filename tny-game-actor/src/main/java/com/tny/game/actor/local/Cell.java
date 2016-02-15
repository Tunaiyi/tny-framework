package com.tny.game.actor.local;

import com.tny.game.actor.ActorRef;
import com.tny.game.actor.ActorSystem;
import com.tny.game.actor.InternalActorRef;
import com.tny.game.actor.Props;

import java.util.Optional;

public interface Cell extends Postor {

    ActorRef self();

    Props getProps();

    ActorSystem getSystem();

    InternalActorRef getParent();

    boolean isLocal();

    Optional<ChildNode> getChildNodeByName(String name);

    InternalActorRef getSingleChild(String name);
}
