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

import com.tny.game.common.scheduler.*;

import java.util.*;

public abstract class GameTimeTaskHandler implements TimeTaskHandler {

    private String name;

    private HandleType handleType;

    private Set<TaskReceiverType> receiverTypeSet = new HashSet<>();

    protected GameTimeTaskHandler(String name, HandleType handleType, TaskReceiverType... receiverTypes) {
        super();
        this.handleType = handleType;
        this.name = name;
        this.receiverTypeSet.addAll(Arrays.asList(receiverTypes));
    }

    @Override
    public HandleType getHandleType() {
        return this.handleType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * 处理 <br>
     *
     * @param receiver 任务接收器
     */
    @Override
    public void handle(TaskReceiver receiver, long executeTime, TriggerContext context) {
        if (this.receiverTypeSet.contains(receiver.getType())) {
            this.doHandle(receiver, executeTime, context);
        }
    }

    protected abstract void doHandle(TaskReceiver receiver, long executeTime, TriggerContext context);

    @Override
    public boolean isHandleWith(TaskReceiverType group) {
        return this.receiverTypeSet.contains(group);
    }

}
