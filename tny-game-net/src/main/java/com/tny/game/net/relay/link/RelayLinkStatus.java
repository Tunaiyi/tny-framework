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

import com.tny.game.common.enums.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/2 3:36 下午
 */
public enum RelayLinkStatus implements IntEnumerable {

    /**
     * 初始化
     **/
    INIT(1, false),
    /**
     * 打开
     **/
    OPEN(2, false),
    /**
     * 打开
     **/
    DISCONNECT(3, false),
    /**
     * 关闭中
     */
    CLOSING(4, true),
    /**
     * 关闭
     **/
    CLOSED(5, true);
    //
    ;

    private final int id;

    private final boolean closeStatus;

    RelayLinkStatus(int id, boolean closeStatus) {
        this.id = id;
        this.closeStatus = closeStatus;
    }

    @Override
    public int id() {
        return this.id;
    }

    public boolean isCloseStatus() {
        return this.closeStatus;
    }
}
