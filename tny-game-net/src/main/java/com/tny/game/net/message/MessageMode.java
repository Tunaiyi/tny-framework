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
package com.tny.game.net.message;

public enum MessageMode {

    /**
     * 处理推送
     */
    PUSH(MessageType.MESSAGE, CodecConstants.MESSAGE_HEAD_OPTION_MODE_VALUE_PUSH, NetworkWay.MESSAGE),
    /**
     * 处理请求
     */
    REQUEST(MessageType.MESSAGE, CodecConstants.MESSAGE_HEAD_OPTION_MODE_VALUE_REQUEST, NetworkWay.MESSAGE),
    /**
     * 处理响应
     */
    RESPONSE(MessageType.MESSAGE, CodecConstants.MESSAGE_HEAD_OPTION_MODE_VALUE_RESPONSE, NetworkWay.MESSAGE),
    /**
     * PING
     */
    PING(MessageType.PING, CodecConstants.MESSAGE_HEAD_OPTION_MODE_VALUE_PING, NetworkWay.HEARTBEAT),
    /**
     * PONG
     */
    PONG(MessageType.PONE, CodecConstants.MESSAGE_HEAD_OPTION_MODE_VALUE_PONG, NetworkWay.HEARTBEAT),
    //
    ;

    private final MessageType type;

    private final NetworkWay way;

    private final byte option;

    private final String mark;

    MessageMode(MessageType type, byte option, NetworkWay way) {
        this.type = type;
        this.option = option;
        this.way = way;
        this.mark = name().toLowerCase();
    }

    public MessageType getType() {
        return this.type;
    }

    public NetworkWay getWay() {
        return way;
    }

    public byte getOption() {
        return this.option;
    }

    public static MessageMode valueOf(MessageType type, byte option) {
        for (MessageMode mode : MessageMode.values()) {
            if (mode.getType() == type && mode.option == option) {
                return mode;
            }
        }
        return null;
    }

    public String getMark() {
        return mark;
    }
}
