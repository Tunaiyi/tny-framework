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
