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
package com.tny.game.basics.scheduler;

import com.tny.game.basics.configuration.*;
import com.tny.game.common.scheduler.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

public class TaskReceiverSchedulerPlugin implements VoidCommandPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskReceiverSchedulerPlugin.class);

    private BasicsTimeTaskProperties properties;

    private TimeTaskService timeTaskService;

    public TaskReceiverSchedulerPlugin(BasicsTimeTaskProperties properties, TimeTaskService timeTaskService) {
        this.properties = properties;
        this.timeTaskService = timeTaskService;
    }

    @Override
    public void doExecute(Tunnel tunnel, Message message, RpcInvokeContext context) throws Exception {
        TaskReceiverType type = properties.getPlugin().getReceiverType(tunnel.getGroup());
        if (type != null) {
            try {
                this.timeTaskService.checkTask(tunnel.getIdentify(), type);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

}
