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

import com.tny.game.actor.exception.*;
import com.tny.game.common.worker.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Actor 命令箱子
 * Created by Kun Yang on 16/4/25.
 */
public abstract class ActorCommandBox extends AbstractWorkerCommandBox<ActorCommand<?>, ActorCommandBox> {

    private final ActorCell actorCell;

    private volatile boolean terminated;

    public ActorCommandBox(ActorCell actorCell) {
        super(new ConcurrentLinkedQueue<>());
        this.actorCell = actorCell;
    }

    @Override
    protected Queue<ActorCommand<?>> acceptQueue() {
        return this.queue;
    }

    protected void terminate() {
        if (this.terminated) {
            return;
        }
        this.terminated = true;
        Queue<ActorCommand<?>> queue = this.acceptQueue();
        while (!queue.isEmpty()) {
            ActorCommand<?> cmd = queue.poll();
            cmd.cancel();
            this.executeCommand(cmd);
        }
        for (ActorCommandBox box : boxes()) {
            box.getActorCell().terminate();
        }
    }

    boolean isTerminated() {
        return this.terminated;
    }

    boolean detach() {
        return this.worker != null && this.worker.unregister(this);
    }

    private ActorCell getActorCell() {
        return this.actorCell;
    }

    private void checkTerminated() {
        if (this.isTerminated()) {
            throw new ActorTerminatedException(this.actorCell.getActor());
        }
    }

    public int getStepSize() {
        return this.actorCell.getStepSize();
    }

    @Override
    public boolean accept(ActorCommand<?> command) {
        this.checkTerminated();
        return super.accept(command);
    }

    @Override
    public boolean bindWorker(CommandBoxWorker worker) {
        return !this.isTerminated() && super.bindWorker(worker);
    }

    @Override
    public boolean unbindWorker() {
        return super.unbindWorker();
    }

    @Override
    public boolean register(CommandBox commandBox) {
        return !this.terminated && super.register(commandBox);
    }

}
