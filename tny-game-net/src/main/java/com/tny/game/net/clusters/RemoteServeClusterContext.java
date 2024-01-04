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

import com.tny.game.common.url.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.allot.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/3 8:47 下午
 */
public interface RemoteServeClusterContext {

    String getService();

    String getServeName();

    String getUsername();

    RemoteServeClusterSetting getSetting();

    List<NetAccessNode> getInstances();

    ServeInstanceAllotStrategy getServeInstanceAllotStrategy();

    RelayLinkAllotStrategy getRelayLinkAllotStrategy();

    /**
     * 连接 link
     *
     * @param url      连接服务器 url
     * @param callback 回调
     */
    void connect(URL url, RelayConnectCallback callback);


}
