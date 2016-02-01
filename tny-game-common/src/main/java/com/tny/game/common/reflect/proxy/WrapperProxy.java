package com.tny.game.common.reflect.proxy;

public interface WrapperProxy<T> {

    public T get$Wraper();

    public void set$Proxyed(T object);

}
