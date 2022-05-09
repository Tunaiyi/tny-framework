package com.tny.game.doc.controller;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.common.result.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import com.tny.game.net.base.*;
import com.tny.game.net.rpc.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.Future;

@XStreamAlias("operation")
public class OperationConfiger {

    @XStreamAsAttribute
    private String methodName;

    @XStreamAsAttribute
    private int opId;

    @XStreamAsAttribute
    private String des;

    @XStreamAsAttribute
    private String text;

    @XStreamAsAttribute
    private String returnType;

    @XStreamAsAttribute
    private String returnDes;

    private ParamList paramList;

    @XStreamAlias("paramList")
    private static class ParamList {

        @XStreamAsAttribute
        @XStreamAlias("class")
        private String type = "list";

        @XStreamImplicit(itemFieldName = "param")
        private List<VarConfiger> paramList;

    }

    public OperationConfiger(FunDocHolder holder, TypeFormatter typeFormatter) {
        this.opId = holder.getOpId();
        this.returnDes = holder.getFunDoc().returnDes();
        this.methodName = holder.getMethod().getName();
        this.paramList = new ParamList();
        this.des = holder.getFunDoc().des();
        this.text = holder.getFunDoc().text();
        if (StringUtils.isBlank(this.text)) {
            this.text = this.des;
        }
        List<VarConfiger> paramList = new ArrayList<VarConfiger>();
        for (VarDocHolder varDocHolder : holder.getParamList()) {
            paramList.add(new VarConfiger(varDocHolder, typeFormatter));
        }
        this.paramList.paramList = Collections.unmodifiableList(paramList);
        Class<?> docReturn = holder.getFunDoc().returnType();
        if (docReturn == null || docReturn == Object.class) {
            Method method = holder.getMethod();
            this.returnType = returnType(method.getGenericReturnType()).getSimpleName();
        } else {
            this.returnType = docReturn.getSimpleName();
        }
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
            if (RpcResult.class.isAssignableFrom(clazz) || RpcFuture.class.isAssignableFrom(clazz)) {
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

    public String getDes() {
        return des;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getReturnDes() {
        return returnDes;
    }

    public int getOpId() {
        return opId;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<VarConfiger> getParamList() {
        return paramList.paramList;
    }

}
