package com.tny.game.net.common.dispatcher;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.tny.game.common.utils.Logs;
import com.tny.game.common.formula.FormulaHolder;
import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MvelFormulaFactory;
import com.tny.game.common.reflect.GMethod;
import com.tny.game.common.utils.ObjectAide;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.result.ResultCodes;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.command.ControllerPlugin;
import com.tny.game.net.command.InvokeContext;
import com.tny.game.net.command.PluginContext;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageMode;
import com.tny.game.net.session.Session;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.common.number.LocalNum;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author KGTny
 * @ClassName: MethodHolder
 * @Description: 业务方法持有对象
 * @date 2011-8-16 上午11:19:03
 * <p>
 * <p>
 * <br>
 */
public final class MethodControllerHolder extends ControllerHolder {

    // private static final String DEV_TIMEOUT_CHECK = "tny.server.dev.timeout.check";

    protected static final ConcurrentHashMap<String, FormulaHolder> cache = new ConcurrentHashMap<>();

    /**
     * 执行对象
     */
    private final Object executor;
    /**
     * 执行方法
     */
    private final GMethod method;

    /**
     * 方法名字
     */
    private final String methodName;
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

    private boolean isUserID(Annotation[][] value) {
        for (Annotation annotation : value[0]) {
            if (UserID.class.isInstance(annotation))
                return true;
        }
        return false;
    }

