package com.tny.game.common.reflect.aop;

import com.tny.game.common.context.Attributes;

public interface AOPable<T> {

    public T get$Wraper();

    public Attributes get$Attributes();

}
