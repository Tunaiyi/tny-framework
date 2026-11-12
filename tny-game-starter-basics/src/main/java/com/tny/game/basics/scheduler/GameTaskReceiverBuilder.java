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

public abstract class GameTaskReceiverBuilder {

    /**
     * 接收者ID
     *
     * @uml.property name="receicerId"
     */
    private long playerId;

    /**
     * 任务组
     *
     * @uml.property name="group"
     */
    private TaskReceiverType type;

    /**
     * 最后处理任务的时间
     *
     * @uml.property name="lastHandlerTime"
     */
    private long lastHandlerTime = -1L;

    /**
     * 最后一次实际处理时间
     */
    private long actualLastHandlerTime = -1L;

    protected GameTaskReceiverBuilder() {
        super();
    }

    public GameTaskReceiverBuilder setPlayerId(long playerId) {
        this.playerId = playerId;
        return this;
    }

    public GameTaskReceiverBuilder setType(TaskReceiverType type) {
        this.type = type;
        return this;
    }

    public GameTaskReceiverBuilder setLastHandlerTime(long lastHandlerTime) {
        this.lastHandlerTime = lastHandlerTime;
        return this;
    }

    public GameTaskReceiverBuilder setActualLastHandlerTime(long actualLastHandlerTime) {
        this.actualLastHandlerTime = actualLastHandlerTime;
        return this;
    }

    public GameTaskReceiver build() {
        GameTaskReceiver receiver = newReceiver();
        receiver.setPlayerId(this.playerId);
        receiver.setActualLastHandlerTime(this.actualLastHandlerTime == -1 ? System.currentTimeMillis() : this.actualLastHandlerTime);
        receiver.setLastHandlerTime(this.lastHandlerTime == -1 ? System.currentTimeMillis() : this.lastHandlerTime);
        if (this.type == null) {
            throw new NullPointerException("group is null");
        }
        receiver.setType(this.type);
        return receiver;
    }

    protected GameTaskReceiver newReceiver() {
        return new GameTaskReceiver();
    }

}
