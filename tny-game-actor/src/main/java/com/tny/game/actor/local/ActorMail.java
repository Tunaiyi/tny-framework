package com.tny.game.actor.local;


import com.tny.game.actor.Actor;

/**
 * Created by Kun Yang on 16/4/26.
 */
public interface ActorMail<M> extends ActorMessage {

    M getMessage();

    <ACT extends Actor> ACT getSender();

}
