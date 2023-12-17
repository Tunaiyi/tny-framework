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
package com.tny.game.net.relay.link;

import com.tny.game.net.base.*;

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
    void delinkTunnel(RelayTunnel tunnel);

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
