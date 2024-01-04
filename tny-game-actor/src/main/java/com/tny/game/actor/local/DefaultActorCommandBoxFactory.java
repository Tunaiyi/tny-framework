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

import com.tny.game.common.worker.*;

import java.util.Queue;

/**
 * Created by Kun Yang on 16/5/10.
 */
public class DefaultActorCommandBoxFactory implements ActorCommandBoxFactory {

    @Override
    public ActorCommandBox create(ActorCell actorCell) {
        return new DefaultActorCommandBox(actorCell);
    }

    private static final class DefaultActorCommandBox extends ActorCommandBox {

        private DefaultActorCommandBox(ActorCell actorCell) {
            super(actorCell);
        }

        @Override
        protected void doProcess() {
            //        System.out.println(++handleTimes);
            ActorCommand<?> delimiter = null;
            Queue<ActorCommand<?>> queue = this.acceptQueue();
            int runSize = 0;
            int stepSize = this.getStepSize();
            while (!queue.isEmpty()) {
                ActorCommand<?> cmd = queue.peek();
                if (cmd == delimiter) {
                    break;
                }
                queue.poll();
                this.executeCommand(cmd);
                runSize++;
                if (!cmd.isDone()) {
                    if (delimiter == null) {
                        delimiter = cmd;
                    }
                    queue.add(cmd);
                }
                if (runSize >= stepSize) {
                    break;
                }
            }
            for (CommandBox<?> commandBox : boxes()) {
                commandBox.process();
            }
        }

    }

}
