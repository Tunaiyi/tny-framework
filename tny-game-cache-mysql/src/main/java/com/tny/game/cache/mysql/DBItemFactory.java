package com.tny.game.cache.mysql;


public interface DBItemFactory {

    public DBItem create(String key, Object data, long version, long expire);

}
