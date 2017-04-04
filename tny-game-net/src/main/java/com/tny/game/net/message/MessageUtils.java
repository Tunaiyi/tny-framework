package com.tny.game.net.message;


/**
 * Created by Kun Yang on 2017/3/18.
 */
public interface MessageUtils {

    int PUSH_TO_MESSAGE_MIN_ID = Integer.MIN_VALUE;
    int PUSH_TO_MESSAGE_MAX_ID = -1;

    int REQUEST_TO_MESSAGE_MIN_ID = 0;
    int REQUEST_TO_MESSAGE_MAX_ID = 0;

    int RESPONSE_TO_MESSAGE_MIN_ID = 1;
    int RESPONSE_TO_MESSAGE_MAX_ID = Integer.MAX_VALUE;

    static boolean isResponse(Message<?> message) {
        return isResponse(message.getToMessage());
    }

    static boolean isPush(Message<?> message) {
        return isPush(message.getToMessage());
    }

    static boolean isRequest(Message<?> message) {
        return isRequest(message.getToMessage());
    }
    static boolean isResponse(int toMessage) {
        return RESPONSE_TO_MESSAGE_MIN_ID <= toMessage && toMessage <= RESPONSE_TO_MESSAGE_MAX_ID;
    }

    static boolean isPush(int toMessage) {
        return PUSH_TO_MESSAGE_MIN_ID <= toMessage && toMessage <= PUSH_TO_MESSAGE_MAX_ID;
    }

    static boolean isRequest(int toMessage) {
        return REQUEST_TO_MESSAGE_MIN_ID <= toMessage && toMessage <= REQUEST_TO_MESSAGE_MAX_ID;
    }

}
