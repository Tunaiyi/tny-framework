/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
