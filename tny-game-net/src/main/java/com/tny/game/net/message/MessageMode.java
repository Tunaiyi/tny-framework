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
