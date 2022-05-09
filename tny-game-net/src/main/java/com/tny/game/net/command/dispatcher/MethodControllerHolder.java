package com.tny.game.net.command.dispatcher;

import com.google.common.collect.*;
import com.tny.game.common.reflect.*;
import com.tny.game.expr.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * @author KGTny
 */
public final class MethodControllerHolder extends ControllerHolder {

    //	private static final ConcurrentHashMap<String, ExprHolder> cache = new ConcurrentHashMap<>();

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
    private final List<ControllerParamDescription> paramDescriptions;

    /**
     * 参数注解列表
     */
    private final Map<Class<?>, List<Annotation>> indexParamAnnotationsMap;

    /**
     * 方法注解
     */
    private final AnnotationHolder methodAnnotationHolder;

    /**
     * 返回类型
     */
    private final Class<?> returnType;

    /**
     * 执行list
     */
    private PluginChain beforeContext;

    /**
     * 执行list
     */
    private PluginChain afterContext;

    /**
     * 控制器操作配置
     */
    private final RpcProfile rpcProfile;

    /**
     * 构造方法
     *
     * @param executor 调用的执行对象
     * @param method   方法
     */
    MethodControllerHolder(final Object executor, final MessageDispatcherContext context, ExprHolderFactory exprHolderFactory,
            final ClassControllerHolder classController, final MethodAccessor method, final RpcProfile rpcProfile) {
        super(executor, context,
                method.getJavaMethod().getAnnotationsByType(BeforePlugin.class),
                method.getJavaMethod().getAnnotationsByType(AfterPlugin.class),
                method.getJavaMethod().getAnnotation(AuthenticationRequired.class),
                method.getJavaMethod().getAnnotation(AppProfile.class),
                method.getJavaMethod().getAnnotation(ScopeProfile.class), exprHolderFactory);
        try {
            this.rpcProfile = rpcProfile;
            this.classController = classController;
            this.method = method;
            this.executor = executor;
            StringBuilder nameBuilder = new StringBuilder();
            nameBuilder.append(executor.getClass().getSimpleName()).append("#").append(method.getName());
            // 解析参数
            Class<?>[] parameterClasses = method.getJavaMethod().getParameterTypes();
            List<ControllerParamDescription> paramDescriptions = new ArrayList<>();
            Annotation[][] parameterAnnotations = method.getJavaMethod().getParameterAnnotations();
            ParamIndexCreator indexCreator = new ParamIndexCreator(method.getJavaMethod());
            if (parameterClasses.length > 0) {
                for (int index = 0; index < parameterClasses.length; index++) {
                    Class<?> paramClass = parameterClasses[index];
                    List<Annotation> annotations = ImmutableList.copyOf(parameterAnnotations[index]);
                    ControllerParamDescription paramDesc = new ControllerParamDescription(this, paramClass, annotations, indexCreator);
                    paramDescriptions.add(paramDesc);
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
            this.methodAnnotationHolder = new AnnotationHolder(method.getJavaMethod().getAnnotations());
            Map<Class<?>, List<Annotation>> indexParamAnnotationsMap = new HashMap<>();
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
                indexParamAnnotationsMap.put(clazz, Collections.unmodifiableList(indexAnnotationList));
            }
            this.indexParamAnnotationsMap = ImmutableMap.copyOf(indexParamAnnotationsMap);
            this.paramDescriptions = ImmutableList.copyOf(paramDescriptions);
            for (CommandPluginHolder plugin : this.getControllerBeforePlugins())
                this.beforeContext = this.putPlugin(this.beforeContext, plugin);
            for (CommandPluginHolder plugin : this.getControllerAfterPlugins())
                this.afterContext = this.putPlugin(this.afterContext, plugin);
            this.returnType = method.getReturnType();
        } catch (Exception e) {
            throw new IllegalArgumentException(format("{}.{} 方法解析失败", method.getDeclaringClass(), method.getName()), e);
        }
    }

    public MessageMode getMessageMode() {
        return this.rpcProfile.getMode();
    }

    public List<ControllerParamDescription> getParamDescriptions() {
        return this.paramDescriptions;
    }

    public int getParametersSize() {
        return this.paramDescriptions.size();
    }

    private PluginChain putPlugin(PluginChain context, CommandPluginHolder plugin) {
        if (context == null) {
            context = new PluginChain(plugin);
        }
        context.append(new PluginChain(plugin));
        return context;
    }

    @Override
    public Class<?> getControllerClass() {
        return this.executor.getClass();
    }

    public Object getParameterValue(int index, NetTunnel<?> tunnel, Message message, Object body) throws CommandException {
        if (index >= this.paramDescriptions.size()) {
            throw new CommandException(NetResultCode.SERVER_EXECUTE_EXCEPTION,
                    format("{} 获取 index 为 {} 的ParamDesc越界, index < {}", this, index, this.paramDescriptions.size()));
        }
        ControllerParamDescription desc = this.paramDescriptions.get(index);
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

    public int getProtocol() {
        return this.rpcProfile != null ? this.rpcProfile.getProtocol() : -1;
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
    public boolean isUserGroup(MessagerType messagerType) {
        return this.userGroups != null ? super.isUserGroup(messagerType) : this.classController.isUserGroup(messagerType);
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
    public Class<? extends AuthenticateValidator<?, ?>> getAuthValidator() {
        return this.auth != null ? super.getAuthValidator() : this.classController.getAuthValidator();
    }

    @Override
    protected List<CommandPluginHolder> getControllerBeforePlugins() {
        return this.beforePlugins != null && !this.beforePlugins.isEmpty() ? Collections.unmodifiableList(this.beforePlugins)
                : this.classController.getControllerBeforePlugins();
    }

    @Override
    protected List<CommandPluginHolder> getControllerAfterPlugins() {
        return this.afterPlugins != null && !this.afterPlugins.isEmpty() ? Collections.unmodifiableList(this.afterPlugins)
                : this.classController.getControllerAfterPlugins();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return this.classController.getAnnotation(annotationClass);
    }

    @Override
    public <A extends Annotation> List<A> getAnnotations(Class<A> annotationClass) {
        return this.classController.getAnnotations(annotationClass);
    }

    @SuppressWarnings("unchecked")
    public <A extends Annotation> List<A> getAnnotationsOfParametersByType(Class<A> clazz) {
        List<Annotation> annotations = this.indexParamAnnotationsMap.get(clazz);
        if (annotations == null) {
            return new ArrayList<>();
        }
        return (List<A>)annotations;
    }

    public List<Annotation> getParamAnnotationsByIndex(int index) {
        return this.paramDescriptions.get(index).getAnnotations();
    }

    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass) {
        return methodAnnotationHolder.getAnnotation(annotationClass);
    }

    public <A extends Annotation> List<A> getMethodAnnotations(Class<A> annotationClass) {
        return methodAnnotationHolder.getAnnotations(annotationClass);
    }

    public Set<Class<?>> getParamAnnotationClass() {
        return this.indexParamAnnotationsMap.keySet();
    }

    public Class<?> getReturnType() {
        return this.returnType;
    }

    Object invoke(NetTunnel<?> tunnel, Message message) throws Exception {
        // 获取调用方法的参数类型
        Object[] param = new Object[this.getParametersSize()];
        Object body = message.bodyAs(Object.class);
        for (int index = 0; index < param.length; index++) {
            param[index] = this.getParameterValue(index, tunnel, message, body);
        }
        return this.invoke(param);
    }

    void beforeInvoke(Tunnel<?> tunnel, Message message, MessageCommandContext context) {
        if (this.beforeContext == null) {
            return;
        }
        this.beforeContext.execute(tunnel, message, context);
    }

    void afterInvoke(Tunnel<?> tunnel, Message message, MessageCommandContext context) {
        if (this.afterContext == null) {
            return;
        }
        this.afterContext.execute(tunnel, message, context);
    }

    @Override
    public String toString() {
        return "MethodHolder [" + getControllerClass() + "." + getName() + "]";
    }

}
