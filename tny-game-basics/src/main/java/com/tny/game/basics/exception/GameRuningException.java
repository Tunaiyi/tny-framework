/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
