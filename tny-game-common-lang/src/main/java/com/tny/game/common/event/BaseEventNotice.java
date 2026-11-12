/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
