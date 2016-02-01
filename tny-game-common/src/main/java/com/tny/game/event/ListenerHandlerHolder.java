package com.tny.game.event;

import com.tny.game.LogUtils;
import com.tny.game.common.reflect.GMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KGTny
 * @ClassName: ListenerHandlerHolder
 * @Description: 监听器处理器持有器
 * @date 2011-9-21 ����11:58:09
 * <p>
 * 监听器处理器持有器
 * <p>
 * 监听器处理器持有器,负责管理监听器处理方法<br>
 */
class ListenerHandlerHolder {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(LogUtils.EVENT);

    /**
     * 处理的方法
     *
     * @uml.property name="method"
     */
    private final GMethod method;

    /**
     * 监听器对象
     *
     * @uml.property name="object"
     */
    private final Object object;

    /**
     * 处理器名称
     *
     * @uml.property name="name"
     */
    private final String name;

    /**
     * 构造函数
     *
     * @param method 处理方方法
     * @param object 监听器对象
     */
    public ListenerHandlerHolder(GMethod method, Object object) {
        super();
        if (method == null)
            throw new NullPointerException("method is null");
        if (object == null)
            throw new NullPointerException("object is null");
        this.method = method;
        this.object = object;
        String methodName = method.getName().substring(6);
        this.name = methodName.substring(0, 1).toLowerCase() + methodName.substring(1, methodName.length());
    }

    /**
     * 指定处理器是否属于次监听器
     * <p>
     * <p>
     * 检测指定处理器是否属于次监听器<br>
     *
     * @param eventListener 指定处理器
     * @return 处理器属于监听器则返回ture 否则返回false
     */
    public boolean isHodlerOnwer(Object eventListener) {
        if (eventListener == null)
            return false;
        return this.object.equals(eventListener);
    }

    /**
     * 获取处理器名称
     * <p>
     * 获取处理器名称<br>
     *
     * @return 返回处理器名称
     * @uml.property name="name"
     */
    public String getName() {
        return this.name;
    }

    /**
     * 处理事件
     * <p>
     * <p>
     * 处理事件<br>
     *
     * @param event 处理的时间
     */
    public void handle(Event<?> event) {
        try {
            if (LOG.isDebugEnabled())
                LOG.debug("#EventHandler调用#触发{}，调用监听器 {} 方法 ", event, this.method);
            this.method.invoke(this.object, new Object[]{event});
        } catch (Throwable e) {
            LOG.error("#EventHandler调用#触发{}，调用监听器 {} 方法,抛出异常", LogUtils.msg(event, this.method, e));
        }
    }

    @Override
    public String toString() {
        return "ListenerHandlerHolder [method=" + this.method.getJavaMethod() + ", class=" + this.object.getClass() + "]";
    }

}
