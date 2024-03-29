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