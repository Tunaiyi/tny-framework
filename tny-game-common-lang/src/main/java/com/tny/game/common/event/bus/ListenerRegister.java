package com.tny.game.common.event.bus;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-08 14:50
 */
public interface ListenerRegister<L> {

    void addListener(L handler);

    void removeListener(L handler);

    void clearListener();

}
