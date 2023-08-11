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
import com.tny.game.common.utils.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/27 3:47 下午
 */
public class RpcRemoteMethod {

    /**
     * 目标
     */
    private final ContactType serviceType;

    /**
     * 目标
     */
    private RpcServiceType targetServiceType;

    /**
     * 转发
     */
    private RpcServiceType forwardServiceType;

    /**
     * 服务名
     */
    private final String name;

    /**
     * java method;
     */
    private final Method method;

    /**
     * 路由参数
     */
    private final List<RpcRemoteParamDescription> parameters;

    /**
     * 协议 id
     */
    private final int protocol;

    /**
     * 请求线
     */
    private final int line;

    /**
     * 调用模式
     */
    private final MessageMode mode;

    /**
     * 返回值类
     */
    private final Class<?> returnClass;

    /**
     * RPC 返回模式
     */
    private final RpcReturnMode returnMode;

    /**
     * 返回 body 类型
     */
    private final RpcBodyMode bodyMode;

    /**
     * 路由类型
     */
    private final Class<? extends RpcRouter> routerClass;

    /**
     * 异步
     */
    private final RpcInvokeMode invokeMode;

    /**
     * 安静模式
     */
    private final boolean silently;

    /**
     * 超时
     */
    private final long timeout;

    /**
     * 消息参数大小
     */
    private int messageParamSize;

    public static List<RpcRemoteMethod> methodsOf(Class<?> rpcClass) {
        RpcRemoteService rpcService = rpcClass.getAnnotation(RpcRemoteService.class);
        Asserts.checkNotNull(rpcService, "{} 没有标识 {} 注解", rpcClass, RpcRemoteService.class);
        List<RpcRemoteMethod> methods = new ArrayList<>();
        for (Method method : rpcClass.getMethods()) {
            methodOf(method).ifPresent(methods::add);
        }
        return methods;
    }

    private static Optional<RpcRemoteMethod> methodOf(Method method) {
        Class<?> rpcClass = method.getDeclaringClass();
        RpcRemoteService rpcService = rpcClass.getAnnotation(RpcRemoteService.class);
        RpcProfile profile = RpcProfile.oneOf(method);
        if (profile == null) {
            return Optional.empty();
        }
        if (method.getReturnType() == RpcReturn.class) {
            return Optional.empty();
        }
        if (method.isBridge()) {
            return Optional.empty();
        }
        return Optional.of(new RpcRemoteMethod(method, profile, rpcService));
    }

    private RpcRemoteMethod(Method method, RpcProfile profile, RpcRemoteService rpcService) {
        this.method = method;
        this.protocol = profile.getProtocol();
        this.line = profile.getLine();
        RpcRemoteOptions options = method.getAnnotation(RpcRemoteOptions.class);
        if (options == null) {
            options = rpcService.options();
        }
        this.routerClass = as(options.router());
        var forward = StringUtils.isNoneBlank(rpcService.forwardService());
        this.mode = profile.getMode();
        serviceType = ContactTypes.checkGroup(rpcService.value());
        if (forward) {
            this.targetServiceType = RpcServiceTypes.checkService(rpcService.value());
            this.forwardServiceType = RpcServiceTypes.checkService(rpcService.forwardService());
        }
        this.name = method.getDeclaringClass().getName() + "." + method.getName();
        this.invokeMode = options.mode();
        this.silently = options.silently();
        this.timeout = options.timeout();
        this.returnClass = method.getReturnType();
        this.returnMode = RpcReturnMode.typeOf(returnClass);
        Asserts.checkArgument(returnMode != null, "{} 返回类型 {} 是非法返回类型", method, returnClass);
        Asserts.checkArgument(this.returnMode.isCanInvokeBy(mode), "{} 返回类型 {} 是使用 {} Rpc 模式", method, returnClass, mode);
        Class<?> bodyClass = returnMode.findBodyClass(method);
        this.bodyMode = RpcBodyMode.typeOf(method, bodyClass);

        List<RpcRemoteParamDescription> paramDescriptions = new ArrayList<>();
        Class<?>[] parameterClasses = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        ParamIndexCreator indexCreator = new ParamIndexCreator(method);
        int bodySize = 0;
        int paramSize = 0;
        int maxIndex = -1;
        for (int index = 0; index < parameterClasses.length; index++) {
            Class<?> paramClass = parameterClasses[index];
            List<Annotation> annotations = ImmutableList.copyOf(parameterAnnotations[index]);
            RpcRemoteParamDescription paramDesc = new RpcRemoteParamDescription(this, paramClass, annotations, indexCreator);
            paramDescriptions.add(paramDesc);
            if (paramDesc.getMode() == ParamMode.PARAM) {
                paramSize++;
                if (paramDesc.getIndex() > maxIndex) {
                    maxIndex = paramDesc.getIndex();
                }
            }
            if (paramDesc.getMode() == ParamMode.BODY) {
                bodySize++;
            }
        }
        if (bodySize > 0 && paramSize > 0) {
            throw new IllegalArgumentException(format("{} 方法不可同时使用 {} 与 {}参数", method, RpcParam.class, RpcBody.class));
        }
        if (bodySize > 1) {
            throw new IllegalArgumentException(format("{} 方法 {} 参数只能存在一个", method, RpcBody.class));
        }
        if (paramSize > 0) {
            if (maxIndex >= paramSize) {
                throw new IllegalArgumentException(format("{} 方法 参数最大index {} 需要 < 参数个数 {}", method, maxIndex, paramSize));
            }
            this.messageParamSize = paramSize;
        }
        if (bodySize == 1) {
            this.messageParamSize = 1;
        }
        this.parameters = ImmutableList.copyOf(paramDescriptions);
    }

