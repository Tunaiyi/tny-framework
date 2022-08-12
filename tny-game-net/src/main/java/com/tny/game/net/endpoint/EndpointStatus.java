/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.endpoint;

public enum EndpointStatus {

    /**
     * 初始化
     */
    INIT(0),

    /**
     * 在线
     */
    ONLINE(1),

    /**
     * 离线
     */
    OFFLINE(2),

    /**
     * 关闭
     */
    CLOSE(3),

    //

    ;

    private final int id;

    EndpointStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
