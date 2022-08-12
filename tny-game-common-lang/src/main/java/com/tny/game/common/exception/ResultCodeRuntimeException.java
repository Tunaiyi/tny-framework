/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.exception;

import com.tny.game.common.result.*;

/**
 * <p>
 *
 * @author Kun Yang
 */
public class ResultCodeRuntimeException extends CommonRuntimeException {

    private final ResultCode code;

    public ResultCodeRuntimeException(ResultCode code) {
        super();
        this.code = code;
    }

    public ResultCodeRuntimeException(ResultCode code, String message, Object... messageParams) {
        this(code, null, message, messageParams);
    }

    public ResultCodeRuntimeException(ResultCode code, Throwable cause) {
        this(code, cause, "");
    }

    public ResultCodeRuntimeException(ResultCode code, Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
        this.code = code;
    }

    public ResultCode getCode() {
        return code;
    }

}
