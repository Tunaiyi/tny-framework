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
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        AutoManageAdvice.getInstance().doAfterReturning(result, method, args, target);
    }

    @AfterThrowing(pointcut = "invokeAutoDB()", throwing = "error")
    public void persistentAfterThrowing(JoinPoint joinPoint, Throwable error) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        AutoManageAdvice.getInstance().doAfterThrowing(method, args, target, error);
    }

}
