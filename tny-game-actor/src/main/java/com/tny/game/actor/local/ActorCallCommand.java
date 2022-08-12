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

import com.tny.game.common.result.*;

import java.util.concurrent.Callable;

/**
 * Actor Callable 命令
 * Created by Kun Yang on 16/4/26.
 */
class ActorCallCommand<T> extends BaseActorCommand<T> {

    private Callable<T> callable;

    protected ActorCallCommand(ActorCell actorCell, Callable<T> callable) {
        this(actorCell, callable, null);
    }

    protected ActorCallCommand(ActorCell actorCell, Callable<T> callable, ActorAnswer<T> answer) {
        super(actorCell, answer);
        this.callable = callable;
    }

    @Override
    protected Done<T> doHandle() throws Exception {
        return DoneResults.successNullable(this.callable.call());
    }

}
