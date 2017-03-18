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
        return RESPONSE_TO_MESSAGE_MIN_ID <= message.getToMessage() && message.getToMessage() <= RESPONSE_TO_MESSAGE_MAX_ID;
    }

    static boolean isPush(Message<?> message) {
        return PUSH_TO_MESSAGE_MIN_ID <= message.getToMessage() && message.getToMessage() <= PUSH_TO_MESSAGE_MAX_ID;
    }

    static boolean isRequest(Message<?> message) {
        return REQUEST_TO_MESSAGE_MIN_ID <= message.getToMessage() && message.getToMessage() <= REQUEST_TO_MESSAGE_MAX_ID;
    }

}
