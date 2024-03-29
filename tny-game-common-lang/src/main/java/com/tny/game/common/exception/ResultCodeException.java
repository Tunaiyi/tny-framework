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
package com.tny.game.common.exception;

import com.tny.game.common.result.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 */
public class ResultCodeException extends CommonException implements ResultCodableException {

    private final ResultCode code;

    public ResultCodeException(ResultCode code) {
        super();
        this.code = code;
    }

    public ResultCodeException(ResultCode code, String message, Object... messageParams) {
        this(code, null, message, messageParams);
    }

    public ResultCodeException(ResultCode code, Throwable cause) {
        this(code, cause, "");
    }

    public ResultCodeException(ResultCode code, Throwable cause, String message, Object... messageParams) {
        super(format(message, messageParams), cause);
        this.code = code;
    }

    @Override
    public ResultCode getCode() {
        return code;
    }

}
