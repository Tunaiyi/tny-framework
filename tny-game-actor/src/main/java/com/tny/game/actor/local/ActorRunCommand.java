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

/**
 * Actor Runnable 命令
 * Created by Kun Yang on 16/4/26.
 */
public class ActorRunCommand extends BaseActorCommand<Void> {

    private Runnable runnable;

    ActorRunCommand(ActorCell actorCell, Runnable runnable) {
        this(actorCell, runnable, null);
    }

    ActorRunCommand(ActorCell actorCell, Runnable runnable, ActorAnswer<Void> answer) {
        super(actorCell, answer);
        this.runnable = runnable;
    }

    @Override
    protected Done<Void> doHandle() {
        this.runnable.run();
        return DoneResults.successNullable(null);
    }

}
