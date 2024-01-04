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
 * Actor 终止命令
 * Created by Kun Yang on 16/4/28.
 */
class ActorTerminateCommand extends BaseActorCommand<Void> {

    private ActorTerminateMessage message;

    ActorTerminateCommand(ActorCell actorCell, ActorTerminateMessage message) {
        super(actorCell);
        this.message = message;
    }

    @Override
    protected Done<Void> doHandle() {
        this.actorCell.handle(this.message);
        return DoneResults.successNullable(null);
    }

}
