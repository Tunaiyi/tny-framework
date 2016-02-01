package com.tny.game.event;

import com.tny.game.common.context.AbstractAttributes;

/**
 * @author KGTny
 * @ClassName: Event
 * @Description: 时间基础抽象类
 * @date 2011-9-21 上午11:30:08
 * <p>
 * EventDispatcher派发的抽象基础类
 * <p>
 * EventDispatcher派发器派发事件的抽象基础类<br>
 */
public abstract class Event<T> extends AbstractAttributes {

    /**
     * 事件源
     *
     * @uml.property name="source"
     */
    protected final T source;

    /**
     * 事件处理器名称
     *
     * @uml.property name="handler"
     */
    protected final String handler;

    /**
     * 分发器
     */
    private EventDispatcher dispatcher = EventDispatcher.getDispatcher();

    public Event(String handler, T source) {
        super(false);
        this.handler = handler;
        this.source = source;
    }

    public Event(String handler, T source, EventDispatcher dispatcher) {
        super(false);
        this.handler = handler;
        this.source = source;
        if (dispatcher != null)
            this.dispatcher = dispatcher;
    }

    /**
     * 获取事件源
     * <p>
     * <p>
     * 获取触发该事件的事件源<br>
     *
     * @return 返回事件源
     */
    public T getSource() {
        return source;
    }

    /**
     * 获取事件处理器名称
     * <p>
     * 获取事件处理器名称<br>
     *
     * @return 返回事件处理器名称
     * @uml.property name="handler"
     */
    public String getHandler() {
        return handler;
    }

    public void dispatch() {
        dispatcher.dispatch(this);
    }

}
