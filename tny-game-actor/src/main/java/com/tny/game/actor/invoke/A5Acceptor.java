package com.tny.game.actor.invoke;


import com.tny.game.actor.Actor;

@FunctionalInterface
public interface A5Acceptor<ACT extends Actor, A1, A2, A3, A4, A5> extends ActorSender {

    void accept(ACT receiver, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);

}
