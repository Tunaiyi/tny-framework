package com.tny.game.data.mongodb;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/11/4 4:22 下午
 */
public interface EntityOnLoad<T> {

	Class<T> getLoadClass();

	T onLoad(T object);

}
