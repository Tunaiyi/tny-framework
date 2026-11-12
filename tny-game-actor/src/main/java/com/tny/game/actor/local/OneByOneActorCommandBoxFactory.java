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

import com.tny.game.common.worker.*;

import java.util.Queue;

/**
 * Created by Kun Yang on 16/5/10.
 */
public class OneByOneActorCommandBoxFactory implements ActorCommandBoxFactory {

    @Override
    public ActorCommandBox create(ActorCell actorCell) {
        return new OneByOneActorCommandBox(actorCell);
    }

    private static final class OneByOneActorCommandBox extends ActorCommandBox {

        private OneByOneActorCommandBox(ActorCell actorCell) {
            super(actorCell);
        }

        @Override
        protected void doProcess() {
            Queue<ActorCommand<?>> queue = this.acceptQueue();
            long startTime = System.currentTimeMillis();
            this.runSize = 0;
            while (!queue.isEmpty()) {
                ActorCommand<?> cmd = queue.peek();
                this.executeCommand(cmd);
                this.runSize++;
                if (!cmd.isDone()) {
                    break;
                } else {
                    queue.poll();
                }
            }
            for (CommandBox<?> commandBox : boxes()) {
                commandBox.process();
                // this.runSize += commandBox.getProcessSize();
            }
            long finishTime = System.currentTimeMillis();
            this.runUseTime = finishTime - startTime;
        }

    }

}
