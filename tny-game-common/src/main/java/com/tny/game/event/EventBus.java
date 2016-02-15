package com.tny.game.event;

/**
 * Created by Kun Yang on 16/2/4.
 */
public interface EventBus<D> {

    void add(D delegate);

    void remove(D delegate);

    void clear();

}

