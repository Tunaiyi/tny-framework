package com.tny.game.cache;

public interface CacheTrigger<O, D, N> {

    public O triggerLoad(String key, D rawDate, N newValue);

    public D triggerReplace(String key, O rawObject, N newValue);

    public D triggerAdd(String key, O rawObject, N newValue);

    public D triggerSet(String key, O rawObject, N newValue);

    public void triggerDelete(String key, O rawObject);

}
