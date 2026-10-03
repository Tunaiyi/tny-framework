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

package com.tny.game.net.message;

public interface Protocol {

    int PING_PONG_PROTOCOL_NUM = -1;

    /**
     * @return 协议号
     */
    int getProtocolId();

    /**
     * @return 获取信道号
     */
    int getLine();

    /**
     * 指定消息是否是属于此协议
     *
     * @param protocol 消息头
     */
    default boolean isOwn(Protocol protocol) {
        return this.getProtocolId() == protocol.getProtocolId();
    }

}
