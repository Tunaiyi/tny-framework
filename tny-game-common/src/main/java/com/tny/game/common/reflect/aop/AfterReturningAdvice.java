package com.tny.game.common.reflect.aop;

import java.lang.reflect.Method;

public interface AfterReturningAdvice {

    public void afterReturning(Object returnValue, Method method, Object[] args, Object target);

}
