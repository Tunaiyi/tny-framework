package com.tny.game.net.dispatcher;


import com.tny.game.annotation.MsgBody;
import com.tny.game.annotation.UserID;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.common.reflect.GMethod;

import java.util.ArrayList;
import java.util.Arrays;

public class MessageWatcherHolder {

    private enum ParamType {

        MESSAGE,

        BODY,

        UID,

        PARAM,

        CODE,

        CODE_NUM;

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
    protected final MessageWatcher watcher;
    /**
     * 用户组名称列表
     */
    protected final List<String> userGroupList;

    protected MessageWatcherHolder(final MessageWatcher watcher, final GMethod method, final Object executor) {
        if (executor == null)
            throw new IllegalArgumentException("executor is null");
        this.clazz = executor.getClass();
        ExceptionUtils.checkNotNull(watcher, "{} watcher is null", this.clazz);
        ExceptionUtils.checkNotNull(method, "{} method is null", this.clazz);

        this.method = method;
        this.methodName = this.method.getName();
        this.executor = executor;
        this.userGroupList = new ArrayList<>();
        this.watcher = watcher;
        this.userGroupList.addAll(Arrays.asList(this.watcher.userGroup()));

        this.parameterClass = method.getJavaMethod().getParameterTypes();
        this.paramTypes = new ParamType[this.parameterClass.length];
        Annotation[][] value = method.getJavaMethod().getParameterAnnotations();
        for (int index = 0; index < this.parameterClass.length; index++) {
            if (Response.class.isAssignableFrom(this.parameterClass[index])) {
                this.paramTypes[index] = ParamType.MESSAGE;
            } else if (this.isHasAnnotation(value, index, UserID.class)) {
                this.paramTypes[index] = ParamType.UID;
            } else if (this.isHasAnnotation(value, index, MsgBody.class)) {
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

    public boolean isCanHandle(NetSession session, NetMessage message) {
        return this.isUserGroup(session.getGroup()) && this.messageClass.isInstance(message.getBody());
    }

    public void handle(Response response, Object body) throws Exception {
    }
    //
    //	public abstract int getID();
    //
    //	public abstract <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass);

}
