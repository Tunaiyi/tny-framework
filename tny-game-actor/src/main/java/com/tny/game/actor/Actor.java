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

package com.tny.game.actor;

/**
 * Actor的引用,开发过程中,无法获取到实际的Actor对象,只能获取到对应的Actor引用,
 * 可以通过actor引用进行消息发送.
 *
 * @author KGTny
 */
public interface Actor<ID, M> { // TODO extends Executor

    /**
     * @return 获取Actor ID
     */
    ID getActorId();

    /**
     * 获取Actor的路径信息对象
     *
     * @return 路径信息对象
     */
    ActorURL getURL();

    /**
     * @return 是否终止
     */
    boolean isTerminated();

    /**
     * @return 是否被接管
     */
    boolean isTakenOver();

    /**
     * @return 是否是本地
     */
    boolean isLocal();

    /**
     * 向当前Actor发送消息, 发送者为noSender
     *
     * @param message 发送的消息
     */
    void tell(M message);

    /**
     * sender发送消息给当前Actor,
     *
     * @param message 发送消息
     * @param sender  发送者
     */
    void tell(M message, Actor sender);

    /**
     * sender发送消息给当前Actor,
     *
     * @param message 发送消息
     * @return 返回一个等待结果的Answer
     */
    <V> Answer<V> ask(M message);

    /**
     * sender发送消息给当前Actor,
     *
     * @param message 发送消息
     * @param sender  发送者
     * @return 返回一个等待结果的Answer
     */
    <V> Answer<V> ask(M message, Actor sender);

}