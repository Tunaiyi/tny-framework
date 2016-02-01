package com.tny.game.cache;

import java.io.Serializable;

public interface CasItem<T> extends Serializable {

    public long getVersion();

    public String getKey();

    public T getData();

}
