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

public class CommandException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private ResultCode resultCode;

    private Object body;

    public CommandException(ResultCode code, String message) {
        this(code, message, null);
    }

    public CommandException(ResultCode code) {
        this(code, code.getMessage(), null);
    }

    public CommandException(ResultCode code, Throwable cause) {
        this(code, code.getMessage(), cause);
    }

    public CommandException(ResultCode code, String message, Throwable cause) {
        this(code, message, null, cause);
    }

    public CommandException(ResultCode code, Object body) {
        this(code, null, body, null);
    }

    public CommandException(ResultCode code, String message, Object body) {
        this(code, message, body, null);
    }

    public CommandException(ResultCode code, String message, Object body, Throwable cause) {
        super(message, cause);
        this.resultCode = code;
        this.body = body;
    }

    public ResultCode getResultCode() {
        return this.resultCode;
    }

    public Object getBody() {
        return body;
    }

}
