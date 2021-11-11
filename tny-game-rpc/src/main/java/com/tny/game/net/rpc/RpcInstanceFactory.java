package com.tny.game.net.rpc;

import com.tny.game.common.result.*;
import com.tny.game.net.rpc.annotation.*;
import com.tny.game.net.rpc.exception.*;
import javassist.util.proxy.*;

import java.lang.reflect.Method;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 6:11 下午
 */
public class RpcInstanceFactory {

	private RpcSetting setting;

	private RpcRemoteService rpcRemoteService;

	private RpcRouteManager rpcRouteManager;

	public RpcInstanceFactory(RpcSetting setting, RpcRemoteService rpcRemoteService, RpcRouteManager rpcRouteManager) {
		this.setting = setting;
		this.rpcRemoteService = rpcRemoteService;
		this.rpcRouteManager = rpcRouteManager;
	}

	public <T> T create(Class<?> rpcClass) {
		ProxyFactory factory = new ProxyFactory();
		RpcInstance instance = createRpcInstance(rpcClass);
		factory.setInterfaces(new Class[]{rpcClass});
		factory.setFilter((method) -> instance.invoker(method) != null);
		Class<?> c = factory.createClass();
		MethodHandler handler = (self, method, proceed, args) -> {
			RpcInvoker invoker = instance.invoker(method);
			return invoker.invoke(args);
		};
		Object proxy;
		try {
			proxy = c.newInstance();
		} catch (Throwable e) {
			throw new RpcException(ResultCode.FAILURE, e);
		}
		((Proxy)proxy).setHandler(handler);
		return as(proxy);
	}

	private RpcInstance createRpcInstance(Class<?> rpcClass) {
		List<RpcMethod> methods = RpcMethod.instanceOf(rpcClass);
		RpcService rpcService = rpcClass.getAnnotation(RpcService.class);
		RpcRemoteServicer remoteServicer = rpcRemoteService.loadOrCreate(rpcService.value());
		RpcInstance instance = new RpcInstance(rpcClass, this.setting, remoteServicer, this.rpcRouteManager);
		Map<Method, RpcInvoker> invokerMap = new HashMap<>();
		for (RpcMethod method : methods) {
			RpcInvoker invoker = new RpcInvoker(instance, method);
			invokerMap.put(method.getMethod(), invoker);
		}
		instance.setInvokerMap(invokerMap);
		return instance;
	}

	public RpcInstanceFactory setSetting(RpcSetting setting) {
		this.setting = setting;
		return this;
	}

	public RpcInstanceFactory setRpcRemoteService(RpcRemoteService rpcRemoteService) {
		this.rpcRemoteService = rpcRemoteService;
		return this;
	}

	public RpcInstanceFactory setRpcRouteManager(RpcRouteManager rpcRouteManager) {
		this.rpcRouteManager = rpcRouteManager;
		return this;
	}

}
