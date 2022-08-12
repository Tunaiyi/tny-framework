/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.transport;

import com.tny.game.common.enums.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-08 11:49
 */
public enum TunnelStatus implements IntEnumerable {

    /**
     * 初始化
     **/
    INIT(1),

    /**
     * 连接
     **/
    OPEN(2),

    /**
     * 挂起
     */
    SUSPEND(3),

    /**
     * 关闭
     **/
    CLOSED(4);

    //

    private final int id;

    TunnelStatus(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return this.id;
    }

}
