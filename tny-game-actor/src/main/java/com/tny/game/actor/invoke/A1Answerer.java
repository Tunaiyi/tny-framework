package com.tny.game.actor.invoke;


import com.tny.game.actor.Actor;

@FunctionalInterface
public interface A1Answerer<ACT extends Actor, R, A1> extends ActorSender {

    R answer(ACT receiver, A1 arg1);

}
