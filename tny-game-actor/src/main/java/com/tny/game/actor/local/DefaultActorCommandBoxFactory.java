package com.tny.game.actor.local;

import com.tny.game.worker.CommandBox;

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
        public void process() {
//        System.out.println(++handleTimes);
            ActorCommand<?, ?, ?> delimiter = null;
            Queue<ActorCommand<?, ?, ?>> queue = this.acceptQueue();
            long startTime = System.currentTimeMillis();
            this.runSize = 0;
            int stepSize = this.getStepSize();
            while (!queue.isEmpty()) {
                ActorCommand<?, ?, ?> cmd = queue.peek();
                if (cmd == delimiter)
                    break;
                queue.poll();
                if (!cmd.isWork())
                    continue;
                this.executeCommand(cmd);
                this.runSize++;
                if (!cmd.isDone()) {
                    if (delimiter == null)
                        delimiter = cmd;
                    queue.add(cmd);
                }
                if (this.runSize >= stepSize)
                    break;
            }
            for (CommandBox commandBox : boxes()) {
                this.worker.submit(commandBox);
                this.runSize += commandBox.getProcessSize();
            }
            long finishTime = System.currentTimeMillis();
            this.runUseTime = finishTime - startTime;
        }

    }

}
