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

import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;

public class NetException extends ResultCodeRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Throwable VOID_EXCEPTION = null;

    private static final Object EMPTY_BODY = null;

    private static final ResultCode CODE = NetResultCode.SERVER_ERROR;

    private final Object body;

    public NetException(Throwable cause) {
        this(CODE, CODE.getMessage(), cause);
    }

    public NetException(ResultCode code) {
        this(code, EMPTY_BODY, VOID_EXCEPTION, code.getMessage());
    }

    public NetException(Object body) {
        this(CODE, body, VOID_EXCEPTION, CODE.getMessage());
    }

    public NetException(String message, Object... messageParams) {
        this(CODE, EMPTY_BODY, VOID_EXCEPTION, message, messageParams);
    }

    public NetException(Throwable cause, String message, Object... messageParams) {
        this(CODE, EMPTY_BODY, cause, message, messageParams);
    }

    public NetException(Object body, String message, Object... messageParams) {
        this(CODE, body, VOID_EXCEPTION, message, messageParams);
    }

    public NetException(Object body, Throwable cause, String message, Object... messageParams) {
        this(CODE, body, cause, message, messageParams);
    }

    public NetException(ResultCode code, String message, Object... messageParams) {
        this(code, EMPTY_BODY, VOID_EXCEPTION, message, messageParams);
    }

    public NetException(ResultCode code, Throwable cause) {
        this(code, EMPTY_BODY, cause, code.getMessage());
    }

    public NetException(ResultCode code, Throwable cause, String message, Object... messageParams) {
        this(code, EMPTY_BODY, cause, message, messageParams);
    }

    public NetException(ResultCode code, Object body) {
        this(code, body, code.getMessage());
    }

    public NetException(ResultCode code, Object body, String message, Object... messageParams) {
        this(code, body, VOID_EXCEPTION, message, messageParams);
    }

    public NetException(ResultCode code, Object body, Throwable cause, String message, Object... messageParams) {
        super(code, cause, message, messageParams);
        this.body = body;
    }

    public Object getBody() {
        return body;
    }

}
