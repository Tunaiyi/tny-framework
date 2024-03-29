/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.clusters;

import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
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
    RelayServeInstance getLocalInstance(long id);

    /**
     * @return 健康集群实例列表
     */
    List<RelayServeInstance> getHealthyLocalInstances();

    /**
     * 分配 link 给指定 tunnel
     *
     * @param tunnel 指定 tunnel
     * @return 返回分配的 link
     */
    ClientRelayLink allotLink(Tunnel tunnel);

}
