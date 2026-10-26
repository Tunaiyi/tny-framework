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

import com.tny.game.common.result.*;
import com.tny.game.net.application.*;

/**
 * 验证异常
 * <p>
 *
 * @date Kun Yang
 * @date 2022/4/8 04:37
 **/
public class AuthFailedException extends NetCheckException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final ResultCode CODE = NetResultCode.AUTH_FAIL_ERROR;

    public AuthFailedException(Throwable cause) {
        super(CODE, cause);
    }

    public AuthFailedException(String message, Object... messageParams) {
        super(CODE, message, messageParams);
    }

    public AuthFailedException(Throwable cause, String message, Object... messageParams) {
        super(CODE, cause, message, messageParams);
    }

    public AuthFailedException(ResultCode code) {
        super(code);
    }

    public AuthFailedException(ResultCode code, String message, Object... messageParams) {
        super(code, message, messageParams);
    }

    public AuthFailedException(ResultCode code, Throwable cause) {
        super(code, cause);
    }

    public AuthFailedException(ResultCode code, Throwable cause, String message, Object... messageParams) {
        super(code, cause, message, messageParams);
    }

}
