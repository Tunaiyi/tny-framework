package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.utils.*;
import com.tny.game.net.rpc.annotation.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.*;

import java.lang.reflect.*;
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
	private final String service;

	/**
	 * 服务名
	 */
	private final String name;

	/**
	 * 路由参数
	 */
	private int routeValueIndex = -1;

	/**
	 * java method;
	 */
	private final Method method;

	/**
	 * 路由参数
	 */
	private final List<Integer> parameterIndexes;

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
	private final RpcMode mode;

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

	public static List<RpcMethod> instanceOf(Class<?> rpcClass) {
		RpcService rpcService = rpcClass.getAnnotation(RpcService.class);
		Asserts.checkNotNull(rpcService, "{} 没有标识 {} 注解", rpcClass, RpcService.class);
		List<RpcMethod> methods = new ArrayList<>();
		for (Method method : rpcClass.getMethods()) {
			RpcCaller rpc = method.getAnnotation(RpcCaller.class);
			if (rpc == null) {
				continue;
			}
			methods.add(new RpcMethod(method, rpc, rpcService));
		}
		return methods;
	}

	private static RpcMethod instanceOf(Method method) {
		Class<?> rpcClass = method.getDeclaringClass();
		RpcService rpcService = rpcClass.getAnnotation(RpcService.class);
		RpcCaller rpc = method.getAnnotation(RpcCaller.class);
		return new RpcMethod(method, rpc, rpcService);
	}

	private RpcMethod(Method method, RpcCaller rpc, RpcService rpcService) {
		this.method = method;
		this.protocol = rpc.value();
		this.line = rpc.line();
		this.routerClass = as(rpc.router());
		if (Objects.equals(this.routerClass, RpcRouter.class)) {
			this.routerClass = as(rpcService.router());
		}
		this.service = rpcService.value();
		this.name = method.getDeclaringClass().getName() + "." + method.getName();
		this.invocation = rpc.invocation();
		this.mode = rpc.mode();
		this.silently = rpc.silently();
		this.timeout = rpc.timeout();
		int index = 0;
		List<Integer> parameterIndexes = new ArrayList<>();
		for (Parameter parameter : method.getParameters()) {
			if (routeValueIndex < 0) {
				RpcRoutable routeParam = parameter.getAnnotation(RpcRoutable.class);
				if (routeParam != null) {
					routeValueIndex = index;
				}
			}
			RpcIgnore ignore = parameter.getAnnotation(RpcIgnore.class);
			if (ignore == null) {
				parameterIndexes.add(index);
			}
			index++;
		}
		this.returnClass = method.getReturnType();
		this.returnMode = RpcReturnMode.typeOf(returnClass);
		Asserts.checkArgument(returnMode != null, "{} 返回类型 {} 是非法返回类型", method, returnClass);
		Asserts.checkArgument(this.returnMode.isCanInvokeBy(mode), "{} 返回类型 {} 是使用 {} Rpc 模式", method, returnClass, mode);
		this.parameterIndexes = ImmutableList.copyOf(parameterIndexes);

		if (!this.mode.checkParamsSize(this.parameterIndexes.size())) {
			throw new IllegalArgumentException(format("{} mode 为 {}, 请求参数数量必须在 {} 到 {} 个之间",
					method, mode, mode.getMinParamSizeLimit(), mode.getMaxParamSizeLimit()));
		}

		Class<?> bodyClass = returnMode.findBodyClass(method);
		this.bodyType = RpcBodyType.typeOf(method, bodyClass);
	}

	public String getName() {
		return name;
	}

	public String getService() {
		return service;
	}

	public int getProtocol() {
		return protocol;
	}

	public int getLine() {
		return line;
	}

	public RpcMode getMode() {
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

	public List<Integer> getParameterIndexes() {
		return parameterIndexes;
	}

	public int getRouteValueIndex() {
		return routeValueIndex;
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
		return new ToStringBuilder(this)
				.append("service", service)
				.append("name", name)
				.toString();
	}

}
