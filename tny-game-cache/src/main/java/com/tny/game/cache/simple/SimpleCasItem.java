package com.tny.game.cache.simple;

import com.tny.game.cache.CasItem;

public class SimpleCasItem<T> implements CasItem<T> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String key;
    private T data;
    private long vesion;

    public SimpleCasItem() {
    }

    ;

    public SimpleCasItem(String key, T data, long vesion) {
        this.key = key;
        this.data = data;
        this.vesion = vesion;
    }

    public SimpleCasItem(CasItem<?> mItem, T newData) {
        this.init(mItem, newData);
    }

    private void init(CasItem<?> mItem, T newValue) {
        this.key = mItem.getKey();
        this.data = newValue;
        this.vesion = mItem.getVersion();
    }

    @Override
    public long getVersion() {
        return this.vesion;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public T getData() {
        return this.data;
    }

    protected void updateValue(T value) {
        this.data = value;
    }

    protected void update() {
        this.vesion++;
    }

    public static void main(String[] args) {
        System.out.println("LOCK_HAED_ssss".substring("LOCK_HAED_".length()));
    }

}
