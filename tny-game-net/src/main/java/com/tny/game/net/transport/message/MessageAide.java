package com.tny.game.net.transport.message;


/**
 * Created by Kun Yang on 2017/3/18.
 */
public interface MessageAide {

    long PUSH_TO_MESSAGE_MAX_ID = -1;

    long REQUEST_TO_MESSAGE_ID = 0;
    long REQUEST_TO_MESSAGE_MIN_ID = REQUEST_TO_MESSAGE_ID;
    long REQUEST_TO_MESSAGE_MAX_ID = REQUEST_TO_MESSAGE_ID;

    long RESPONSE_TO_MESSAGE_MIN_ID = 1;

    static boolean isResponse(MessageHeader header) {
        return isResponse(header.getToMessage());
    }

    static boolean isPush(MessageHeader header) {
        return isPush(header.getToMessage());
    }

    static boolean isRequest(MessageHeader header) {
        return isRequest(header.getToMessage());
    }
    static boolean isResponse(long toMessage) {
        return RESPONSE_TO_MESSAGE_MIN_ID <= toMessage;
    }

    static boolean isPush(long toMessage) {
        return toMessage <= PUSH_TO_MESSAGE_MAX_ID;
    }

    static boolean isRequest(long toMessage) {
        return REQUEST_TO_MESSAGE_MIN_ID <= toMessage && toMessage <= REQUEST_TO_MESSAGE_MAX_ID;
    }

}
