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

package com.tny.game.actor.local;

import com.tny.game.actor.*;

/**
 * Created by Kun Yang on 16/4/26.
 */
public interface ActorDispatcher {

    /**
     * sender发送消息给当前Actor,
     *
     * @param message 发送消息
     * @param sender  发送者
     */
    default void tell(Object message, Actor<?, ?> sender) {
        sendMessage(message, sender, false);
    }

    /**
     * sender发送消息给当前Actor,
     *
     * @param message 发送消息
     * @param sender  发送者
     * @return 返回一个等待结果的Answer
     */
    default <V> Answer<V> ask(Object message, Actor<?, ?> sender) {
        return sendMessage(message, sender, true);
    }

    /**
     * 发送消息
     *
     * @param message    消息
     * @param sender     发送者
     * @param needAnswer 是否需要获取答案
     * @return 如果需要答案返回答案, 若不需要返回null
     */
    <V> Answer<V> sendMessage(Object message, Actor<?, ?> sender, boolean needAnswer);

    <ACT extends Actor<?, ?>> ACT getActor();

}