    /**
     * 构造方法
     *
     * @param executor 调用的执行对象
     * @param method   方法
     */
    protected MethodControllerHolder(final Object executor, final AbstractMessageDispatcher dispatcher, final ClassControllerHolder classController, final GMethod method, final Controller controller) {
        super(executor, dispatcher, controller,
                method.getJavaMethod().getAnnotationsByType(BeforePlugin.class),
                method.getJavaMethod().getAnnotationsByType(AfterPlugin.class),
                method.getJavaMethod().getAnnotation(Auth.class),
                method.getJavaMethod().getAnnotationsByType(Check.class),
                method.getJavaMethod().getAnnotation(MessageFilter.class),
                method.getJavaMethod().getAnnotation(AppProfile.class));
        try {
            this.methodName = method.getName();
            this.method = method;
            this.executor = executor;
            this.classController = classController;
            Class<?>[] parameterClasses = method.getJavaMethod().getParameterTypes();
            List<ParamDesc> parameterDescs = new ArrayList<>();
            Annotation[][] parameterAnnotations = method.getJavaMethod().getParameterAnnotations();
            LocalNum<Integer> counter = new LocalNum<>(0);
            if (parameterClasses.length > 0) {
                for (int index = 0; index < parameterClasses.length; index++) {
                    Class<?> paramClass = parameterClasses[index];
                    List<Annotation> annotations = ImmutableList.copyOf(parameterAnnotations[index]);
                    ParamDesc paramDesc = new ParamDesc(this, paramClass, annotations, counter);
                    parameterDescs.add(paramDesc);
                }
            }
            this.initMethodAnnotation(method.getJavaMethod().getAnnotations());
            Map<Class<?>, List<Annotation>> annotationsMap = new HashMap<>();
            Set<Class<?>> annotationClassSet = new HashSet<>();
            for (Annotation[] paramAnnotations : parameterAnnotations) {
                for (Annotation paramAnnotation : paramAnnotations) {
                    if (paramAnnotation != null)
                        annotationClassSet.add(paramAnnotation.annotationType());
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
            for (ControllerPlugin plugin : this.getControllerBeforePlugins())
                this.beforeContext = this.putPlugin(this.beforeContext, plugin);
            for (ControllerPlugin plugin : this.getControllerAfterPlugins())
                this.afterContext = this.putPlugin(this.afterContext, plugin);
        } catch (Exception e) {
            throw new IllegalArgumentException(Logs.format("{}.{} 方法解析失败", method.getDeclaringClass(), method.getName()), e);
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
        if (this.messageModes != null)
            return this.messageModes;
        return this.classController.getMessageModes();
    }

    public List<ParamDesc> getParameterDescs() {
        return parameterDescs;
    }

    public int getParametersSize() {
        return this.parameterDescs.size();
    }

    private PluginContext putPlugin(PluginContext context, ControllerPlugin plugin) {
        if (context == null)
            context = new PluginContext(plugin);
        context.setNext(new PluginContext(plugin));
        return context;
    }

    @Override
    public Class<?> getControllerClass() {
        return method.getDeclaringClass();
    }

    private static FormulaHolder formula(String formula) {
        FormulaHolder holder = cache.get(formula);
        if (holder != null)
            return holder;
        holder = MvelFormulaFactory.create(formula, FormulaType.EXPRESSION);
        return ObjectAide.defaultIfNull(cache.putIfAbsent(formula, holder), holder);
    }

    public Object getParameterValue(int index, Tunnel<?> tunnel, Message<?> message, Object body) throws DispatchException {
        if (index >= this.parameterDescs.size())
            throw new DispatchException(CoreResponseCode.EXECUTE_EXCEPTION, Logs.format("{} 获取 index 为 {} 的ParamDesc越界, index < {}", this, index, parameterDescs.size()));
        ParamDesc desc = this.parameterDescs.get(index);
        if (desc == null)
            throw new DispatchException(CoreResponseCode.EXECUTE_EXCEPTION, Logs.format("{} 获取 index 为 {} 的ParamDesc为null", this, index));
        return desc.getValue(tunnel, message, body);
    }

    /**
     * 获取处理方法名称
     *
     * @return 返回处理方法名称
     */
    @Override
    public String getName() {
        return this.methodName;
    }

    @Override
    public int getID() {
        return this.controller != null ? this.controller.value() : -1;
    }

    /**
     * 调用消息处理
     *
     * @param params 调用参数
     * @return 返回值
     * @throws IllegalArgumentException  参数异常
     * @throws IllegalAccessException    进入异常
     * @throws InvocationTargetException 调用异常
     */
    private Object invoke(final Object[] params) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return this.method.invoke(this.executor, params);
    }

    @Override
    public boolean isUserGroup(String group) {
        return this.userGroups != null ? super.isUserGroup(group) : classController.isUserGroup(group);
    }

    @Override
    public boolean isActive(String appType) {
        return this.appTypes != null ? super.isActive(appType) : classController.isActive(appType);
    }

    @Override
    public boolean isAuth() {
        return this.auth != null ? super.isAuth() : classController.isAuth();
    }

    @Override
    public Class<?> getAuthProvider() {
        return this.auth != null ? super.getAuthProvider() : classController.getAuthProvider();
    }

    @Override
    protected List<ControllerPlugin> getControllerBeforePlugins() {
        return this.beforePlugins != null && !this.beforePlugins.isEmpty() ? Collections.unmodifiableList(this.beforePlugins) : this.classController.getControllerBeforePlugins();
    }

    @Override
    protected List<ControllerPlugin> getControllerAfterPlugins() {
        return this.afterPlugins != null && !this.afterPlugins.isEmpty() ? Collections.unmodifiableList(this.afterPlugins) : this.classController.getControllerAfterPlugins();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> List<A> getParamsAnnotationsByType(Class<A> clazz) {
        List<Annotation> annotations = this.paramAnnotationsMap.get(clazz);
        if (annotations == null)
            return new ArrayList<>();
        return (List<A>) annotations;
    }

    @Override
    public boolean isParamsAnnotationExist(Class<? extends Annotation> clazz) {
        return this.paramAnnotationsMap.containsKey(clazz);
    }


    @Override
    public List<Annotation> getParamAnnotationsByIndex(int index) {
        return this.parameterDescs.get(index).getParamAnnotations();
    }

    public Set<Class<?>> getParamAnnotationClass() {
        return this.paramAnnotationsMap.keySet();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return this.classController.getAnnotation(annotationClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass) {
        return (A) this.methodAnnotationMap.get(annotationClass);
    }

    protected void invoke(Tunnel<?> tunnel, Message<?> message, InvokeContext context) throws Exception {
        // 获取调用方法的参数类型
        Object[] param = new Object[this.getParametersSize()];
        Object body = message.getBody(Object.class);
        for (int index = 0; index < param.length; index++) {
            param[index] = this.getParameterValue(index, tunnel, message, body);
        }
        context.setResult(this.invoke(param));
    }

    protected void beforeInvoke(Tunnel<?> tunnel, Message<?> message, InvokeContext context) {
        if (this.beforeContext == null)
            return;
        this.beforeContext.execute(tunnel, message, context);
    }

    protected void afterInvke(Tunnel<?> tunnel, Message<?> message, InvokeContext context) {
        if (this.afterContext == null)
            return;
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

        private FormulaHolder formula;

        private ParamType paramType = ParamType.NONE;

        /* 参数类型 */
        private Class<?> paramClass;

        /* 参数注解 */
        private MsgParam msgParam;

        private boolean require;

        private List<Annotation> paramAnnotations;

        private MethodControllerHolder holder;

        private ParamDesc(MethodControllerHolder holder, Class<?> paramClass, List<Annotation> paramAnnotations, LocalNum<Integer> indexCounter) {
            this.holder = holder;
            this.paramClass = paramClass;
            this.paramAnnotations = paramAnnotations;
            this.require = true;
            if (paramClass == Session.class) {
                this.paramType = ParamType.SESSION;
            } else if (paramClass == Tunnel.class) {
                this.paramType = ParamType.TUNNEL;
            } else if (paramClass == Message.class) {
                this.paramType = ParamType.MESSAGE;
            } else {
                for (Annotation anno : this.paramAnnotations) {
                    if (anno.annotationType() == MsgBody.class) {
                        this.paramType = ParamType.BODY;
                        this.require = ((MsgBody) anno).require();
                    } else if (anno.annotationType() == MsgParam.class) {
                        this.msgParam = (MsgParam) anno;
                        this.require = this.msgParam.require();
                        if (StringUtils.isNoneBlank(this.msgParam.value())) {
                            this.name = this.msgParam.value();
                            this.formula = formula("_body." + this.name.trim());
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
                    } else if (anno.annotationType() == UserID.class) {
                        this.paramType = ParamType.UserID;
                    } else if (anno.annotationType() == MsgCode.class) {
                        if (paramClass == Integer.class || paramClass == int.class) {
                            this.paramType = ParamType.CODE_NUM;
                        } else if (ResultCode.class.isAssignableFrom(this.paramClass)) {
                            this.paramType = ParamType.CODE;
                        } else {
                            throw new IllegalArgumentException(Logs.format("{} 类型参数只能是 {} {} {}, 无法为 {}",
                                    MsgCode.class, Integer.class, int.class, ResultCode.class));
                        }
                    }
                }
            }
        }

        private Object getValue(Tunnel<?> tunnel, Message<?> message, Object body) throws DispatchException {
            boolean require = this.require;
            if (body == null)
                body = message.getBody(Object.class);
            Object value = null;
            switch (this.paramType) {
                case MESSAGE:
                    value = message;
                    break;
                case SESSION:
                    value = tunnel.getSession();
                    break;
                case TUNNEL:
                    value = tunnel;
                    break;
                case BODY:
                    value = body;
                    break;
                case UserID:
                    value = message.getUserID();
                    break;
                case INDEX_PARAM:
                    try {
                        if (body == null) {
                            if (require)
                                throw new NullPointerException(Logs.format("{} 收到消息体为 null"));
                            else
                                break;
                        }
                        if (body instanceof List) {
                            value = ((List) body).get(this.index);
                        } else if (body.getClass().isArray()) {
                            value = Array.get(body, this.index);
                        } else {
                            throw new DispatchException(CoreResponseCode.EXECUTE_EXCEPTION, Logs.format("{} 收到消息体为 {}, 不可通过index获取", holder.toString(), body.getClass()));
                        }
                    } catch (DispatchException e) {
                        throw e;
                    } catch (Throwable e) {
                        throw new DispatchException(CoreResponseCode.EXECUTE_EXCEPTION, Logs.format("{} 调用异常", holder.toString()), e);
                    }
                    break;
                case KEY_PARAM:
                    if (body == null) {
                        if (require)
                            throw new NullPointerException(Logs.format("{} 收到消息体为 null"));
                        else
                            break;
                    }
                    if (body instanceof Map) {
                        value = ((Map) body).get(this.name);
                    } else {
                        value = this.formula.createFormula()
                                .put("_body", body)
                                .execute(this.paramClass);
                    }
                    break;
                case CODE:
                    value = ResultCodes.of(message.getCode());
                    break;
                case CODE_NUM:
                    value = message.getCode();
                    break;
            }
            return value;
        }

        private List<Annotation> getParamAnnotations() {
            return paramAnnotations;
        }

    }

}
