/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
public abstract class BaseEventNotice<T> implements EventNotice<T> {

    /**
     * 事件源
     *
     * @uml.property name="source"
     */
    protected final T source;

    public BaseEventNotice(T source) {
        this.source = source;
    }

    /**
     * 获取事件源
     * <p>
     * <p>
     * 获取触发该事件的事件源<br>
     *
     * @return 返回事件源
     */
    @Override
    public T getSource() {
        return this.source;
    }

}
