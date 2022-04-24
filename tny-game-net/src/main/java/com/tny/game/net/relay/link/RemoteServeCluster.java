package com.tny.game.net.relay.link;

import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.transport.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/14 7:36 下午
 */
public interface RemoteServeCluster extends ServeCluster {

    /**
     * @return 登录用户
     */
    String getUsername();

    /**
     * @return 集群是否关闭
     */
    boolean isClose();

    /**
     * @return 获取上下文
     */
    RemoteServeClusterContext getContext();

    /**
     * 获取指定 id 的 instance
     *
     * @param id 指定 id
     * @return 返回 instance
     */
    RemoteServeInstance getLocalInstance(long id);

    /**
     * @return 健康集群实例列表
     */
    List<RemoteServeInstance> getHealthyLocalInstances();

    /**
     * 分配 link 给指定 tunnel
     *
     * @param tunnel 指定 tunnel
     * @return 返回分配的 link
     */
    RemoteRelayLink allotLink(Tunnel<?> tunnel);

}
