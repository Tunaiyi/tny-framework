package com.tny.game.common.reflect.aop;

import java.lang.reflect.Method;

public interface BeforeAdvice {

    public void before(Method method, Object[] args, Object target) throws Throwable;

}
