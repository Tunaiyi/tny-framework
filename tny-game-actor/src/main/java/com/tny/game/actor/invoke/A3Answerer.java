package com.tny.game.actor.invoke;


import com.tny.game.actor.Actor;

@FunctionalInterface
public interface A3Answerer<ACT extends Actor, R, A1, A2, A3> extends ActorSender {

    R answer(ACT receiver, A1 arg1, A2 arg2, A3 arg3);

}
