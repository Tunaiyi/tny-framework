package com.tny.game.net.message;

import com.tny.game.net.message.coder.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2019-03-22 17:27
 */
public enum MessageType {

    MESSAGE(CodecContent.DATA_PACK_OPTION_MESSAGE),

    PING(CodecContent.DATA_PACK_OPTION_PING),

    PONE(CodecContent.DATA_PACK_OPTION_PONG),

    ;

    byte option;

    MessageType(byte packageOptionMark) {
        this.option = packageOptionMark;
    }

    public byte getOption() {
        return option;
    }

}
