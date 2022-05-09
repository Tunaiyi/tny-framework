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
import org.slf4j.*;

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
public class RpcMethod {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcMethod.class);

    /**
     * 服务名
     */
    private final RpcServiceType serviceType;

    private boolean proxy;

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
    private final List<RpcParamDescription> parameters;

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
     * RPC 返回类型
     */
    private final RpcReturnMode returnMode;

    /**
     * 返回 body 类型
     */
    private final RpcBodyType bodyType;

    /**
     * 路由类型
     */
    private Class<? extends RpcRouter<?>> routerClass;

    /**
     * 异步
     */
    private final RpcInvocation invocation;

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

    public static List<RpcMethod> instanceOf(Class<?> rpcClass) {
        RpcService rpcService = rpcClass.getAnnotation(RpcService.class);
        Asserts.checkNotNull(rpcService, "{} 没有标识 {} 注解", rpcClass, RpcService.class);
        List<RpcMethod> methods = new ArrayList<>();
        for (Method method : rpcClass.getMethods()) {
            instanceOf(method).ifPresent(methods::add);
        }
        return methods;
    }

    private static Optional<RpcMethod> instanceOf(Method method) {
        Class<?> rpcClass = method.getDeclaringClass();
        RpcService rpcService = rpcClass.getAnnotation(RpcService.class);
        RpcRemoteOptions options = rpcClass.getAnnotation(RpcRemoteOptions.class);
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
        return Optional.of(new RpcMethod(method, profile, rpcService, options));
    }

    private RpcMethod(Method method, RpcProfile profile, RpcService rpcService, RpcRemoteOptions options) {
        this.method = method;
        this.protocol = profile.getProtocol();
        this.line = profile.getLine();
        if (options == null) {
            options = rpcService.options();
        }
        this.routerClass = as(options.router());
        if (Objects.equals(this.routerClass, RpcRouter.class)) {
            this.routerClass = as(rpcService.router());
        }
        this.proxy = StringUtils.isNoneBlank(rpcService.proxyService());
        this.mode = profile.getMode();
        this.serviceType = RpcServiceTypes.checkService(rpcService.value());
        this.name = method.getDeclaringClass().getName() + "." + method.getName();
        this.invocation = options.invocation();
        this.silently = options.silently();
        this.timeout = options.timeout();
        this.returnClass = method.getReturnType();
        this.returnMode = RpcReturnMode.typeOf(returnClass);
        Asserts.checkArgument(returnMode != null, "{} 返回类型 {} 是非法返回类型", method, returnClass);
        Asserts.checkArgument(this.returnMode.isCanInvokeBy(mode), "{} 返回类型 {} 是使用 {} Rpc 模式", method, returnClass, mode);
        Class<?> bodyClass = returnMode.findBodyClass(method);
        this.bodyType = RpcBodyType.typeOf(method, bodyClass);

        List<RpcParamDescription> paramDescriptions = new ArrayList<>();
        Class<?>[] parameterClasses = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        ParamIndexCreator indexCreator = new ParamIndexCreator(method);
        int bodySize = 0;
        int paramSize = 0;
        int maxIndex = -1;
        for (int index = 0; index < parameterClasses.length; index++) {
            Class<?> paramClass = parameterClasses[index];
            List<Annotation> annotations = ImmutableList.copyOf(parameterAnnotations[index]);
            RpcParamDescription paramDesc = new RpcParamDescription(this, paramClass, annotations, indexCreator);
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
            throw new IllegalArgumentException(format("{} 方法 {} 参数只能存在一个", method, RpcParam.class, RpcBody.class));
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

    public String getService() {
        return serviceType.getService();
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

    public boolean isAsync() {
        return returnMode.checkInvocation(this.invocation) == RpcInvocation.ASYNC;
    }

    public boolean isSilently() {
        return silently;
    }

    public Class<? extends RpcRouter<?>> getRouterClass() {
        return routerClass;
    }

    public List<RpcParamDescription> getParameters() {
        return parameters;
    }

    public Class<?> getReturnClass() {
        return returnClass;
    }

    public RpcReturnMode getReturnMode() {
        return returnMode;
    }

    public RpcBodyType getBodyType() {
        return bodyType;
    }

    public Method getMethod() {
        return method;
    }

    public long getTimeout(long defaultTimeout) {
        return this.timeout > 0 ? this.timeout : defaultTimeout;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("service", serviceType).append("name", name).toString();
    }

    public RpcInvokeParams getParams(Object[] paramValues) throws CommandException {
        RpcInvokeParams params = new RpcInvokeParams(this.messageParamSize);
        for (RpcParamDescription desc : this.parameters) {
            Object value = paramValues[desc.getIndex()];
            if (desc.isRequire() && value == null) {
                throw new NullPointerException(format("{} 第 {} 参数为 null", this.method, desc.getIndex()));
            }
            if (this.proxy) {
                params.setTo(serviceType);
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
                    // case TO_SERVICE:
                    //     params.setTo(as(value, RpcServicer.class));
                    //     break;
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
