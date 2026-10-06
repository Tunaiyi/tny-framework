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

import com.tny.game.basics.item.*;
import com.tny.game.common.scheduler.*;

public class GameTaskReceiver extends TaskReceiver implements Any {

    protected long playerId;

    protected GameTaskReceiver() {
    }

    @Override
    public long getId() {
        return playerId;
    }

    @Override
    public long getPlayerId() {
        return this.playerId;
    }

    protected void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    protected void setType(TaskReceiverType type) {
        this.type = type;
    }

    public void setActualLastHandlerTime(long actualLastHandlerTime) {
        this.actualLastHandleTime = actualLastHandlerTime;
    }

    protected void setLastHandlerTime(long lastHandlerTime) {
        this.lastHandlerTime = lastHandlerTime;
    }

}
