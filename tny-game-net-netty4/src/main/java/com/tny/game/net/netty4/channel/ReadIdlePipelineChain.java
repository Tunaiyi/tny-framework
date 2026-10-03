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

package com.tny.game.net.netty4.channel;

import io.netty.channel.*;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

public class ReadIdlePipelineChain<C extends Channel> implements ChannelPipelineChain {

    private long idleTimeout = 180000;

    public ReadIdlePipelineChain() {
    }

    public ReadIdlePipelineChain(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    @Override
    public void afterMake(ChannelPipeline channelPipeline) {
        channelPipeline.addLast(new ReadTimeoutHandler(this.idleTimeout, TimeUnit.MILLISECONDS));
    }

    public long getIdleTimeout() {
        return this.idleTimeout;
    }

    public ReadIdlePipelineChain<C> setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
        return this;
    }

}
