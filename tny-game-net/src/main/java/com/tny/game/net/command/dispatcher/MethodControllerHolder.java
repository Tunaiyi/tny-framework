package com.tny.game.net.command.dispatcher;

import com.google.common.collect.*;
import com.tny.game.common.number.*;
import com.tny.game.common.reflect.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.expr.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.StringAide.*;

/**
 * @author KGTny
 */
public final class MethodControllerHolder extends ControllerHolder {

	// private static final String DEV_TIMEOUT_CHECK = "tny.server.dev.timeout.check";

	protected static final ConcurrentHashMap<String, ExprHolder> cache = new ConcurrentHashMap<>();

	/**
	 * 执行对象
	 */
	private final Object executor;

	/**
	 * 执行方法
	 */
	private final MethodAccessor method;

	/**
	 * 方法名字
	 */
	private final String name;

	/**
	 * 控制器操作配置
	 */
	private final ClassControllerHolder classController;

	/**
	 * 参数类型
	 */
	private List<ParamDesc> parameterDescs;

	/**
	 * 参数注解列表
	 */
	private Map<Class<?>, List<Annotation>> paramAnnotationsMap;

	/**
	 * 方法注解
	 */
	private Map<Class<?>, Annotation> methodAnnotationMap;

	/**
	 * 执行list
	 */
	private PluginContext beforeContext;

	/**
	 * 执行list
	 */
	private PluginContext afterContext;

	/**
	 * 构造方法
	 *
	 * @param executor 调用的执行对象
	 * @param method   方法
	 */
	protected MethodControllerHolder(final Object executor, final MessageDispatcherContext context, ExprHolderFactory exprHolderFactory,
			final ClassControllerHolder classController, final MethodAccessor method, final Controller controller) {
		super(executor, context, controller,
				method.getJavaMethod().getAnnotationsByType(BeforePlugin.class),
				method.getJavaMethod().getAnnotationsByType(AfterPlugin.class),
				method.getJavaMethod().getAnnotation(AuthenticationRequired.class),
				method.getJavaMethod().getAnnotation(MessageFilter.class),
				method.getJavaMethod().getAnnotation(AppProfile.class),
				method.getJavaMethod().getAnnotation(ScopeProfile.class), exprHolderFactory);
		try {
			this.classController = classController;
			this.method = method;
			this.executor = executor;
			StringBuilder nameBuilder = new StringBuilder();
			nameBuilder.append(executor.getClass().getSimpleName()).append("#").append(method.getName());
			Class<?>[] parameterClasses = method.getJavaMethod().getParameterTypes();
			List<ParamDesc> parameterDescs = new ArrayList<>();
			Annotation[][] parameterAnnotations = method.getJavaMethod().getParameterAnnotations();
			LocalNum<Integer> counter = new LocalNum<>(0);
			if (parameterClasses.length > 0) {
				for (int index = 0; index < parameterClasses.length; index++) {
					Class<?> paramClass = parameterClasses[index];
					List<Annotation> annotations = ImmutableList.copyOf(parameterAnnotations[index]);
					ParamDesc paramDesc = new ParamDesc(this, paramClass, annotations, counter, exprHolderFactory);
					parameterDescs.add(paramDesc);
					if (index > 0) {
						nameBuilder.append(", ");
					} else {
						nameBuilder.append("(");
					}
					nameBuilder.append(paramClass.getSimpleName());
				}
			}
			nameBuilder.append(")");
			this.name = nameBuilder.toString();
			this.initMethodAnnotation(method.getJavaMethod().getAnnotations());
			Map<Class<?>, List<Annotation>> annotationsMap = new HashMap<>();
			Set<Class<?>> annotationClassSet = new HashSet<>();
			for (Annotation[] paramAnnotations : parameterAnnotations) {
				for (Annotation paramAnnotation : paramAnnotations) {
					if (paramAnnotation != null) {
						annotationClassSet.add(paramAnnotation.annotationType());
					}
				}
			}
			for (Class<?> clazz : annotationClassSet) {
				List<Annotation> indexAnnotationList = new ArrayList<>();
				for (Annotation[] paramAnnotations : parameterAnnotations) {
					Annotation select = null;
					for (Annotation paramAnnotation : paramAnnotations) {
						if (clazz.isInstance(paramAnnotation)) {
							select = paramAnnotation;
							break;
						}
					}
					indexAnnotationList.add(select);
				}
				annotationsMap.put(clazz, Collections.unmodifiableList(indexAnnotationList));
			}
			this.paramAnnotationsMap = ImmutableMap.copyOf(annotationsMap);
			this.parameterDescs = ImmutableList.copyOf(parameterDescs);
			for (ControllerPluginHolder plugin : this.getControllerBeforePlugins())
				this.beforeContext = this.putPlugin(this.beforeContext, plugin);
			for (ControllerPluginHolder plugin : this.getControllerAfterPlugins())
				this.afterContext = this.putPlugin(this.afterContext, plugin);
		} catch (Exception e) {
			throw new IllegalArgumentException(format("{}.{} 方法解析失败", method.getDeclaringClass(), method.getName()), e);
		}
	}

