/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.actor.local;

/**
 * Deliver对象,负责处理消息.
 *
 * @author KGTny
 */
public interface ActorLifeCycle {

    default void preBindWorker() {
    }

    default void postBindWorker() {
    }

    default void preUnbindWorker() {
    }

    default void postUnbindWorker() {
    }

    default void preTerminate() {
    }

    default void postTerminate() {
    }

    default void preHandle(ActorCommand<?> command) {
    }

    default void postHandle(ActorCommand<?> command) {
    }

    default void postSucc(Object result) {
    }

    default void postFail(Throwable cause) {
    }

}