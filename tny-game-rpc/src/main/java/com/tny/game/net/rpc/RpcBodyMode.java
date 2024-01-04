/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
