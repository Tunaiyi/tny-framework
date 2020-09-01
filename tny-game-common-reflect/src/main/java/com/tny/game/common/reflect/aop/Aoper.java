package com.tny.game.common.reflect.aop;

public interface Aoper<T> extends Aopable<T> {

    public void set$Avice(BeforeAdvice advice);

    public void set$Avice(AfterReturningAdvice advice);

    public void set$Avice(ThrowsAdvice advice);

}
