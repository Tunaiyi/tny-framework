package com.tny.game.data.mongodb;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/11/4 4:22 下午
 */
public interface MongoObjectLoaded<T> {

    Class<T> getLoadClass();

    T onLoad(T object);

}
