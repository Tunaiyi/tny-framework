package com.tny.game.net.message;


import com.tny.game.common.utils.BytesAide;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public interface MessageAide {

    long MESSAGE_MASK_BIT_SIZE = 1;
    long SENDER_MESSAGE_ID_MASK = 1;
    long MESSAGE_ID_MASK = Long.MIN_VALUE >>> (64 - SENDER_MESSAGE_ID_MASK);

    long PUSH_TO_MESSAGE_ID = 1;
    long REQUEST_TO_MESSAGE_ID = 0;
    long RESPONSE_TO_MESSAGE_MIN_ID = 1 << MESSAGE_MASK_BIT_SIZE;

    long MESSAGE_MAX_ID = Long.MAX_VALUE >> MESSAGE_MASK_BIT_SIZE;

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
        return toMessage >> MESSAGE_MASK_BIT_SIZE > 0;
    }

    static boolean isPush(long toMessage) {
        return toMessage <= PUSH_TO_MESSAGE_ID;
    }

    static boolean isRequest(long toMessage) {
        return REQUEST_TO_MESSAGE_ID == toMessage;
    }

    static void main(String[] args) {
        System.out.println(BytesAide.toBinaryString(BytesAide.long2Bytes(-1)));
        System.out.println(BytesAide.toBinaryString(BytesAide.long2Bytes(SENDER_MESSAGE_ID_MASK)));
        System.out.println(BytesAide.toBinaryString(BytesAide.long2Bytes((byte) (1 << 3))));
        System.out.println(RESPONSE_TO_MESSAGE_MIN_ID);
    }

}
