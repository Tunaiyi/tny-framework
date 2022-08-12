/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.message;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2019-03-22 17:27
 */
public enum MessageType {

    MESSAGE(CodecConstants.DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_MESSAGE),

    PING(CodecConstants.DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_PING),

    PONE(CodecConstants.DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_PONG),

    //
    ;

    byte option;

    MessageType(byte packageOptionMark) {
        this.option = packageOptionMark;
    }

    public byte getOption() {
        return this.option;
    }

}
