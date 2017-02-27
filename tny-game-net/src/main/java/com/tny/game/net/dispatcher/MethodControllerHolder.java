package com.tny.game.net.dispatcher;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.tny.game.LogUtils;
import com.tny.game.annotation.Controller;
import com.tny.game.annotation.MsgBody;
import com.tny.game.annotation.MsgCode;
import com.tny.game.annotation.MsgParam;
import com.tny.game.annotation.Plugin;
import com.tny.game.annotation.UserID;
import com.tny.game.common.formula.FormulaHolder;
import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MvelFormulaFactory;
import com.tny.game.common.reflect.GMethod;
import com.tny.game.common.reflect.ObjectUtils;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.result.ResultCodes;
import com.tny.game.net.DevUtils;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.Message;
import com.tny.game.net.base.ResultFactory;
import com.tny.game.net.dispatcher.exception.DispatchException;
import com.tny.game.net.dispatcher.plugin.ControllerPlugin;
import com.tny.game.net.dispatcher.plugin.PluginContext;
import com.tny.game.net.dispatcher.plugin.PluginHolder;
import com.tny.game.number.LocalNum;
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

    private static final String DEV_TIMEOUT_CHECK = "tny.server.dev.timeout.check";

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
     * 注解列表
     */
    private Map<Class<?>, List<Annotation>> annotationsMap;
    /**
     * 执行list
     */
    private PluginContext pluginContext;

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
    protected MethodControllerHolder(final Object executor, final ClassControllerHolder classController, final GMethod method, final Controller controller, final PluginHolder pluginHolder) {
        super(pluginHolder, controller, method.getJavaMethod().getAnnotation(Plugin.class), executor);
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
                for (int index = 0; index < parameterClasses.length; index--) {
                    Class<?> paramClass = parameterClasses[index];
                    List<Annotation> annotations = ImmutableList.copyOf(parameterAnnotations[index]);
                    ParamDesc paramDesc = new ParamDesc(paramClass, annotations, counter);
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
            this.annotationsMap = ImmutableMap.copyOf(annotationsMap);
            this.parameterDescs = ImmutableList.copyOf(parameterDescs);
            for (ControllerPlugin plugin : this.getControllerPluginBeforeList())
                this.putPlugin(plugin);
            this.putPlugin(new MethodControllerPlugin(this));
            for (ControllerPlugin plugin : this.getControllerPluginAfterList())
                this.putPlugin(plugin);
        } catch (Exception e) {
            throw new IllegalArgumentException(LogUtils.format("{}.{} 方法解析失败", method.getDeclaringClass(), method.getName()), e);
        }
    }

    public List<ParamDesc> getParameterDescs() {
        return parameterDescs;
    }

    private void putPlugin(ControllerPlugin plugin) {
        if (this.pluginContext == null) {
            this.pluginContext = new PluginContext(this, plugin);
        } else {
            this.pluginContext.setNext(new PluginContext(this, plugin));
        }
    }

    public Class<?> getMethodClass() {
        return method.getDeclaringClass();
    }

    private static FormulaHolder formula(String formula) {
        FormulaHolder holder = cache.get(formula);
        if (holder != null)
            return holder;
        holder = MvelFormulaFactory.create(formula, FormulaType.EXPRESSION);
        return ObjectUtils.defaultIfNull(cache.putIfAbsent(formula, holder), holder);
    }

    private static class MethodControllerPlugin implements ControllerPlugin<Object> {

        private MethodControllerHolder methodHolder;

        private MethodControllerPlugin(MethodControllerHolder methodHolder) {
            super();
            this.methodHolder = methodHolder;
        }

        @Override
        public CommandResult execute(Message<Object> message, CommandResult result, PluginContext context) throws Exception {
            // 获取调用方法的参数类型
            List<ParamDesc> descs = this.methodHolder.getParameterDescs();
            Object[] param = new Object[descs.size()];
            Object body = message.getBody(Object.class);
            for (int i = 0; i < descs.size(); i++) {
                ParamDesc desc = descs.get(i);
                Object value = null;
                switch (desc.getParamType()) {
                    case MESSAGE:
                        value = message;
                        break;
                    case SESSION:
                        value = message.getSession();
                        break;
                    case BODY:
                        value = body;
                        break;
                    case UserID:
                        value = message.getID();
                        break;
                    case INDEX_PARAM:
                        try {
                            if (body == null)
                                throw new NullPointerException(LogUtils.format("{} 收到消息体为 null"));
                            if (body instanceof List) {
                                value = ((List) body).get(desc.getIndex());
                            } else if (body.getClass().isArray()) {
                                value = Array.get(body, desc.getIndex());
                            } else {
                                throw new DispatchException(CoreResponseCode.EXECUTE_EXCEPTION, LogUtils.format("{} 收到消息体为 {}, 不可通过index获取", methodHolder.toString(), body.getClass()));
                            }
                        } catch (DispatchException e) {
                            throw e;
                        } catch (Throwable e) {
                            throw new DispatchException(CoreResponseCode.EXECUTE_EXCEPTION, LogUtils.format("{} 调用异常", methodHolder.toString()), e);
                        }
                        break;
                    case KEY_PARAM:
                        if (body == null)
                            throw new NullPointerException(LogUtils.format("{} 收到消息体为 null"));
                        if (body instanceof Map) {
                            body = ((Map) body).get(desc.getName());
                        } else {
                            value = desc.getFormula().createFormula()
                                    .put("_body", body)
                                    .execute(desc.getParamClass());
                        }
                        break;
                    case CODE:
                        value = ResultCodes.of(message.getCode());
                        break;
                    case CODE_NUM:
                        value = message.getCode();
                        break;
                }
                param[i] = value;
            }

            CommandResult executeResult = null;

            // 执行方法
            Object object = this.methodHolder.invoke(param);


            if (object instanceof CommandResult) {
                CommandResult rsObject = (CommandResult) object;
                if (rsObject != ResultFactory.NONE)
                    executeResult = rsObject;
            } else if (object != null) {
                executeResult = ResultFactory.success(object);
            }

            return context.passToNext(message, executeResult);
        }
    }

    /**
     * 获取处理方法名称
     *
     * @return 返回处理方法名称
     */
    @Override
    public String getName() {
        final String operationName = this.controller != null ? this.controller.name() : null;
        if (operationName == null || operationName.equals(""))
            return this.methodName;
        return operationName;
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
    public boolean isCheck() {
        return this.controller != null ? this.controller.check() : this.classController.isCheck();
    }

    @Override
    public boolean isAuth() {
        return this.controller != null ? this.controller.auth() : this.classController.isAuth();
    }

    // @Override
    // public UserType getUserType() {
    // return controller != null ? controller.userType() :
    // classController.getUserType();
    // }

    @Override
    public List<ControllerPlugin> getControllerPluginBeforeList() {
        return this.plugin != null ? Collections.unmodifiableList(this.pluginBeforeList) : this.classController.getControllerPluginBeforeList();
    }

    @Override
    public List<ControllerPlugin> getControllerPluginAfterList() {
        return this.plugin != null ? Collections.unmodifiableList(this.pluginAfterList) : this.classController.getControllerPluginAfterList();
    }

    @Override
    public boolean isUserGroup(String group) {
        return this.controller != null ? this.userGroupList.indexOf(group) > -1 : this.classController.isUserGroup(group);
    }

    public long getRequestLife() {
        return this.controller != null ? this.controller.requestLife() : this.classController.getRequestLife();
    }

    public boolean isCanCall(String serverType) {
        return this.controller != null ? this.serverTypeList.isEmpty() || this.serverTypeList.indexOf(serverType) > -1 : this.classController.isCanCall(serverType);
    }

    public boolean isTimeOut() {
        if (!DevUtils.DEV_CONFIG.getBoolean(DEV_TIMEOUT_CHECK, true))
            return false;
        return this.controller != null ? this.controller.timeOut() : this.classController.isTimeOut();
    }

    @SuppressWarnings("unchecked")
    public <A extends Annotation> List<A> getParamAnnotationsByType(Class<A> clazz) {
        List<Annotation> annotations = this.annotationsMap.get(clazz);
        if (annotations == null)
            return new ArrayList<>();
        return (List<A>) annotations;
    }

    public boolean isExistParamAnnotation(Class<? extends Annotation> clazz) {
        return this.annotationsMap.containsKey(clazz);
    }

    /**
     * 获取某个参数上的注解列表
     *
     * @param index 参数位置索引
     * @return 返回指定参数的注解列表 ( @A @B int a, int b, @A int C) 获取 0 : [@A, @B] 获取 1
     * : [] 获取 2 : [@A]
     */
    public List<Annotation> getParamAnnotationsByIndex(int index) {
        return this.parameterDescs.get(index).getParamAnnotations();
    }

    public Set<Class<?>> getParamAnnotationClass() {
        return this.annotationsMap.keySet();
    }

    /**
     * 获取某方上参数指定注解类型的注解列表
     *
     * @param annotationClass 指定的注解类型
     * @return 注解列表 ( @A int a, int b, @A int c, @B int d) <br/>
     * 获取 @A : [@A, null, @A, null] <br/>
     * 获取 @B : [null, null, null, @B]
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> List<A> getParamAnnotationsByClass(Class<A> annotationClass) {
        return (List<A>) this.annotationsMap.get(annotationClass);
    }

    @Override
    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass) {
        A annotation = this.getAnnotation0(annotationClass);
        if (annotation != null)
            return annotation;
        return this.classController.getMethodAnnotation(annotationClass);
    }

    //	public boolean isNeedRequest() {
    //		return needRequest;
    //	}
    public CommandResult execute(Message message) throws Exception {
        return this.pluginContext.passToNext(message, null);
    }

    @Override
    public String toString() {
        return "MethodHolder [" + getMethodClass() + "." + getName() + "]";
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

        private List<Annotation> paramAnnotations;

        private ParamDesc(Class<?> paramClass, List<Annotation> paramAnnotations, LocalNum<Integer> indexCounter) {
            this.paramClass = paramClass;
            this.paramAnnotations = paramAnnotations;
            if (paramClass == Session.class) {
                this.paramType = ParamType.SESSION;
            } else if (paramClass == Message.class) {
                this.paramType = ParamType.MESSAGE;
            } else {
                for (Annotation anno : this.paramAnnotations) {
                    if (anno.annotationType() == MsgBody.class) {
                        this.paramType = ParamType.BODY;
                    } else if (anno.annotationType() == MsgParam.class) {
                        this.msgParam = (MsgParam) anno;
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
                        }
                    } else if (anno.annotationType() == UserID.class) {
                        this.paramType = ParamType.UserID;
                    } else if (anno.annotationType() == MsgCode.class) {
                        if (paramClass == Integer.class || paramClass == int.class) {
                            this.paramType = ParamType.CODE_NUM;
                        } else if (ResultCode.class.isAssignableFrom(this.paramClass)) {
                            this.paramType = ParamType.CODE;
                        } else {
                            throw new IllegalArgumentException(LogUtils.format("{} 类型参数只能是 {} {} {}, 无法为 {}",
                                    MsgCode.class, Integer.class, int.class, ResultCode.class));
                        }
                    }
                }
            }
        }

        private int getIndex() {
            return index;
        }

        private String getName() {
            return name;
        }

        private ParamType getParamType() {
            return paramType;
        }

        private Class<?> getParamClass() {
            return paramClass;
        }

        private FormulaHolder getFormula() {
            return this.formula;
        }

        private List<Annotation> getParamAnnotations() {
            return paramAnnotations;
        }

    }

}
