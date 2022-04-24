package com.tny.game.data.mongodb;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/11/4 4:24 下午
 */
public class NoopMongoObjectLoadedService implements MongoObjectLoadedService {

    @Override
    public <T> T onLoad(T object) {
        return object;
    }

}
