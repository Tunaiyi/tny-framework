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

import com.google.common.collect.ImmutableList;
import io.netty.channel.*;

import java.util.List;

public abstract class BaseChannelMaker<C extends Channel> implements ChannelMaker<C> {

    private List<ChannelPipelineChain> channelPipelineChains = ImmutableList.of();

    protected BaseChannelMaker() {
    }

    @Override
    public void initChannel(C channel) throws Exception {
        ChannelPipeline channelPipeline = channel.pipeline();
        for (ChannelPipelineChain chain : channelPipelineChains) {
            chain.beforeMake(channelPipeline);
        }
        makeChannel(channel);
        for (ChannelPipelineChain chain : channelPipelineChains) {
            chain.afterMake(channelPipeline);
        }
        this.postInitChannel(channel);
    }

    protected abstract void makeChannel(C channel) throws Exception;

    protected abstract void postInitChannel(C channel);

    public BaseChannelMaker<C> setChannelPipelineChains(List<ChannelPipelineChain> channelPipelineChains) {
        this.channelPipelineChains = ImmutableList.copyOf(channelPipelineChains);
        return this;
    }

}
