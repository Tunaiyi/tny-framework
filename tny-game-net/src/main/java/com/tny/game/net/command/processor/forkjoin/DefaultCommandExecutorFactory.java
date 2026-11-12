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
package com.tny.game.net.command.processor.forkjoin;

import com.tny.game.common.concurrent.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.session.*;

import java.util.concurrent.ExecutorService;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/3/9 02:08
 **/
public class DefaultCommandExecutorFactory implements CommandExecutorFactory {

    private final ExecutorService executorService;

    private final SerialCommandExecutorSetting setting;

    public DefaultCommandExecutorFactory() {
        this(new SerialCommandExecutorSetting());
    }

    public DefaultCommandExecutorFactory(SerialCommandExecutorSetting setting) {
        this.setting = setting;
        this.executorService = ForkJoinPools.pool(setting.getThreads(), getClass().getSimpleName(), true);
    }

    public DefaultCommandExecutorFactory(SerialCommandExecutorSetting setting, ExecutorService executorService) {
        this.executorService = executorService;
        this.setting = setting;
    }

    @Override
    public long getCommandTimeoutMillis() {
        return this.setting == null ? 3000L : this.setting.getCommandTimeout();
    }

    public CommandExecutor create(Session session) {
        return new SerialCommandExecutor("CommandExecutor-" + session.getId(), executorService);
    }

}