	private void initMethodAnnotation(Annotation[] annotations) {
		Map<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<>();
		for (Annotation annotation : annotations)
			annotationMap.put(annotation.getClass(), annotation);
		this.methodAnnotationMap = Collections.unmodifiableMap(annotationMap);
	}

	@Override
	public Set<MessageMode> getMessageModes() {
		if (this.messageModes != null) {
			return this.messageModes;
		}
		return this.classController.getMessageModes();
	}

	public List<ParamDesc> getParameterDescs() {
		return this.parameterDescs;
	}

	public int getParametersSize() {
		return this.parameterDescs.size();
	}

	private PluginContext putPlugin(PluginContext context, ControllerPluginHolder plugin) {
		if (context == null) {
			context = new PluginContext(plugin);
		}
		context.setNext(new PluginContext(plugin));
		return context;
	}

	@Override
	public Class<?> getControllerClass() {
		return this.executor.getClass();
	}

	// private static ExprHolder formula(String formula) {
	//     ExprHolder holder = cache.get(formula);
	//     if (holder != null)
	//         return holder;
	//     holder = MvelFormulaFactory.create(formula, ExprType.EXPRESSION);
	//     return ObjectAide.defaultIfNull(cache.putIfAbsent(formula, holder), holder);
	// }

	public Object getParameterValue(int index, NetTunnel<?> tunnel, Message message, Object body) throws CommandException {
		if (index >= this.parameterDescs.size()) {
			throw new CommandException(NetResultCode.SERVER_EXECUTE_EXCEPTION,
					format("{} 获取 index 为 {} 的ParamDesc越界, index < {}", this, index, this.parameterDescs.size()));
		}
		ParamDesc desc = this.parameterDescs.get(index);
		if (desc == null) {
			throw new CommandException(NetResultCode.SERVER_EXECUTE_EXCEPTION, format("{} 获取 index 为 {} 的ParamDesc为null", this, index));
		}
		return desc.getValue(tunnel, message, body);
	}

	/**
	 * 获取处理方法名称
	 *
	 * @return 返回处理方法名称
	 */
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getId() {
		return this.controller != null ? this.controller.value() : -1;
	}

	/**
	 * 调用消息处理
	 *
	 * @param params 调用参数
	 * @return 返回值
	 * @throws IllegalArgumentException  参数异常
	 * @throws InvocationTargetException 调用异常
	 */
	private Object invoke(final Object[] params) throws IllegalArgumentException, InvocationTargetException {
		return this.method.invoke(this.executor, params);
	}

	@Override
	public boolean isUserGroup(String group) {
		return this.userGroups != null ? super.isUserGroup(group) : this.classController.isUserGroup(group);
	}

	@Override
	public boolean isActiveByAppType(String appType) {
		return this.appTypes != null ? super.isActiveByAppType(appType) : this.classController.isActiveByAppType(appType);
	}

