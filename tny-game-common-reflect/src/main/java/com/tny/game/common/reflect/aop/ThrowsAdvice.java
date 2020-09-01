package com.tny.game.common.reflect.aop;

import java.lang.reflect.Method;

public interface ThrowsAdvice {

    void doAfterThrowing(Method method, Object[] args, Object target, Throwable cause);

}
