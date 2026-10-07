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
