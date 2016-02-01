package com.tny.game.suite.cache;

import com.tny.game.cache.AlterCacheItem;
import com.tny.game.cache.AlterCacheItemFactory;
import com.tny.game.cache.mysql.DBItem;
import com.tny.game.cache.mysql.DBItemFactory;

public class ItemAlterDBItemFactroy implements AlterCacheItemFactory, DBItemFactory {

    @Override
    public <T> AlterCacheItem<T, ?> create(String key, Object value, long expire) {
        return new ItemAlterDBItem<>(key, value, 0L, expire);
    }

    @Override
    public DBItem create(String key, Object data, long version, long expire) {
        return new ItemAlterDBItem<>(key, data, version, expire);
    }

}
