package com.tny.game.common.concurrent;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/24 10:32 上午
 */
public interface ThreadLocalVar<T> {

    T get();

    void set(T value);

    void remove();

}
