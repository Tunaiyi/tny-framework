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
