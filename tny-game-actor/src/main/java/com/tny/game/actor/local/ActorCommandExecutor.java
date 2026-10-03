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

import java.util.concurrent.*;

/**
 * Actor 执行器
 * Created by Kun Yang on 16/4/26.
 */
public class ActorCommandExecutor extends DefaultCommandExecutor implements ActorWorker {

    public ActorCommandExecutor(String name) {
        this(name, ForkJoinPool.commonPool());
    }

    public ActorCommandExecutor(String name, ExecutorService executor) {
        super(name, 30, executor);
    }

    @Override
    public boolean takeOver(LocalActor<?, ?> actor) {
        return register(actor.cell().getCommandBox());
    }

}
