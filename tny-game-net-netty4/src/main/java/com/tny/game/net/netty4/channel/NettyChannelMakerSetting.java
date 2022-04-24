package com.tny.game.net.netty4.channel;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/14 10:59 下午
 */
public class NettyChannelMakerSetting {

    private Class<? extends ChannelMaker<?>> makerClass;

    private List<String> pipelineChains = new ArrayList<>();

    @SuppressWarnings({"rawtypes"})
    public NettyChannelMakerSetting(Class<? extends ChannelMaker> makerClass) {
        this.makerClass = as(makerClass);
    }

    public Class<? extends ChannelMaker<?>> getMakerClass() {
        return makerClass;
    }

    public NettyChannelMakerSetting setMakerClass(Class<? extends ChannelMaker<?>> makerClass) {
        this.makerClass = makerClass;
        return this;
    }

    public List<String> getPipelineChains() {
        return Collections.unmodifiableList(pipelineChains);
    }

    public NettyChannelMakerSetting setPipelineChains(List<String> pipelineChains) {
        this.pipelineChains = pipelineChains;
        return this;
    }

}
