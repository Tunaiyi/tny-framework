package com.tny.game.net.dispatcher;

import com.tny.game.annotation.Controller;
import com.tny.game.annotation.Plugin;
import com.tny.game.annotation.UserID;
import com.tny.game.common.reflect.GMethod;
import com.tny.game.net.DevUtils;
import com.tny.game.net.base.ResultFactory;
import com.tny.game.net.dispatcher.plugin.ControllerPlugin;
import com.tny.game.net.dispatcher.plugin.PluginContext;
import com.tny.game.net.dispatcher.plugin.PluginHolder;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author KGTny
 * @ClassName: MethodHolder
 * @Description: 业务方法持有对象
 * @date 2011-8-16 上午11:19:03
 * <p>
 * <p>
 * <br>
 */
public final class MethodHolder extends AbstractControllerHolder {

    private static final String DEV_TIMEOUT_CHECK = "tny.server.dev.timeout.check";

    private static enum FirstParamType {
        PARAM,

        REQUEST,

        UID;
    }

    /**
     * 执行对象
     */
    private final Object executor;
    /**
     * 执行方法
     */
    private final GMethod method;

    private final String methodName;
    /**
     * 控制器操作配置
     */
    private final ControllerHolder controllerHolder;
    /**
     * 参数类型
     */
    private Class<?>[] parameterClass;
    /**
     * 参数类型
     */
    private Class<?>[] requestParameterClass;
    /**
     * 是否需要request对象
     */
    private FirstParamType firstParamType;
    /**
     * 参数上的注解
     */
    private List<List<Annotation>> paramAnnotationsList;
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
    protected MethodHolder(final Object executor, final ControllerHolder controllerHolder, final GMethod method, final Controller controller, final PluginHolder pluginHolder) {
        super(pluginHolder, controller, method.getJavaMethod().getAnnotation(Plugin.class), executor);
        this.methodName = method.getName();
        this.method = method;
        this.executor = executor;
        this.controllerHolder = controllerHolder;
        this.parameterClass = method.getJavaMethod().getParameterTypes();
        if (this.parameterClass.length > 0) {
            Annotation[][] value = method.getJavaMethod().getParameterAnnotations();
            if (Request.class.isAssignableFrom(this.parameterClass[0])) {
                this.firstParamType = FirstParamType.REQUEST;
            } else if (this.isUserID(value)) {
                this.firstParamType = FirstParamType.UID;
            } else {
                this.firstParamType = FirstParamType.PARAM;
            }
            if (this.firstParamType == FirstParamType.PARAM) {
                this.requestParameterClass = Arrays.copyOfRange(this.parameterClass, 0, this.parameterClass.length);
            } else {
                this.requestParameterClass = Arrays.copyOfRange(this.parameterClass, 1, this.parameterClass.length);
            }
        } else {
            this.firstParamType = FirstParamType.PARAM;
        }
        this.initMethodAnnotation(method.getJavaMethod().getAnnotations());
        Annotation[][] paramAnnotationsArray = method.getJavaMethod().getParameterAnnotations();
        List<List<Annotation>> paramAnnotationsList0 = new ArrayList<List<Annotation>>();
        Map<Class<?>, List<Annotation>> annotationsMap0 = new HashMap<Class<?>, List<Annotation>>();
        Set<Class<?>> annotationClassSet = new HashSet<Class<?>>();
        for (Annotation[] paramAnnotations : paramAnnotationsArray) {
            List<Annotation> paramAnnotationList = new ArrayList<Annotation>();
            for (Annotation paramAnnotation : paramAnnotations) {
                paramAnnotationList.add(paramAnnotation);
                if (paramAnnotation != null)
                    annotationClassSet.add(paramAnnotation.annotationType());
            }
            paramAnnotationsList0.add(Collections.unmodifiableList(paramAnnotationList));
        }
        for (Class<?> clazz : annotationClassSet) {
            List<Annotation> indexAnnotationList = new ArrayList<Annotation>();
            for (Annotation[] paramAnnotations : paramAnnotationsArray) {
                Annotation select = null;
                for (Annotation paramAnnotation : paramAnnotations) {
                    if (clazz.isInstance(paramAnnotation)) {
                        select = paramAnnotation;
                        break;
                    }
                }
                indexAnnotationList.add(select);
            }
            annotationsMap0.put(clazz, Collections.unmodifiableList(indexAnnotationList));
        }
        this.paramAnnotationsList = Collections.unmodifiableList(paramAnnotationsList0);
        this.annotationsMap = Collections.unmodifiableMap(annotationsMap0);
        for (ControllerPlugin plugin : this.getControllerPluginBeforeList())
            this.putPlugin(plugin);
        this.putPlugin(new MethodControllerPlugin(this));
        for (ControllerPlugin plugin : this.getControllerPluginAfterList())
            this.putPlugin(plugin);
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

    private FirstParamType getFirstParamType() {
        return this.firstParamType;
    }

    private static class MethodControllerPlugin implements ControllerPlugin {

        private MethodHolder methodHolder;

        private MethodControllerPlugin(MethodHolder methodHolder) {
            super();
            this.methodHolder = methodHolder;
        }

        @Override
        public CommandResult execute(Request request, CommandResult result, PluginContext context) throws Exception {
            // 获取调用方法的参数类型
            Class<?>[] requestParameterClass = this.methodHolder.getRequestParameterClass();
            // 获取request的对象的参数
            Object[] requestParam = request.getParameters(requestParameterClass);
            Object[] param = null;
            if (this.methodHolder.getFirstParamType() == FirstParamType.PARAM) {
                param = requestParam;
            } else {
                param = new Object[requestParam.length + 1];
                if (this.methodHolder.getFirstParamType() == FirstParamType.UID)
                    param[0] = request.getUserID();
                else
                    param[0] = request;
                System.arraycopy(requestParam, 0, param, 1, requestParam.length);
            }
            // 设置第一个为请求对象

            CommandResult executeResult = null;

            // 执行方法
            Object object = this.methodHolder.invoke(param);

            if (object == null) {

            } else if (object instanceof CommandResult) {
                CommandResult rsObject = (CommandResult) object;
                if (rsObject != ResultFactory.NONE)
                    executeResult = rsObject;
            } else {
                executeResult = ResultFactory.success(object);
            }
            //			if (executeResult != null)
            //				message.putBody(executeResult);

            return context.passToNext(request, executeResult);
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
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Object invoke(final Object[] params) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return this.method.invoke(this.executor, params);
    }

    /**
     * 获取方法参数类型
     * <p>
     * <p>
     * 参数类型<br>
     *
     * @return
     */
    public Class<?>[] getParamsType() {
        return this.parameterClass;
    }

    public Class<?>[] getRequestParameterClass() {
        return this.requestParameterClass;
    }

    @Override
    public boolean isCheck() {
        return this.controller != null ? this.controller.check() : this.controllerHolder.isCheck();
    }

    @Override
    public boolean isAuth() {
        return this.controller != null ? this.controller.auth() : this.controllerHolder.isAuth();
    }

    // @Override
    // public UserType getUserType() {
    // return controller != null ? controller.userType() :
    // controllerHolder.getUserType();
    // }

    @Override
    public List<ControllerPlugin> getControllerPluginBeforeList() {
        return this.plugin != null ? Collections.unmodifiableList(this.pluginBeforeList) : this.controllerHolder.getControllerPluginBeforeList();
    }

    @Override
    public List<ControllerPlugin> getControllerPluginAfterList() {
        return this.plugin != null ? Collections.unmodifiableList(this.pluginAfterList) : this.controllerHolder.getControllerPluginAfterList();
    }

    @Override
    public boolean isUserGroup(String group) {
        return this.controller != null ? this.userGroupList.indexOf(group) > -1 : this.controllerHolder.isUserGroup(group);
    }

    public long getRequestLife() {
        return this.controller != null ? this.controller.requestLife() : this.controllerHolder.getRequestLife();
    }

    public boolean isCanCall(String serverType) {
        return this.controller != null ? this.serverTypeList.isEmpty() || this.serverTypeList.indexOf(serverType) > -1 : this.controllerHolder.isCanCall(serverType);
    }

    public boolean isTimeOut() {
        if (!DevUtils.DEV_CONFIG.getBoolean(DEV_TIMEOUT_CHECK, true))
            return false;
        return this.controller != null ? this.controller.timeOut() : this.controllerHolder.isTimeOut();
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
        return this.paramAnnotationsList.get(index);
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
    public <A extends Annotation> List<A> getParamAnnotationsByIndex(Class<A> annotationClass) {
        List<A> annotationList = new ArrayList<>();
        for (List<Annotation> indexAnnotationList : this.paramAnnotationsList) {
            A aClassAnnotation = null;
            for (Annotation annotation : indexAnnotationList) {
                if (annotationClass.isInstance(annotation))
                    aClassAnnotation = (A) annotation;
            }
            annotationList.add(aClassAnnotation);
        }
        return annotationList;
    }

    @Override
    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass) {
        A annotation = this.getAnnotation0(annotationClass);
        if (annotation != null)
            return annotation;
        return this.controllerHolder.getMethodAnnotation(annotationClass);
    }

    //	public boolean isNeedRequest() {
    //		return needRequest;
    //	}
    public CommandResult execute(Request request) throws Exception {
        return this.pluginContext.passToNext(request, null);
    }

    @Override
    public String toString() {
        return "MethodHolder [" + getMethodClass() + "." + getName() + "]";
    }

}
