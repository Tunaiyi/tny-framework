package com.tny.game.actor.invoke;


import com.tny.game.actor.Actor;

@FunctionalInterface
public interface A1Acceptor<ACT extends Actor, A1> extends ActorSender {

    void accept(ACT receiver, A1 arg1);

}
