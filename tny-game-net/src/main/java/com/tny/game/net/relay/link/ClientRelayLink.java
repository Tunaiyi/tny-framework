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
package com.tny.game.net.relay.link;

import com.tny.game.net.application.*;

/**
 * 本地转发连接
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 10:12 下午
 */
public interface ClientRelayLink extends NetRelayLink {

    /**
     * 连接认证
     *
     * @param serviceType 服务类型
     * @param service     服务名
     * @param instanceId  实例 id
     */
    void auth(RpcServiceType serviceType, String service, long instanceId);

    /**
     * 切换link
     *
     * @param tunnel tunnel
     */
    void switchTunnel(ClientRelayTunnel tunnel);

    /**
     * 断开link 与 tunnel的关联
     *
     * @param tunnel tunnel
     */
    void unlinkTunnel(RelayTunnel tunnel);

    //	/**
    //	 * 绑定客户端传到
    //	 *
    //	 * @param tunnel 客户端管道
    //	 * @return 成功返回true 失败返回 false
    //	 */
    //	boolean registerTunnel(RelayTunnel tunnel);
    //
    //	/**
    //	 * 反注册 tunnel
    //	 *
    //	 * @param tunnel 移除的tunnel
    //	 */
    //	void unregisterTunnel(RelayTunnel tunnel);

}
