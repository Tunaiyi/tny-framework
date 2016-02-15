package com.tny.game.actor;

/**
 * Created by Kun Yang on 16/2/14.
 */
public interface ActorFactory<T> {

    Actor<T> createActor();

}
