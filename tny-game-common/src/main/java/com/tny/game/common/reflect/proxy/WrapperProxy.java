package com.tny.game.common.reflect.proxy;

public interface WrapperProxy<T> {

    public T get$Wrapper();

    public void set$Proxied(T object);

}
