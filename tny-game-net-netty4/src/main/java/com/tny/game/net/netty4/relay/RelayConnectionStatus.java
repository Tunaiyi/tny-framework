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

package com.tny.game.net.netty4.relay;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 9:10 下午
 */
enum RelayConnectionStatus {

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

    RelayConnectionStatus(boolean canConnect) {
        this.canConnect = canConnect;
    }

    boolean isCanConnect() {
        return canConnect;
    }
}
