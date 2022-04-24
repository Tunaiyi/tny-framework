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
