package com.tny.game.cache;

public interface ToCacheClassHolderFactory {

    public TriggerHolder getTriggerHolder(Class<?> clazz);

    public ToCacheClassHolder getCacheClassHolder(Class<?> clazz);

}
