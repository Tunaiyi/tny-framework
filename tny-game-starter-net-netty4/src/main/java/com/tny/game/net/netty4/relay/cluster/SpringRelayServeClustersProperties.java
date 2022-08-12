/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.relay.cluster;

import com.google.common.collect.ImmutableList;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/26 1:50 下午
 */
@ConfigurationProperties("tny.net.relay.cluster")
public class SpringRelayServeClustersProperties {

    private List<SpringRelayServeClusterSetting> serveClusters = ImmutableList.of();

    public List<SpringRelayServeClusterSetting> getServeClusters() {
        return serveClusters;
    }

    public SpringRelayServeClustersProperties setServeClusters(List<SpringRelayServeClusterSetting> serveClusters) {
        this.serveClusters = ImmutableList.copyOf(serveClusters);
        return this;
    }
    //
    //	public String getRelayMessageRouter() {
    //		return relayMessageRouter;
    //	}
    //
    //	public String getServeClusterSelector() {
    //		return serveClusterSelector;
    //	}

}
