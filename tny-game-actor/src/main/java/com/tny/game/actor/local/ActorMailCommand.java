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
import com.tny.game.common.result.*;

/**
 * Actor Message 命令
 * Created by Kun Yang on 16/4/26.
 */
@SuppressWarnings("unchecked")
class ActorMailCommand<T> extends BaseActorCommand<T> implements ActorMail<Object> {

    private Object message;

    private Actor<?, ?> sender;

    ActorMailCommand(ActorCell actorCell, Object message, Actor<?, ?> sender) {
        this(actorCell, message, sender, null);
    }

    ActorMailCommand(ActorCell actorCell, Object message, Actor<?, ?> sender, ActorAnswer<T> answer) {
        super(actorCell, answer);
        this.message = message;
        this.sender = sender;
    }

    @Override
    protected Done<T> doHandle() {
        return DoneResults.successNullable((T) this.actorCell.handle(this));
    }

    @Override
    public Object getMessage() {
        return this.message;
    }

    @Override
    public <ACT extends Actor> ACT getSender() {
        return (ACT) this.sender;
    }

}
