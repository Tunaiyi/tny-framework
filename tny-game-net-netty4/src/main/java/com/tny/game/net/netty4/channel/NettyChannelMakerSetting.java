/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
