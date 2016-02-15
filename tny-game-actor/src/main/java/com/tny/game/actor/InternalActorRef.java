package com.tny.game.actor;


import com.tny.game.actor.local.ActorRefScope;

public abstract class InternalActorRef extends BaseActorRef implements ActorRef, ActorRefScope {

    protected InternalActorRef(ActorPath path) {
        super(path);
    }

    public abstract boolean isTerminated();

    public abstract void start();

    public abstract void resume(Throwable causedByFailure);

    public abstract void suspend();

    public abstract void restart(Throwable caused);

    public abstract void stop();

    public abstract void sendSystemMessage(SystemMessage message);

    public abstract ActorRefProvider getProvider();

    public abstract InternalActorRef getParent();

    public abstract InternalActorRef getChild(Iterable<String> name);

}
