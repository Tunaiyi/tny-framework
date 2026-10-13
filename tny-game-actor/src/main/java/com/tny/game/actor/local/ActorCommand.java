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

import com.tny.game.actor.*;
import com.tny.game.common.result.*;
import com.tny.game.common.worker.command.*;

/**
 * Actor命令
 * Created by Kun Yang on 16/4/26.
 */
public abstract class ActorCommand<T> implements Command {

    protected ActorCell actorCell;

    protected ActorCommand(ActorCell actorCell) {
        this.actorCell = actorCell;
    }

    protected abstract void handle() throws Throwable;

    public abstract T getResult();

    public abstract Answer<T> getAnswer();

    public Done<T> getDone() {
        if (this.isDone()) {
            return DoneResults.failure();
        }
        return DoneResults.successNullable(this.getResult());
    }

    @Override
    public void execute() {
        if (isDone()) {
            return;
        }
        this.actorCell.execute(this);
    }

    @Override
    public abstract boolean isDone();

    protected abstract boolean cancel();

    protected abstract void setAnswer(ActorAnswer<T> answer);

}
