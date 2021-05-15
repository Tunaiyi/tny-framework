package com.tny.game.common.collection.map.access;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2021/5/11 12:43 下午
 */
@FunctionalInterface
public interface NullHandler<T> {

    T onNull(String key);

}
