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

package com.tny.game.net.transport;

import com.tny.game.common.enums.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-08 11:49
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
