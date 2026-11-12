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
package com.tny.game.net.exception;

import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;
import com.tny.game.net.application.*;

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
