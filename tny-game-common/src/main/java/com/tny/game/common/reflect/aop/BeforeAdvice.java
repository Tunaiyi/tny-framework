package com.tny.game.common.reflect.aop;

import java.lang.reflect.Method;

public interface BeforeAdvice {

    void doBefore(Method method, Object[] args, Object target) throws Throwable;

}
