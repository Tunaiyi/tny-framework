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
package com.tny.game.net.exception;

import com.tny.game.common.result.*;
import com.tny.game.net.application.*;

public class TunnelConnectException extends TunnelException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final ResultCode CODE = NetResultCode.CONNECT_FAILED;

    public TunnelConnectException(Throwable cause) {
        super(CODE, cause);
    }

    public TunnelConnectException(ResultCode code) {
        super(code);
    }

    public TunnelConnectException(String message, Object... messageParams) {
        super(CODE, message, messageParams);
    }

    public TunnelConnectException(Throwable cause, String message, Object... messageParams) {
        super(CODE, cause, message, messageParams);
    }

    public TunnelConnectException(ResultCode code, String message, Object... messageParams) {
        super(code, message, messageParams);
    }

    public TunnelConnectException(ResultCode code, Throwable cause) {
        super(code, cause);
    }

    public TunnelConnectException(ResultCode code, Throwable cause, String message, Object... messageParams) {
        super(code, cause, message, messageParams);
    }

}
