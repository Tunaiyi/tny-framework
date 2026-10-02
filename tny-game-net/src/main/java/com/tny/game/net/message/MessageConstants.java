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

/**
 * Created by Kun Yang on 2017/3/18.
 */
public interface MessageConstants {

    // long MESSAGE_MASK_BIT_SIZE = 1;
    long SENDER_MESSAGE_ID_MASK = 1;
    long MESSAGE_ID_MASK = Long.MIN_VALUE >>> (64 - SENDER_MESSAGE_ID_MASK);

    // long PUSH_TO_MESSAGE_ID = 1;
    long EMPTY_MESSAGE_ID = 0;
    long RESPONSE_TO_MESSAGE_MIN_ID = 1;
    long MESSAGE_MAX_ID = Long.MAX_VALUE;

    // static boolean isResponse(MessageHeader header) {
    //     return isResponse(header.getToMessage());
    // }
    //
    // static boolean isPush(MessageHeader header) {
    //     return isPush(header.getToMessage());
    // }
    //
    // static boolean isRequest(MessageHeader header) {
    //     return isRequest(header.getToMessage());
    // }
    //
    // static boolean isResponse(long toMessage) {
    //     return toMessage >> MESSAGE_MASK_BIT_SIZE > 0;
    // }
    //
    // static boolean isPush(long toMessage) {
    //     return toMessage <= PUSH_TO_MESSAGE_ID;
    // }
    //
    // static boolean isRequest(long toMessage) {
    //     return EMPTY_MESSAGE_ID == toMessage;
    // }

    // static void main(String[] args) {
    //     System.out.println(BytesAide.toBinaryString(BytesAide.long2Bytes(-1)));
    //     System.out.println(BytesAide.toBinaryString(BytesAide.long2Bytes(SENDER_MESSAGE_ID_MASK)));
    //     System.out.println(BytesAide.toBinaryString(BytesAide.long2Bytes((byte) (1 << 3))));
    //     System.out.println(RESPONSE_TO_MESSAGE_MIN_ID);
    // }

}
