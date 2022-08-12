/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.processor.disruptor;

import com.tny.game.net.command.processor.*;
import com.tny.game.net.command.task.*;
import io.netty.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/20 10:47 下午
 */
public class TimerCommandTaskBoxDriver extends CommandTaskBoxDriver implements TimerTask {

    protected TimerCommandTaskBoxDriver(CommandTaskBox taskBox, EndpointCommandTaskBoxProcessor<?> processor) {
        super(taskBox, processor);
    }

    @Override
    public void run(Timeout timeout) {
        if (!timeout.isCancelled()) {
            this.trySubmit();
        }
    }

}
