package com.tny.game.cache.simple;

import com.tny.game.cache.*;

public class SimpleCasItem<T> implements CasItem<T> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String key;

    private T data;

    private long version;

    public SimpleCasItem(String key, T data, long version) {
        this.key = key;
        this.data = data;
        this.version = version;
    }

    public SimpleCasItem(CasItem<?> mItem, T newData) {
        this.init(mItem, newData);
    }

    private void init(CasItem<?> mItem, T newValue) {
        this.key = mItem.getKey();
        this.data = newValue;
        this.version = mItem.getVersion();
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public T getData() {
        return this.data;
    }

    protected void update() {
        this.version++;
    }

    public static void main(String[] args) {
        System.out.println("LOCK_HAED_ssss".substring("LOCK_HAED_".length()));
    }

}
