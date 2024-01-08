package com.tny.game.common.event;

/**
 * Created by Kun Yang on 16/2/4.
 */
public interface Event<D, E extends Event<D, E>> extends EventWatch<D> {

    E forkChild();

}
