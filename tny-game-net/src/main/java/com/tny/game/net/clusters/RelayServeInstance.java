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

import com.tny.game.net.application.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;

import java.util.List;

/**
 * 本地集群服务实例
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:24 下午
 */
public interface RelayServeInstance extends ServeInstance {

    /**
     * @return 服务类型
     */
    default RpcServiceType serviceType() {
        return RpcServiceTypes.checkService(getServeName());
    }

    /**
     * @return 获取本地服务实例所有转发连接
     */
    List<ClientRelayLink> getActiveRelayLinks();

}
