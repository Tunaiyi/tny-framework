/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.actor.exception;

import com.tny.game.actor.*;

/**
 * Actor初始化异常
 *
 * @author KGTny
 */
public class ActorInitializationException extends ActorException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 相关的ActorRef
     */
    private Actor<?, ?> actor;

    public ActorInitializationException(Actor<?, ?> actor, String message, Throwable cause) {
        super(message, cause);
        this.actor = actor;
    }

    /**
     * 获取相关ActorRef
     *
     * @return ActorRef
     */
    public Actor<?, ?> getActorRef() {
        return actor;
    }

}
