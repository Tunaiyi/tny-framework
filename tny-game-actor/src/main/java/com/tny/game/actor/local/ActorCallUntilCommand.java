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

import com.tny.game.common.result.*;

import java.util.function.Function;

/**
 * Actor Callable 命令
 * Created by Kun Yang on 16/4/26.
 */
class ActorCallUntilCommand<T> extends BaseActorCommand<T> {

    private Function<LocalActor, Done<T>> function;

    protected ActorCallUntilCommand(ActorCell actorCell, Function<LocalActor, Done<T>> function) {
        this(actorCell, function, null);
    }

    protected ActorCallUntilCommand(ActorCell actorCell, Function<LocalActor, Done<T>> function, ActorAnswer<T> answer) {
        super(actorCell, answer);
        this.function = function;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Done<T> doHandle() throws Exception {
        return this.function.apply(this.actorCell.getActor());
    }

}
