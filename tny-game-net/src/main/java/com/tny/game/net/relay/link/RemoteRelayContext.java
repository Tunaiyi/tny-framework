/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.relay.link;

import com.tny.game.net.base.*;
import com.tny.game.net.relay.link.route.*;

/**
 * 本地转发服务上下问
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 2:47 下午
 */
public interface RemoteRelayContext {

    /**
     * @return 获取前服务ServeName
     */
    String getAppServeName();

    /**
     * @return 获取当前服务实例 id
     */
    long getAppInstanceId();

    /**
     * @return 分配 link id
     */
    String createLinkKey(String service);

    /**
     * @return 获取 message 路由器
     */
    RelayMessageRouter getRelayMessageRouter();

    /**
     * @return 获取 ServeCluster 过滤器
     */
    ServeClusterFilter getServeClusterFilter();

    /**
     * @return 获取网络应用上下问
     */
    NetAppContext getAppContext();

}
