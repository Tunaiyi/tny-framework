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
package com.tny.game.doc.controller;

import com.tny.game.common.result.*;
import com.tny.game.doc.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.doc.holder.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.rpc.*;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.Future;

public class OperationDescription extends MethodDescription {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationDescription.class);

    private final int opId;

    private final Class<?> docReturnClass;

    private final String docReturnClassName;

    private final List<OperationParamDescription> operationParamList;

    public OperationDescription(Class<?> clazz, DocMethod holder, TypeFormatter typeFormatter) {
        super(holder);
        var method = holder.getMethod();
        int opId = 0;
        RpcProfile controller = RpcProfile.oneOf(method);
        if (controller == null) {
            LOGGER.warn("{}.{} is not controller", clazz, method.getName());
        } else {
            if (controller.getProtocol() < 0) {
                LOGGER.warn("{}.{} controller value {} < 0", clazz, method.getName(), controller.getProtocol());
            }
            opId = controller.getProtocol();
        }
        List<OperationParamDescription> operationParamList = new ArrayList<>();
        for (DocParam varDocHolder : holder.getParamList()) {
            operationParamList.add(new OperationParamDescription(varDocHolder, typeFormatter));
        }
        this.operationParamList = Collections.unmodifiableList(operationParamList);
        var funReturnType = holder.getMethod().getAnnotation(FunReturnType.class);
        if (funReturnType == null || funReturnType.value() == Object.class) {
            this.docReturnClass = returnType(method.getGenericReturnType());
        } else {
            this.docReturnClass = funReturnType.value();
        }
        this.docReturnClassName = typeFormatter.format(docReturnClass);
        this.opId = opId;
    }

    private Class<?> returnType(Type type) {
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>)type;
            if (clazz == Void.class || clazz == void.class || ResultCode.class.isAssignableFrom(clazz)) {
                return Void.class;
            } else {
                return clazz;
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType returnType = (ParameterizedType)type;
            Class<?> clazz = (Class<?>)returnType.getRawType();
            if (RpcReturn.class.isAssignableFrom(clazz) || RpcResult.class.isAssignableFrom(clazz) || RpcFuture.class.isAssignableFrom(clazz)) {
                return getGenericClass(returnType);
            }
            if (Future.class.isAssignableFrom(clazz)) {
                return returnType(getGenericType(returnType));
            }
            return clazz;
        }
        throw new UnsupportedOperationException("不支持" + type);
    }

    private Class<?> getGenericClass(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>)type;
        }
        ParameterizedType resultType = (ParameterizedType)type;
        Type bodyType = resultType.getActualTypeArguments()[0];
        if (bodyType instanceof Class) {
            return (Class<?>)bodyType;
        }
        if (bodyType instanceof ParameterizedType) {
            return (Class<?>)((ParameterizedType)bodyType).getRawType();
        }
        throw new UnsupportedOperationException("不支持" + bodyType);
    }

    private Type getGenericType(Type type) {
        ParameterizedType resultType = (ParameterizedType)type;
        return resultType.getActualTypeArguments()[0];
    }

    public int getOpId() {
        return opId;
    }

    public Class<?> getDocReturnClass() {
        return docReturnClass;
    }

    public String getDocReturnClassName() {
        return docReturnClassName;
    }

    public List<OperationParamDescription> getOperationParamList() {
        return operationParamList;
    }

}
