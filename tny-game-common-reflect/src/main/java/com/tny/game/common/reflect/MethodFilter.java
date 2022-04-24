package com.tny.game.common.reflect;

import java.lang.reflect.Method;

public interface MethodFilter {

    boolean filter(Method method);

}
