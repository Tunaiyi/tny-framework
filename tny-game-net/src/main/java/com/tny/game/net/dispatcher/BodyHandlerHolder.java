package com.tny.game.net.dispatcher;

import com.tny.game.annotation.Body;
import com.tny.game.annotation.BodyHandler;
import com.tny.game.annotation.UserID;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.common.reflect.GMethod;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BodyHandlerHolder {

    private static enum ParamType {
        RESPONSE,

        BODY,

        UID;
    }

    //	private static final Logger LOG = LoggerFactory.getLogger(CoreLogger.DISPATCHER);

    /**
     * 执行方法
     */
    private final GMethod method;

    private final String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] parameterClass;
    /**
     * 控制器类型
     */
    protected Class<?> messageClass;
    /**
     * 是否需要requset对象
     */
    private ParamType[] paramTypes;
    /**
     * 执行对象
     */
    private final Object executor;

    /**
     * 控制器类型
     */
    protected final Class<?> clazz;
    /**
     * 控制器操作配置
     */
    protected final BodyHandler bodyHandler;
    /**
     * 用户组名称列表
     */
    protected final List<String> userGroupList;

    protected BodyHandlerHolder(final BodyHandler bodyHandler, final GMethod method, final Object executor) {
        if (executor == null)
            throw new IllegalArgumentException("executor is null");
        this.clazz = executor.getClass();
        ExceptionUtils.checkNotNull(bodyHandler, "{} bodyHandler is null", this.clazz);
        ExceptionUtils.checkNotNull(method, "{} method is null", this.clazz);

        this.bodyHandler = bodyHandler;
        this.method = method;
        this.methodName = this.method.getName();
        this.executor = executor;
        this.userGroupList = new ArrayList<String>();
        if (this.bodyHandler != null) {
            this.userGroupList.addAll(Arrays.asList(this.bodyHandler.userGroup()));
        }

        this.parameterClass = method.getJavaMethod().getParameterTypes();
        this.paramTypes = new ParamType[this.parameterClass.length];
        Annotation[][] value = method.getJavaMethod().getParameterAnnotations();
        for (int index = 0; index < this.parameterClass.length; index++) {
            if (Response.class.isAssignableFrom(this.parameterClass[index])) {
                this.paramTypes[index] = ParamType.RESPONSE;
            } else if (this.isHasAnnotation(value, index, UserID.class)) {
                this.paramTypes[index] = ParamType.UID;
            } else if (this.isHasAnnotation(value, index, Body.class)) {
                this.paramTypes[index] = ParamType.BODY;
            } else {
                ExceptionUtils.checkNotNull(method, "{} 第 {} 个参数没有@Body", this.method.getJavaMethod(), index);
            }
        }
    }

    private boolean isHasAnnotation(Annotation[][] value, int index, Class<? extends Annotation> target) {
        for (Annotation annotation : value[index]) {
            if (UserID.class.isInstance(annotation))
                return true;
        }
        return false;
    }

    public boolean isUserGroup(String group) {
        return this.userGroupList.isEmpty() || this.userGroupList.contains(group);
    }

    public String getName() {
        return this.methodName;
    }

    public void isCanHandle(ClientSession session, Response response, Object body) throws Exception {
        if (isUserGroup(session.getGroup()) && this.messageClass.isInstance(body)) {

        }
    }

    public void handle(Response response, Object body) throws Exception {
    }
    //
    //	public abstract int getID();
    //
    //	public abstract <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass);

}
