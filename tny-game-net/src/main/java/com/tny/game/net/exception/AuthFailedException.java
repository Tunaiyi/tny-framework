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
import com.tny.game.net.base.*;

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
