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
