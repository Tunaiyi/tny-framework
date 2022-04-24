package com.tny.game.basics.transaction;

import com.tny.game.boot.transaction.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

/**
 * Created by Kun Yang on 16/1/30.
 */
@Aspect
public class AutoTransactionAspect {

    @Pointcut("execution(* *(..)) && @annotation(com.tny.game.basics.transaction.annotation.InTransaction)")
    public void invokeTransaction() {
    }

    @Around("invokeTransaction()")
    public Object transactionAround(ProceedingJoinPoint joinPoint) throws Throwable {
        TransactionManager.open();
        try {
            Object result = joinPoint.proceed();
            TransactionManager.close();
            return result;
        } catch (Throwable e) {
            TransactionManager.rollback(e);
            throw e;
        }
    }

}