	@Override
	public boolean isActiveByScope(String scope) {
		return this.scopes != null ? super.isActiveByAppType(scope) : this.classController.isActiveByScope(scope);
	}

	@Override
	public boolean isAuth() {
		return this.auth != null ? super.isAuth() : this.classController.isAuth();
	}

	@Override
	public Class<? extends AuthenticateValidator<?>> getAuthValidator() {
		return this.auth != null ? super.getAuthValidator() : this.classController.getAuthValidator();
	}

	@Override
	protected List<ControllerPluginHolder> getControllerBeforePlugins() {
		return this.beforePlugins != null && !this.beforePlugins.isEmpty() ? Collections.unmodifiableList(this.beforePlugins)
				: this.classController.getControllerBeforePlugins();
	}

	@Override
	protected List<ControllerPluginHolder> getControllerAfterPlugins() {
		return this.afterPlugins != null && !this.afterPlugins.isEmpty() ? Collections.unmodifiableList(this.afterPlugins)
				: this.classController.getControllerAfterPlugins();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A extends Annotation> List<A> getParamsAnnotationsByType(Class<A> clazz) {
		List<Annotation> annotations = this.paramAnnotationsMap.get(clazz);
		if (annotations == null) {
			return new ArrayList<>();
		}
		return (List<A>)annotations;
	}

	@Override
	public boolean isParamsAnnotationExist(Class<? extends Annotation> clazz) {
		return this.paramAnnotationsMap.containsKey(clazz);
	}

	@Override
	public List<Annotation> getParamAnnotationsByIndex(int index) {
		return this.parameterDescs.get(index).getParamAnnotations();
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
		return this.classController.getAnnotation(annotationClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass) {
		return (A)this.methodAnnotationMap.get(annotationClass);
	}

	public Set<Class<?>> getParamAnnotationClass() {
		return this.paramAnnotationsMap.keySet();
	}

	protected Object invoke(NetTunnel<?> tunnel, Message message, MessageCommandContext context) throws Exception {
		// 获取调用方法的参数类型
		Object[] param = new Object[this.getParametersSize()];
		Object body = message.bodyAs(Object.class);
		for (int index = 0; index < param.length; index++) {
			param[index] = this.getParameterValue(index, tunnel, message, body);
		}
		return this.invoke(param);
	}

	protected void beforeInvoke(Tunnel<?> tunnel, Message message, MessageCommandContext context) {
		if (this.beforeContext == null) {
			return;
		}
		this.beforeContext.execute(tunnel, message, context);
	}

	protected void afterInvoke(Tunnel<?> tunnel, Message message, ControllerMessageCommandContext context) {
		if (this.afterContext == null) {
			return;
		}
		this.afterContext.execute(tunnel, message, context);
	}

	@Override
	public String toString() {
		return "MethodHolder [" + getControllerClass() + "." + getName() + "]";
	}

	private static class ParamDesc {

		/* body 为List时 索引 */
		private int index;

		/* body 为Map时 key */
		private String name;

		private ExprHolder formula;

		private ParamType paramType = ParamType.NONE;

		/* 参数类型 */
		private Class<?> paramClass;

		/* 参数注解 */
		private MsgParam msgParam;

		private boolean require;

		private List<Annotation> paramAnnotations;

		private MethodControllerHolder holder;

		private ParamDesc(MethodControllerHolder holder, Class<?> paramClass, List<Annotation> paramAnnotations, LocalNum<Integer> indexCounter,
				ExprHolderFactory exprHolderFactory) {
			this.holder = holder;
			this.paramClass = paramClass;
			this.paramAnnotations = paramAnnotations;
			this.require = true;
			if (paramClass == Endpoint.class) {
				this.paramType = ParamType.ENDPOINT;
			} else if (paramClass == Session.class) {
				this.paramType = ParamType.SESSION;
			} else if (paramClass == Tunnel.class) {
				this.paramType = ParamType.TUNNEL;
			} else if (paramClass == Message.class) {
				this.paramType = ParamType.MESSAGE;
			} else if (ResultCode.class.isAssignableFrom(this.paramClass)) {
				this.paramType = ParamType.CODE;
			} else {
				for (Annotation annotation : this.paramAnnotations) {
					if (annotation.annotationType() == MsgBody.class) {
						this.paramType = ParamType.BODY;
						this.require = ((MsgBody)annotation).require();
					} else if (annotation.annotationType() == MsgParam.class) {
						this.msgParam = (MsgParam)annotation;
						this.require = this.msgParam.require();
						if (StringUtils.isNoneBlank(this.msgParam.value())) {
							this.name = this.msgParam.value();
							this.formula = exprHolderFactory.create("_body." + this.name.trim());
							this.paramType = ParamType.KEY_PARAM;
						} else {
							if (this.msgParam.index() >= 0) {
								this.index = this.msgParam.index();
							} else {
								this.index = indexCounter.intValue();
								indexCounter.add(1);
							}
							this.paramType = ParamType.INDEX_PARAM;
						}
					} else if (annotation.annotationType() == UserID.class) {
						this.paramType = ParamType.UserID;
					} else if (annotation.annotationType() == MsgCode.class) {
						if (paramClass == Integer.class || paramClass == int.class) {
							this.paramType = ParamType.CODE_NUM;
						} else if (ResultCode.class.isAssignableFrom(this.paramClass)) {
							this.paramType = ParamType.CODE;
						} else {
							throw new IllegalArgumentException(format("{} 类型参数只能是 {} {} {}, 无法为 {}",
									MsgCode.class, Integer.class, int.class, ResultCode.class));
						}
					}
				}
			}
		}

		private Object getValue(NetTunnel<?> tunnel, Message message, Object body) throws CommandException {
			boolean require = this.require;
			if (body == null) {
				body = message.bodyAs(Object.class);
			}
			MessageHead head = message.getHead();
			Object value = null;
			switch (this.paramType) {
				case MESSAGE:
					value = message;
					break;
				case ENDPOINT: {
					value = tunnel.getEndpoint();
					break;
				}
				case SESSION: {
					value = tunnel.getEndpoint();
					if (value instanceof Session) {
						break;
					}
					throw new NullPointerException(format("{} session is null", tunnel));
				}
				case CLIENT: {
					value = tunnel.getEndpoint();
					if (value instanceof Client) {
						break;
					}
					throw new NullPointerException(format("{} session is null", tunnel));
				}
				case TUNNEL:
					value = tunnel;
					break;
				case BODY:
					value = body;
					break;
				case UserID:
					value = tunnel.getUserId();
					break;
				case INDEX_PARAM:
					try {
						if (body == null) {
							if (require) {
								throw new NullPointerException(format("{} 收到消息体为 null"));
							} else {
								break;
							}
						}
						if (body instanceof List) {
							value = ((List<?>)body).get(this.index);
						} else if (body.getClass().isArray()) {
							value = Array.get(body, this.index);
						} else {
							throw new CommandException(NetResultCode.SERVER_EXECUTE_EXCEPTION,
									format("{} 收到消息体为 {}, 不可通过index获取", this.holder, body.getClass()));
						}
					} catch (CommandException e) {
						throw e;
					} catch (Throwable e) {
						throw new CommandException(NetResultCode.SERVER_EXECUTE_EXCEPTION, format("{} 调用异常", this.holder), e);
					}
					break;
				case KEY_PARAM:
					if (body == null) {
						if (require) {
							throw new NullPointerException(format("{} 收到消息体为 null"));
						} else {
							break;
						}
					}
					if (body instanceof Map) {
						value = ((Map)body).get(this.name);
					} else {
						value = this.formula.createExpr()
								.put("_body", body)
								.execute(this.paramClass);
					}
					break;
				case CODE:
					value = ResultCodes.of(head.getCode());
					break;
				case CODE_NUM:
					value = head.getCode();
					break;
			}
			if (value != null && !this.paramClass.isInstance(value)) {
				value = ObjectAide.convertTo(value, this.paramClass);
			}
			return value;
		}

		private List<Annotation> getParamAnnotations() {
			return this.paramAnnotations;
		}

	}

}
