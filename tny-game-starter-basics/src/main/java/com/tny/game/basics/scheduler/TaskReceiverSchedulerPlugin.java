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
package com.tny.game.basics.scheduler;

import com.tny.game.basics.configuration.*;
import com.tny.game.common.scheduler.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

public class TaskReceiverSchedulerPlugin implements VoidCommandPlugin<Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskReceiverSchedulerPlugin.class);

    private BasicsTimeTaskProperties properties;

    private TimeTaskService timeTaskService;

    public TaskReceiverSchedulerPlugin(BasicsTimeTaskProperties properties, TimeTaskService timeTaskService) {
        this.properties = properties;
        this.timeTaskService = timeTaskService;
    }

    @Override
    public void doExecute(Tunnel<Long> communicator, Message message, RpcInvokeContext context) throws Exception {
        TaskReceiverType type = properties.getPlugin().getReceiverType(communicator.getGroup());
        if (type != null) {
            try {
                this.timeTaskService.checkTask(communicator.getUserId(), type);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

}
