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
