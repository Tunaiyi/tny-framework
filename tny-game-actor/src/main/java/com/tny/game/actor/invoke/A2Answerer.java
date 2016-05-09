package com.tny.game.actor.invoke;


import com.tny.game.actor.Actor;

@FunctionalInterface
public interface A2Answerer<ACT extends Actor, R, A1, A2> extends ActorSender {

    R answer(ACT receiver, A1 arg1, A2 arg2);

}
