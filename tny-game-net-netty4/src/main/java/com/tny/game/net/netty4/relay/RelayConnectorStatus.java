/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.relay;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 9:10 下午
 */
enum RelayConnectorStatus {

    /**
     * 初始化
     */
    INIT(true),

    /**
     * 连接中
     */
    CONNECTING(false),

    /**
     * 打开
     */
    OPEN(false),

    /**
     * 失败
     */
    DISCONNECT(true),

    /**
     * 关闭
     */
    CLOSE(false),

    //
    ;

    private final boolean canConnect;

    RelayConnectorStatus(boolean canConnect) {
        this.canConnect = canConnect;
    }

    boolean isCanConnect() {
        return canConnect;
    }
}
