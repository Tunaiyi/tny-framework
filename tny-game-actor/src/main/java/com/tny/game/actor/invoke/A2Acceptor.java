package com.tny.game.actor.invoke;


import com.tny.game.actor.Actor;

@FunctionalInterface
public interface A2Acceptor<ACT extends Actor, A1, A2> extends ActorSender {

    void accept(ACT receiver, A1 arg1, A2 arg2);

}
