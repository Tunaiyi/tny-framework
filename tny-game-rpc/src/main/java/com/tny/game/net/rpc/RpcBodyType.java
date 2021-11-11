package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.result.*;
import com.tny.game.net.command.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 4:39 下午
 */
public enum RpcBodyType {

	VOID(null, Void.class, void.class),

	MESSAGE(null, Message.class),

	MESSAGE_HEAD(null, MessageHead.class),

	RESULT(null, RpcResult.class),

	RESULT_CODE_ID(RpcResultCode.class, Integer.class, int.class),

	RESULT_CODE(null, ResultCode.class),

	BODY(RpcBody.class),

	//
	;

	private final Class<? extends Annotation> bodyAnnotation;

	private final List<Class<?>> bodyClasses;

	RpcBodyType(Class<? extends Annotation> bodyAnnotation, Class<?>... bodyTypes) {
		this.bodyAnnotation = bodyAnnotation;
		this.bodyClasses = ImmutableList.copyOf(bodyTypes);
	}

	public static RpcBodyType typeOf(Method method, Class<?> returnClass) {
		for (RpcBodyType type : RpcBodyType.values()) {
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
		throw new IllegalArgumentException(format("{}  非法返回类型 {}", method, returnClass));
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
