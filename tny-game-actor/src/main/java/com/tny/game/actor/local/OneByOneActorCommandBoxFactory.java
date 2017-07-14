package com.tny.game.actor.local;

import com.tny.game.common.worker.CommandBox;

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
                if (!cmd.isWork()) {
                    queue.poll();
                    continue;
                }
                this.executeCommand(cmd);
                this.runSize++;
                if (!cmd.isDone()) {
                    break;
                } else {
                    queue.poll();
                }
            }
            for (CommandBox commandBox : boxes()) {
                commandBox.process();
                // this.runSize += commandBox.getProcessSize();
            }
            long finishTime = System.currentTimeMillis();
            this.runUseTime = finishTime - startTime;
        }

    }

}
