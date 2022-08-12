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

import com.tny.game.common.result.*;

public class CommandTimeoutException extends CommandException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CommandTimeoutException(ResultCode code, String message) {
        this(code, message, null);
    }

    public CommandTimeoutException(ResultCode code) {
        this(code, code.getMessage(), null);
    }

    public CommandTimeoutException(ResultCode code, Throwable cause) {
        this(code, code.getMessage(), cause);
    }

    public CommandTimeoutException(ResultCode code, String message, Throwable cause) {
        this(code, null, null, cause);
    }

    public CommandTimeoutException(ResultCode code, Object body) {
        this(code, null, body, null);
    }

    public CommandTimeoutException(ResultCode code, String message, Object body) {
        this(code, message, body, null);
    }

    public CommandTimeoutException(ResultCode code, String message, Object body, Throwable cause) {
        super(code, message, body, cause);
    }

}
