package com.tny.game.actor.invoke;


import com.tny.game.actor.Actor;

/**
 * Created by Kun Yang on 16/4/23.
 */
public interface A0Answerer<ACT extends Actor, R> extends ActorSender {

    R answer(ACT receiver);

}
