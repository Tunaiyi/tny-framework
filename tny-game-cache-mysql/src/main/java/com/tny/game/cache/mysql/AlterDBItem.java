package com.tny.game.cache.mysql;

import com.tny.game.cache.AlterCacheItem;
import com.tny.game.cache.CacheItem;

import java.sql.Blob;

public class AlterDBItem<R> extends DBItem implements AlterCacheItem<R, Blob> {

    private static final long serialVersionUID = 1L;

    private CacheItem<?> item;
    private R rawValue;

    public AlterDBItem(CacheItem<?> item) {
        this(item.getKey(), item.getData(), item.getVersion(), item.getExpire());
        this.item = item;
    }

    public AlterDBItem(String key, Object data, long version, long expire) {
        super(key, data, version, expire);
    }

    @SuppressWarnings("unchecked")
    public <T extends CacheItem<?>> T getItem() {
        return (T) (this.item == null ? this : this.item);
    }

    @Override
    public R getRawValue() {
        return this.rawValue;
    }

    @Override
    public void setRawValue(R rawValue) {
        this.rawValue = rawValue;
    }

}
