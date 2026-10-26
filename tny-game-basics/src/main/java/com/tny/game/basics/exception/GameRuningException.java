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

package com.tny.game.basics.exception;

import com.tny.game.common.result.*;

public class GameRuningException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private ResultCode resultCode;

    /**
     * 附加对象
     */
    private Object data;

    public GameRuningException(ResultCode code, Object... messages) {
        super(format(null, code, messages));
        this.resultCode = code;
        this.data = null;
    }

    public GameRuningException(Object data, ResultCode code, Object... messages) {
        super(format(data, code, messages));
        this.resultCode = code;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    protected static String format(Object data, ResultCode code, Object... messages) {
        String exMessage = "\n###CODE : " + code.getCode() + "\n###MESSAGE : " + code.getMessage() + "\n###NFO : ";
        if (data != null) {
            exMessage += "\n   ---  " + data;
        }
        for (Object message : messages)
            exMessage += "\n   ---  " + message;
        return exMessage;
    }

}
