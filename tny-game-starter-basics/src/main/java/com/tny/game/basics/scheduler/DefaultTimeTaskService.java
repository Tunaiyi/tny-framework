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
import org.slf4j.*;

public class DefaultTimeTaskService implements TimeTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTimeTaskService.class);

    private final BasicsTimeTaskProperties properties;

    private final TimeTaskScheduler scheduler;

    private final GameTaskReceiverManager taskReceiverManager;

    private final GameTaskReceiverFactory taskReceiverFactory;

    private GameTaskReceiver systemReceiver;

    public DefaultTimeTaskService(BasicsTimeTaskProperties properties, TimeTaskHandlerHolder handlerHolder,
            SchedulerStore schedulerStore, GameTaskReceiverManager taskReceiverManager, GameTaskReceiverFactory taskReceiverFactory) {
        this.properties = properties;
        this.scheduler = new TimeTaskScheduler(handlerHolder, schedulerStore, properties.getMaxTaskSize());
        this.taskReceiverManager = taskReceiverManager;
        this.taskReceiverFactory = taskReceiverFactory;
    }

    private void checkSystemTask() {
        try {
            if (this.systemReceiver == null) {
                GameTaskReceiver systemReceiver = this.taskReceiverManager.getReceiver(properties.getId(), DefaultTaskReceiverType.SYSTEM);
                if (systemReceiver == null) {
                    this.systemReceiver = taskReceiverFactory.create(DefaultTaskReceiverType.SYSTEM, properties.getId());
                    this.taskReceiverManager.saveReceiver(this.systemReceiver);
                } else {
                    this.systemReceiver = systemReceiver;
                }
            }
            this.scheduler.schedule(this.systemReceiver);
            this.taskReceiverManager.saveReceiver(this.systemReceiver);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void checkTask(long playerId, TaskReceiverType receiverType) {
        GameTaskReceiver dbReceiver = this.taskReceiverManager.getReceiver(playerId, receiverType);
        final GameTaskReceiver receiver;
        if (dbReceiver == null) {
            receiver = taskReceiverFactory.create(receiverType, playerId);
            this.taskReceiverManager.insertReceiver(receiver);
        } else {
            receiver = dbReceiver;
        }
        this.scheduler.schedule(receiver);
        this.taskReceiverManager.saveReceiver(receiver);
    }

    public void reload() {
        this.scheduler.reload(properties);
    }

    @Override
    public void prepareStart() throws Exception {
        if (!this.scheduler.isStart()) {
            this.scheduler.addListener(timeTask -> checkSystemTask());
            this.scheduler.start(properties);
        }
    }

}
