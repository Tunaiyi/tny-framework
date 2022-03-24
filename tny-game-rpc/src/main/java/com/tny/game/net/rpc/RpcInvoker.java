package com.tny.game.net.rpc;

import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.exception.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 1:58 下午
 */
public class RpcInvoker {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcInvoker.class);

	/**
	 * 协议描述
	 */
	private final RpcMethod method;

	/**
	 * rpc实例
	 */
	private final RpcInstance instance;

	/**
	 * 远程服务
	 */
	private final RpcRemoteServicer servicer;

	/**
	 * 路由
	 */
	private volatile RpcRouter<Object> router;

	public RpcInvoker(RpcInstance instance, RpcMethod method) {
		this.method = method;
		this.instance = instance;
		this.servicer = instance.getServicer();
	}

	private Object getRouteParam(Object... params) {
		Object routeValue = null;
		int routableIndex = method.getRouteValueIndex();
		if (routableIndex >= 0) {
			routeValue = as(params[routableIndex]);
		}
		return routeValue;
	}

	private RpcRouter<Object> router() {
		RpcRouter<Object> router = this.router;
		if (router != null) {
			return router;
		}
		router = instance.getRouter(this.method.getRouterClass());
		if (router != null) {
			this.router = router;
		} else {
			throw new RpcInvokeException(NetResultCode.REMOTE_EXCEPTION, "调用 {} 异常, 未找到 {} RpcRouter", this.method, this.method.getRouterClass());
		}
		return router;
	}

	private long timeout() {
		RpcSetting setting = instance.getSetting();
		return this.method.getTimeout(setting.getInvokeTimeout());
	}

	public <T> Object invoke(Object... params) {
		Object routeValue = getRouteParam(params);
		List<RpcRemoteNode> nodes = servicer.getOrderRemoteNodes();
		Endpoint<?> endpoint = router().route(nodes, method, routeValue, params);
		if (endpoint == null) {
			throw new RpcInvokeException(NetResultCode.REMOTE_EXCEPTION, "调用 {} 异常, 未找到有效的远程服务节点", this.method);
		}
		long timeout = timeout();
		switch (method.getMode()) {
			case PUSH:
				push(endpoint, timeout, params);
				return null;
			case REQUEST:
				return request(endpoint, timeout, params);
		}
		throw new RpcInvokeException(NetResultCode.REMOTE_EXCEPTION, "调用 {} 异常, 非法 rpc 模式", this.method);
	}

	private Protocol protocol() {
		return Protocols.protocol(this.method.getProtocol(), this.method.getLine());
	}

	private Object request(Endpoint<?> endpoint, long timeout, Object... params) {
		RequestContext requestContext = createRequest(protocol(), params).willRespondAwaiter(timeout);
		endpoint.send(requestContext);
		MessageRespondAwaiter awaiter = requestContext.getResponseAwaiter();
		if (this.method.isAsync()) {
			switch (this.method.getReturnMode()) {
				case FUTURE:
					return getReturnFuture(awaiter);
				case VOID:
				case RESULT:
				case OBJECT:
					return null;
			}
		} else {
			Message message;
			try {
				message = awaiter.get(timeout, TimeUnit.MILLISECONDS);
				switch (this.method.getReturnMode()) {
					case RESULT:
						return RpcResults.result(ResultCodes.of(message.getCode()), message.getBody());
					case OBJECT:
						return getReturnObject(message);
					case VOID:
						return null;

				}
			} catch (Throwable e) {
				handleException(e);
			}
		}
		throw new RpcInvokeException(NetResultCode.REMOTE_EXCEPTION, "返回类型错误");
	}

	private Object getReturnFuture(MessageRespondAwaiter future) {
		if (MessageRespondAwaiter.class == this.method.getReturnClass()) {
			return future;
		}
		DefaultRpcFuture<Object> rpcFuture = new DefaultRpcFuture<>();
		future.whenComplete((message, e) -> {
			if (e != null) {
				rpcFuture.completeExceptionally(e);
			} else {
				rpcFuture.complete(RpcResults.result(ResultCodes.of(message.getCode()), message.getBody()));
			}
		});
		return rpcFuture;
	}

	private Object getReturnObject(Message message) {
		switch (this.method.getBodyType()) {
			case MESSAGE:
			case MESSAGE_HEAD:
				return message;
			case RESULT:
				return RpcResults.result(ResultCodes.of(message.getCode()), message.getBody());
			case BODY:
				return message.getBody();
			case RESULT_CODE_ID:
				return message.getCode();
			case RESULT_CODE:
				return ResultCodes.of(message.getCode());
		}
		throw new RpcInvokeException(NetResultCode.REMOTE_EXCEPTION, "返回类型错误");
	}

	private void push(Endpoint<?> endpoint, long timeout, Object... params) {
		List<Integer> parameterIndexes = method.getParameterIndexes();
		MessageContext messageContext = MessageContexts.push(protocol())
				.withBody(parameterIndexes.size() == 0 ? null : params[parameterIndexes.get(0)]);
		endpoint.send(messageContext);
		if (this.method.isAsync()) {
			return;
		}
		MessageWriteAwaiter awaiter = messageContext.getWriteAwaiter();
		try {
			awaiter.get(timeout, TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			handleException(e);
		}
	}

	private void handleException(Throwable e) {
		if (method.isSilently()) {
			LOGGER.warn("{} invoke exception", this.method, e);
		} else {
			ResultCode code = ResultCodeExceptionAide.codeOf(e, NetResultCode.REMOTE_EXCEPTION);
			throw new RpcInvokeException(code, e, "调用 {} 异常", this.method);
		}
	}

	private RequestContext createRequest(Protocol protocol, Object... params) {
		List<Integer> parameterIndexes = method.getParameterIndexes();
		switch (parameterIndexes.size()) {
			case 0:
				return MessageContexts.request(protocol);
			case 1:
				return MessageContexts.request(protocol, params[parameterIndexes.get(0)]);
			case 2:
				return MessageContexts.request(protocol, params[parameterIndexes.get(0)], params[parameterIndexes.get(1)]);
			case 3:
				return MessageContexts.request(protocol, params[parameterIndexes.get(0)], params[parameterIndexes.get(1)],
						params[parameterIndexes.get(2)]);
			case 4:
				return MessageContexts.request(protocol, params[parameterIndexes.get(0)], params[parameterIndexes.get(1)],
						params[parameterIndexes.get(2)], params[parameterIndexes.get(3)]);
			case 5:
				return MessageContexts.request(protocol, params[parameterIndexes.get(0)], params[parameterIndexes.get(1)],
						params[parameterIndexes.get(2)], params[parameterIndexes.get(3)], params[parameterIndexes.get(4)]);
			case 6:
				return MessageContexts.request(protocol, params[parameterIndexes.get(0)], params[parameterIndexes.get(1)],
						params[parameterIndexes.get(2)], params[parameterIndexes.get(3)], params[parameterIndexes.get(4)],
						params[parameterIndexes.get(5)]);
			case 7:
				return MessageContexts.request(protocol, params[parameterIndexes.get(0)], params[parameterIndexes.get(1)],
						params[parameterIndexes.get(2)], params[parameterIndexes.get(3)], params[parameterIndexes.get(4)],
						params[parameterIndexes.get(5)], params[parameterIndexes.get(6)]);
			case 8:
				return MessageContexts.request(protocol, params[parameterIndexes.get(0)], params[parameterIndexes.get(1)],
						params[parameterIndexes.get(2)], params[parameterIndexes.get(3)], params[parameterIndexes.get(4)],
						params[parameterIndexes.get(5)], params[parameterIndexes.get(6)], params[parameterIndexes.get(7)]);
			default:
				Object[] paramArray = new Object[parameterIndexes.size()];
				for (int i = 0; i < paramArray.length; i++) {
					paramArray[i] = params[parameterIndexes.get(i)];
				}
				return MessageContexts.request(protocol, paramArray);
		}
	}

}
