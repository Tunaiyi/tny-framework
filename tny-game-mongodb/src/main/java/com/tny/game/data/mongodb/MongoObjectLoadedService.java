package com.tny.game.data.mongodb;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/11/4 4:24 下午
 */
public interface MongoObjectLoadedService {

	<T> T onLoad(T object);

}
