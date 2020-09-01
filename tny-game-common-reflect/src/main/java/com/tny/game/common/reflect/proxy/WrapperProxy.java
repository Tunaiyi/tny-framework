package com.tny.game.common.reflect.proxy;

public interface WrapperProxy<T> {

    T get$Wrapper();

    void set$Proxied(T object);

}
