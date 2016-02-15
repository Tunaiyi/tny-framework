package com.tny.game.actor.event;

import com.tny.game.actor.ActorRef;

/**
 * 事件总线
 * Created by Kun Yang on 16/1/17.
 */
public interface ActorEventBus<T, E> extends EventBus<ActorRef, T, E> {


}
