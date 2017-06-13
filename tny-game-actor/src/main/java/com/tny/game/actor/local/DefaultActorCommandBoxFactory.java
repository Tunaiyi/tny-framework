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
        protected void doProcess() {
//        System.out.println(++handleTimes);
            ActorCommand<?> delimiter = null;
            Queue<ActorCommand<?>> queue = this.acceptQueue();
            int runSize = 0;
            int stepSize = this.getStepSize();
            while (!queue.isEmpty()) {
                ActorCommand<?> cmd = queue.peek();
                if (cmd == delimiter)
                    break;
                queue.poll();
                if (!cmd.isWork())
                    continue;
                this.executeCommand(cmd);
                runSize++;
                if (!cmd.isDone()) {
                    if (delimiter == null)
                        delimiter = cmd;
                    queue.add(cmd);
                }
                if (runSize >= stepSize)
                    break;
            }
            for (CommandBox commandBox : boxes()) {
                commandBox.process();
                // this.worker.execute(commandBox);
            }
        }

    }

}
