package com.tny.game.common.reflect.aop;

import java.lang.reflect.Method;

public interface ThrowsAdvice {

    public void afterThrowing(Method method, Object[] args, Object target, Throwable cause);

}
