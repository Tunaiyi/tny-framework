/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.relay.exception;

import com.tny.game.common.result.*;
import com.tny.game.net.relay.link.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/4/30 4:14 下午
 */
public class RelayPacketHandleException extends RelayException {

    public RelayPacketHandleException(Throwable cause) {
        super(cause);
    }

    public RelayPacketHandleException(ResultCode code) {
        super(code);
    }

    public RelayPacketHandleException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public RelayPacketHandleException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

    public RelayPacketHandleException(ResultCode code, String message, Object... messageParams) {
        super(code, message, messageParams);
    }

    public RelayPacketHandleException(ResultCode code, Throwable cause) {
        super(code, cause);
    }

    public RelayPacketHandleException(ResultCode code, Throwable cause, String message, Object... messageParams) {
        super(code, cause, message, messageParams);
    }

}
