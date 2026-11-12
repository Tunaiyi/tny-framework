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
