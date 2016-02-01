package com.tny.game.common.reflect.aop;

public interface AOPer<T> extends AOPable<T> {

    public void set$Avice(BeforeAdvice advice);

    public void set$Avice(AfterReturningAdvice advice);

    public void set$Avice(ThrowsAdvice advice);

}
