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

package com.tny.game.actor.exception;

import com.tny.game.actor.*;

/**
 * Actor初始化异常
 *
 * @author KGTny
 */
public class ActorTerminatedException extends ActorException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 相关的Actor
     */
    private Actor<?, ?> actor;

    public ActorTerminatedException(Actor<?, ?> actor) {
        super("", null);
        this.actor = actor;
    }

    public ActorTerminatedException(Actor<?, ?> actor, String message) {
        super(message, null);
        this.actor = actor;
    }

    /**
     * 获取相关Actor
     *
     * @return Actor
     */
    public Actor<?, ?> getActor() {
        return actor;
    }

}
