package com.tny.game.basics.persistent;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Created by Kun Yang on 16/1/30.
 */
@Aspect
public class AutoManageAspect {

    @Pointcut("execution(* *(..)) && (" +
            "@annotation(com.tny.game.basics.persistent.annotation.Modifiable) ||" +
            "@annotation(com.tny.game.basics.persistent.annotation.ModifiableParam) || " +
            "@annotation(com.tny.game.basics.persistent.annotation.ModifiableReturn))")
    public void invokeAutoDB() {
    }

    @AfterReturning(pointcut = "invokeAutoDB()", returning = "result")
    public void persistentAfterReturn(JoinPoint joinPoint, Object result) throws Throwable {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        AutoManageAdvice.getInstance().doAfterReturning(result, method, args, target);
    }

    @AfterThrowing(pointcut = "invokeAutoDB()", throwing = "error")
    public void persistentAfterThrowing(JoinPoint joinPoint, Throwable error) throws Throwable {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        AutoManageAdvice.getInstance().doAfterThrowing(method, args, target, error);
    }

}
