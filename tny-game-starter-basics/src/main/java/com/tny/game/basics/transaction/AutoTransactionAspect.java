/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
