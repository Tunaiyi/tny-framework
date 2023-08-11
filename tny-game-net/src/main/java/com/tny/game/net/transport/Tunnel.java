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
package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;

/**
 * 通道
 * Created by Kun Yang on 2017/3/26.
 */
public interface Tunnel<UID> extends Connector<UID>, Connection {

    /**
     * @return 通道 Id
     */
    long getId();

    /**
     * @return 访问 Id
     */
    long getAccessId();

    /**
     * @return 是否已经开启
     */
    boolean isOpen();

    /**
     * @return 获取绑定中断
     */
    Endpoint<UID> getEndpoint();

    /**
     * @return 管道状态
     */
    TunnelStatus getStatus();

}
