package com.tny.game.net.netty4.relay;

import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 8:50 下午
 */
public class NettyRemoteServeInstance extends BaseRemoteServeInstance {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyRemoteServeInstance.class);

    /**
     * 连接任务
     */
    private final NettyServeInstanceConnectMonitor monitor;

    public NettyRemoteServeInstance(NetRemoteServeCluster cluster, ServeNode node, NettyServeInstanceConnectMonitor monitor) {
        super(cluster, node);
        this.monitor = monitor;
    }

    @Override
    protected void prepareClose() {
        monitor.stop();
    }

}