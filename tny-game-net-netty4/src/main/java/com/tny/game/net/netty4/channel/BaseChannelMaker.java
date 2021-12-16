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