    public String getName() {
        return name;
    }

    public int getProtocol() {
        return protocol;
    }

    public int getLine() {
        return line;
    }

    public MessageMode getMode() {
        return mode;
    }

    public RpcInvokeMode getInvokeMode() {
        return invokeMode;
    }

    public ContactType getServiceType() {
        return serviceType;
    }

    public boolean isAsync() {
        return returnMode.checkInvocation(this.invokeMode) == RpcInvokeMode.ASYNC;
    }

    public boolean isSilently() {
        return silently;
    }

    public Class<? extends RpcRouter> getRouterClass() {
        return routerClass;
    }

    public List<RpcRemoteParamDescription> getParameters() {
        return parameters;
    }

    public Class<?> getReturnClass() {
        return returnClass;
    }

    public RpcReturnMode getReturnMode() {
        return returnMode;
    }

    public RpcBodyMode getBodyMode() {
        return bodyMode;
    }

    public Method getMethod() {
        return method;
    }

    public long getTimeout(long defaultTimeout) {
        return this.timeout > 0 ? this.timeout : defaultTimeout;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("service", targetServiceType).append("name", name).toString();
    }

    private boolean isForward() {
        return this.forwardServiceType != null;
    }

    public RpcRemoteInvokeParams getParams(Object[] paramValues) throws RpcInvokeException {
        RpcRemoteInvokeParams params = new RpcRemoteInvokeParams(this.messageParamSize);
        var forward = this.isForward();
        params.setForward(forward);
        for (int index = 0; index < paramValues.length; index++) {
            RpcRemoteParamDescription desc = this.parameters.get(index);
            Object value = paramValues[index];
            if (desc.isRequire() && value == null) {
                throw new NullPointerException(format("{} 第 {} 参数为 null", this.method, desc.getIndex()));
            }
            if (forward) {
                params.setTo(targetServiceType);
            }
            if (desc.getAnnotationHolder().getAnnotation(RpcRouteParam.class) != null) {
                params.setRouteValue(value);
            }
            if (desc.isRequire()) {
                switch (desc.getMode()) {
                    case PARAM:
                        params.setParams(desc.getIndex(), value);
                        break;
                    case CODE:
                    case CODE_NUM:
                        params.setCode(value);
                        break;
                    case HEADER:
                        params.putHeader(as(value));
                        break;
                    case FROM_SERVICE:
                        params.setFrom(as(value, RpcServicer.class));
                        break;
                    case TO_SERVICE:
                        params.setTo(as(value, RpcServicer.class));
                        break;
                    case SENDER:
                        params.setSender(as(value));
                        break;
                    case RECEIVER:
                        params.setReceiver(as(value));
                        break;
                    case BODY:
                        params.setBody(value);
                        break;
                    case IGNORE:
                        break;
                    default:
                        throw new IllegalArgumentException(format("不支持 {} 参数", desc.getMode()));
                }
            }
        }
        return params;
    }

}
