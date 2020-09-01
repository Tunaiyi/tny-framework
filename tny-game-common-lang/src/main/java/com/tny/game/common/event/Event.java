package com.tny.game.common.event;

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
public interface Event<T> {

    /**
     * 获取事件源
     * <p>
     * <p>
     * 获取触发该事件的事件源<br>
     *
     * @return 返回事件源
     */
    T getSource();

}
