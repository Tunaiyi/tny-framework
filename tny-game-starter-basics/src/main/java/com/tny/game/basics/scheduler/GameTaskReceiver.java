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
