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

package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.result.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.application.*;
import com.tny.game.net.message.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 4:39 下午
 */
public enum RpcBodyMode {

    VOID(null, Void.class, void.class),

    MESSAGE(null, Message.class),

    MESSAGE_HEAD(null, MessageHead.class),

    RESULT(null, RpcResult.class),

    RESULT_CODE_ID(RpcCode.class, Integer.class, int.class),

    RESULT_CODE(null, ResultCode.class),

    BODY(RpcBody.class),

    //
    ;

    private final Class<? extends Annotation> bodyAnnotation;

    private final List<Class<?>> bodyClasses;

    RpcBodyMode(Class<? extends Annotation> bodyAnnotation, Class<?>... bodyTypes) {
        this.bodyAnnotation = bodyAnnotation;
        this.bodyClasses = ImmutableList.copyOf(bodyTypes);
    }

    public static RpcBodyMode typeOf(Method method, Class<?> returnClass) {
        for (RpcBodyMode type : RpcBodyMode.values()) {
            if (type.isNeedAnnotation()) {
                Annotation annotation = method.getAnnotation(type.getBodyAnnotation());
                if (annotation == null) {
                    continue;
                }
            }
            if (type.isCanReturn(returnClass)) {
                return type;
            }
        }
        return RpcBodyMode.BODY;
    }

    boolean isNeedAnnotation() {
        return this.bodyAnnotation != null;
    }

    public Class<? extends Annotation> getBodyAnnotation() {
        return bodyAnnotation;
    }

    public List<Class<?>> getBodyClasses() {
        return bodyClasses;
    }

    public boolean isCanReturn(Class<?> bodyClass) {
        if (this.bodyClasses.isEmpty()) {
            return true;
        }
        return bodyClasses.contains(bodyClass);
    }
}
