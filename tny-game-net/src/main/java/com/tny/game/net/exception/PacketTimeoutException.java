/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.exception;

import com.tny.game.net.base.*;

public class PacketTimeoutException extends CommandException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public PacketTimeoutException() {
        super(NetResultCode.DECODE_ERROR);
    }

    public PacketTimeoutException(String message) {
        super(NetResultCode.DECODE_ERROR, message);
    }

    public PacketTimeoutException(String message, Throwable cause) {
        super(NetResultCode.DECODE_ERROR, message, cause);
    }

    public PacketTimeoutException(Throwable cause) {
        super(NetResultCode.DECODE_ERROR, cause);
    }

}
