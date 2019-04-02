package com.tny.game.net.message;

import com.tny.game.net.message.coder.*;

public enum MessageMode {

    /**
     * 处理推送
     */
    PUSH(MessageType.MESSAGE, CodecContent.HEAD_OPTION_PUSH),

    /**
     * 处理请求
     */
    REQUEST(MessageType.MESSAGE, CodecContent.HEAD_OPTION_REQUEST),

    /**
     * 处理响应
     */
    RESPONSE(MessageType.MESSAGE, CodecContent.HEAD_OPTION_RESPONSE),

    /**
     * PING
     */
    PING(MessageType.PING, CodecContent.HEAD_OPTION_EMPTY),


    /**
     * PONG
     */
    PONG(MessageType.PONE, CodecContent.HEAD_OPTION_EMPTY),

    //
    ;

    private MessageType type;

    private byte option;


    MessageMode(MessageType type, byte option) {
        this.type = type;
        this.option = option;
    }

    public MessageType getType() {
        return type;
    }

    public byte getOption() {
        return option;
    }

    public static MessageMode valueOf(MessageType type, byte option) {
        for (MessageMode mode : MessageMode.values()) {
            if (mode.getType() == type && mode.option == option)
                return mode;
        }
        return null;
    }

}
