package com.tny.game.actor.event;

/**
 * 事件总线
 * Created by Kun Yang on 16/1/17.
 */
public interface EventBus<S, T, E> {

    boolean subscribe(S subscriber, T to);

    boolean unsubscribe(S subscriber, T from);

    void unsubscribe(S subscriber);

    void publish(E event);

}
