/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
