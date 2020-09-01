package com.tny.game.common.reflect.aop;

import com.tny.game.common.context.*;

public interface Aopable<T> {

    T get$Wraper();

    Attributes get$Attributes();

}
