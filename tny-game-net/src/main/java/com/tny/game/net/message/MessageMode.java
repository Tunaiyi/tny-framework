package com.tny.game.net.message;

import com.tny.game.net.message.coder.*;

public enum MessageMode {

    /**
     * 处理推送
     */
    PUSH(MessageType.MESSAGE, CodecConstants.MESSAGE_HEAD_OPTION_MODE_VALUE_PUSH),

    /**
     * 处理请求
     */
    REQUEST(MessageType.MESSAGE, CodecConstants.MESSAGE_HEAD_OPTION_MODE_VALUE_REQUEST),

    /**
     * 处理响应
     */
    RESPONSE(MessageType.MESSAGE, CodecConstants.MESSAGE_HEAD_OPTION_MODE_VALUE_RESPONSE),

    /**
     * PING
     */
    PING(MessageType.PING, CodecConstants.MESSAGE_HEAD_OPTION_MODE_VALUE_EMPTY),

    /**
     * PONG
     */
    PONG(MessageType.PONE, CodecConstants.MESSAGE_HEAD_OPTION_MODE_VALUE_EMPTY),

    //
    ;

    private MessageType type;

    private byte option;

    MessageMode(MessageType type, byte option) {
        this.type = type;
        this.option = option;
    }

    public MessageType getType() {
        return this.type;
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

}
