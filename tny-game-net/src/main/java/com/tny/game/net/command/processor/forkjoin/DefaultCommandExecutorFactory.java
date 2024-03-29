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
    public CommandExecutor create(Session session) {
        return new SerialCommandExecutor("CommandExecutor-" + session.getId(), executorService);
    }

}
