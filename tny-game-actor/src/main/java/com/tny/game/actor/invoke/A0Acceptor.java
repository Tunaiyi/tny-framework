package com.tny.game.actor.invoke;


import com.tny.game.actor.Actor;

@FunctionalInterface
public interface A0Acceptor<ACT extends Actor> extends ActorSender {

    void accept(ACT receiver);

}
