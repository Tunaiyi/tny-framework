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
